package com.reign.framework.netty.util;

import org.apache.commons.logging.*;
import org.jboss.netty.handler.codec.http.*;
import java.text.*;
import java.util.*;

public class CookieDecoder
{
    private static final Log log;
    private static final String COMMA = ",";
    
    static {
        log = LogFactory.getLog(CookieDecoder.class);
    }
    
    public Set<Cookie> decode(final String header) {
        final List<String> names = new ArrayList<String>(8);
        final List<String> values = new ArrayList<String>(8);
        this.extractKeyValuePairs(header, names, values);
        if (names.isEmpty()) {
            return Collections.emptySet();
        }
        int version = 0;
        int i;
        if (names.get(0).equalsIgnoreCase("Version")) {
            try {
                version = Integer.parseInt(values.get(0));
            }
            catch (NumberFormatException ex) {}
            i = 1;
        }
        else {
            i = 0;
        }
        if (names.size() <= i) {
            return Collections.emptySet();
        }
        final Set<Cookie> cookies = new TreeSet<Cookie>();
        while (i < names.size()) {
            String name = names.get(i);
            String value = values.get(i);
            if (value == null) {
                value = "";
            }
            Cookie c = null;
            Label_0824: {
                try {
                    c = new DefaultCookie(name, value);
                    cookies.add(c);
                }
                catch (Exception e) {
                    CookieDecoder.log.warn("parse cookie error, [name: " + name + ", value: " + value + ", header: " + header, e);
                    break Label_0824;
                }
                boolean discard = false;
                boolean secure = false;
                boolean httpOnly = false;
                String comment = null;
                String commentURL = null;
                String domain = null;
                String path = null;
                int maxAge = -1;
                final List<Integer> ports = new ArrayList<Integer>(2);
                for (int j = i + 1; j < names.size(); ++j, ++i) {
                    name = names.get(j);
                    value = values.get(j);
                    if ("Discard".equalsIgnoreCase(name)) {
                        discard = true;
                    }
                    else if ("Secure".equalsIgnoreCase(name)) {
                        secure = true;
                    }
                    else if ("HTTPOnly".equalsIgnoreCase(name)) {
                        httpOnly = true;
                    }
                    else if ("Comment".equalsIgnoreCase(name)) {
                        comment = value;
                    }
                    else if ("CommentURL".equalsIgnoreCase(name)) {
                        commentURL = value;
                    }
                    else if ("Domain".equalsIgnoreCase(name)) {
                        domain = value;
                    }
                    else if ("Path".equalsIgnoreCase(name)) {
                        path = value;
                    }
                    else if ("Expires".equalsIgnoreCase(name)) {
                        try {
                            final long maxAgeMillis = new CookieDateFormat().parse(value).getTime() - System.currentTimeMillis();
                            if (maxAgeMillis <= 0L) {
                                maxAge = 0;
                            }
                            else {
                                maxAge = (int)(maxAgeMillis / 1000L) + ((maxAgeMillis % 1000L != 0L) ? 1 : 0);
                            }
                        }
                        catch (ParseException ex2) {}
                    }
                    else if ("Max-Age".equalsIgnoreCase(name)) {
                        try {
                            maxAge = Integer.parseInt(value);
                        }
                        catch (NumberFormatException e2) {
                            CookieDecoder.log.warn("parse cookie error, [name: " + name + ", value: " + value + ", header: " + header, e2);
                        }
                    }
                    else if ("Version".equalsIgnoreCase(name)) {
                        try {
                            version = Integer.parseInt(value);
                        }
                        catch (NumberFormatException e2) {
                            CookieDecoder.log.warn("parse cookie error, [name: " + name + ", value: " + value + ", header: " + header, e2);
                        }
                    }
                    else {
                        if (!"Port".equalsIgnoreCase(name)) {
                            break;
                        }
                        final String[] portList = value.split(",");
                        String[] array;
                        for (int length = (array = portList).length, k = 0; k < length; ++k) {
                            final String s1 = array[k];
                            try {
                                ports.add(Integer.valueOf(s1));
                            }
                            catch (NumberFormatException ex3) {}
                        }
                    }
                }
                c.setVersion(version);
                c.setMaxAge(maxAge);
                c.setPath(path);
                c.setDomain(domain);
                c.setSecure(secure);
                c.setHttpOnly(httpOnly);
                if (version > 0) {
                    c.setComment(comment);
                }
                if (version > 1) {
                    c.setCommentUrl(commentURL);
                    c.setPorts(ports);
                    c.setDiscard(discard);
                }
            }
            ++i;
        }
        return cookies;
    }
    
