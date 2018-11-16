package com.reign.framework.netty.servlet;

import org.apache.commons.logging.*;
import org.jboss.netty.buffer.*;
import java.util.*;
import java.util.concurrent.*;
import com.reign.framework.netty.util.*;
import java.security.*;

public final class SessionManager
{
    private static final Log log;
    private static final SessionManager instance;
    protected int duplicates;
    protected final List<SessionListener> sessionListeners;
    protected final List<SessionAttributeListener> sessionAttributeListeners;
    protected final ConcurrentMap<String, Session> sessions;
    protected volatile MessageDigest digest;
    protected Random random;
    protected String entropy;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private boolean checkThreadStarted;
    private ServletConfig sc;
    private final ChannelBuffer EMPTY_BUFFER;
    
    static {
        log = LogFactory.getLog(SessionManager.class);
        instance = new SessionManager();
    }
    
    private SessionManager() {
        this.duplicates = 0;
        this.sessionListeners = new ArrayList<SessionListener>();
        this.sessionAttributeListeners = new ArrayList<SessionAttributeListener>();
        this.sessions = new ConcurrentHashMap<String, Session>();
        this.checkThreadStarted = false;
        this.EMPTY_BUFFER = ChannelBuffers.wrappedBuffer("".getBytes());
    }
    
    public static SessionManager getInstance() {
        return SessionManager.instance;
    }
    
    public void startSessionCheckThread() {
        if (this.checkThreadStarted) {
            return;
        }
        synchronized (this) {
            if (!this.checkThreadStarted) {
                this.checkThreadStarted = true;
                (this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1)).scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        if (SessionManager.log.isDebugEnabled()) {
                            SessionManager.log.debug("start check session");
                        }
                        try {
                            final Set<Map.Entry<String, Session>> entrySet = SessionManager.this.sessions.entrySet();
                            for (final Map.Entry<String, Session> entry : entrySet) {
                                final Session session = entry.getValue();
                                if (!session.isValid()) {
                                    if (SessionManager.log.isDebugEnabled()) {
                                        SessionManager.log.debug("clear session id:" + session.getId());
                                    }
                                    session.invalidate();
                                }
                                else {
                                    if (session.getResponse() == null) {
                                        continue;
                                    }
                                    session.write(SessionManager.this.EMPTY_BUFFER);
                                }
                            }
                        }
                        catch (Exception e) {
                            SessionManager.log.error("clear session", e);
                        }
                        if (SessionManager.log.isDebugEnabled()) {
                            SessionManager.log.debug("end check session");
                        }
                    }
                }, this.sc.getSessionTickTime(), this.sc.getSessionTickTime(), TimeUnit.SECONDS);
            }
        }
    }
    
    public void access(final String sessionId) {
        if (sessionId == null) {
            return;
        }
        final Session session = this.getSession(sessionId);
        if (session != null) {
            session.access();
        }
    }
    
    public Session getSession(final String sessionId) {
        return this.getSession(sessionId, false);
    }
    
    public Session getSession(String sessionId, final boolean allowCreate) {
        if (allowCreate) {
            Session session = (sessionId == null) ? null : this._getSession(sessionId);
            if (session == null) {
                session = new StandardSession(this.generateSessionId(), this.sessionListeners, this.sessionAttributeListeners, this.sc);
                this.sessions.put(session.getId(), session);
                sessionId = session.getId();
            }
        }
        return (sessionId == null) ? null : this._getSession(sessionId);
    }
    
    private Session _getSession(final String sessionId) {
        final Session session = this.sessions.get(sessionId);
        return session;
    }
    
    public void addSessionListener(final SessionListener sessionListener) {
        this.sessionListeners.add(sessionListener);
    }
    
    public void addSessionAttributeListener(final SessionAttributeListener sessionAttributeListener) {
        this.sessionAttributeListeners.add(sessionAttributeListener);
    }
    
    public void setServletConfig(final ServletConfig sc) {
        this.sc = sc;
    }
    
    protected synchronized String generateSessionId() {
        byte[] random = new byte[16];
        final String jvmRoute = Utils.getJvmRoute();
        String result = null;
        StringBuilder builder = new StringBuilder();
        do {
            int resultLenBytes = 0;
            if (result != null) {
                builder = new StringBuilder();
                ++this.duplicates;
            }
            while (resultLenBytes < 16) {
                this.getRandomBytes(random);
                random = this.getMessageDigest().digest(random);
                for (int i = 0; i < random.length && resultLenBytes < 16; ++resultLenBytes, ++i) {
                    final byte b1 = (byte)((random[i] & 0xF0) >> 4);
                    final byte b2 = (byte)(random[i] & 0xF);
                    if (b1 < 10) {
                        builder.append((char)(48 + b1));
                    }
                    else {
                        builder.append((char)(65 + (b1 - 10)));
                    }
                    if (b2 < 10) {
                        builder.append((char)(48 + b2));
                    }
                    else {
                        builder.append((char)(65 + (b2 - 10)));
                    }
                }
            }
            if (jvmRoute != null) {
                builder.append('.').append(jvmRoute);
            }
            result = builder.toString();
        } while (this.sessions.containsKey(result));
        return result;
    }
    
    private MessageDigest getMessageDigest() {
        if (this.digest == null) {
            try {
                this.digest = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException ex) {}
        }
        return this.digest;
    }
    
    private void getRandomBytes(final byte[] bytes) {
        if (this.random == null) {
            long seed = System.currentTimeMillis();
            final char[] entropy = this.getEntropy().toCharArray();
            for (int i = 0; i < entropy.length; ++i) {
                final long update = (byte)entropy[i] << i % 8 * 8;
                seed ^= update;
            }
            (this.random = new Random()).setSeed(seed);
        }
        this.random.nextBytes(bytes);
    }
    
    private String getEntropy() {
        if (this.entropy == null) {
            this.entropy = this.toString();
        }
        return this.entropy;
    }
    
    public static void main(final String[] args) throws InstantiationException, IllegalAccessException {
    }
}