    private void extractKeyValuePairs(final String header, final List<String> names, final List<String> values) {
        final int headerLen = header.length();
        int i = 0;
    Label_0009:
        while (true) {
            while (i != headerLen) {
                switch (header.charAt(i)) {
                    case '\t':
                    case '\n':
                    case '\u000b':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ';': {
                        ++i;
                        continue;
                    }
                    default: {
                        while (i != headerLen) {
                            if (header.charAt(i) != '$') {
                                String name = null;
                                String value = null;
                                Label_0497: {
                                    if (i == headerLen) {
                                        name = null;
                                        value = null;
                                    }
                                    else {
                                        final int newNameStart = i;
                                        do {
                                            switch (header.charAt(i)) {
                                                case ';': {
                                                    name = header.substring(newNameStart, i);
                                                    value = null;
                                                    break Label_0497;
                                                }
                                                case '=': {
                                                    name = header.substring(newNameStart, i);
                                                    if (++i == headerLen) {
                                                        value = "";
                                                        break Label_0497;
                                                    }
                                                    final int newValueStart = i;
                                                    char c = header.charAt(i);
                                                    if (c == '\"' || c == '\'') {
                                                        final StringBuilder newValueBuf = new StringBuilder(header.length() - i);
                                                        final char q = c;
                                                        boolean hadBackslash = false;
                                                        ++i;
                                                        while (i != headerLen) {
                                                            if (hadBackslash) {
                                                                hadBackslash = false;
                                                                c = header.charAt(i++);
                                                                switch (c) {
                                                                    case '\"':
                                                                    case '\'':
                                                                    case '\\': {
                                                                        newValueBuf.setCharAt(newValueBuf.length() - 1, c);
                                                                        continue;
                                                                    }
                                                                    default: {
                                                                        newValueBuf.append(c);
                                                                        continue;
                                                                    }
                                                                }
                                                            }
                                                            else {
                                                                c = header.charAt(i++);
                                                                if (c == q) {
                                                                    value = newValueBuf.toString();
                                                                    break Label_0497;
                                                                }
                                                                newValueBuf.append(c);
                                                                if (c != '\\') {
                                                                    continue;
                                                                }
                                                                hadBackslash = true;
                                                            }
                                                        }
                                                        value = newValueBuf.toString();
                                                        break Label_0497;
                                                    }
                                                    final int semiPos = header.indexOf(59, i);
                                                    if (semiPos > 0) {
                                                        value = header.substring(newValueStart, semiPos);
                                                        i = semiPos;
                                                        break Label_0497;
                                                    }
                                                    value = header.substring(newValueStart);
                                                    i = headerLen;
                                                    break Label_0497;
                                                }
                                                default: {
                                                    continue;
                                                }
                                            }
                                        } while (++i != headerLen);
                                        name = header.substring(newNameStart);
                                        value = null;
                                    }
                                }
                                names.add(name);
                                values.add(value);
                                continue Label_0009;
                            }
                            ++i;
                        }
                    }
                }
            }
            break;
        }
    }
    
    private String decodeValue(final String value) {
        if (value == null) {
            return value;
        }
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }
    
    final class CookieDateFormat extends SimpleDateFormat
    {
        private static final long serialVersionUID = 1789486337887402640L;
        
        CookieDateFormat() {
            super("E, d-MMM-y HH:mm:ss z", Locale.ENGLISH);
            this.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }
}
