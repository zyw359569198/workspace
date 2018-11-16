package com.reign.framework.netty.mvc.servlet;

import com.reign.framework.netty.mvc.*;
import com.reign.framework.netty.mvc.interceptor.*;
import org.apache.commons.logging.*;
import org.apache.commons.lang.*;
import com.reign.framework.exception.*;
import com.reign.framework.startup.*;
import java.net.*;
import com.reign.util.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.plugin.*;
import java.util.*;
import com.reign.framework.common.*;
import com.reign.framework.netty.mvc.view.*;
import com.reign.framework.netty.mvc.validation.*;
import com.reign.framework.netty.mvc.annotation.*;
import java.lang.reflect.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.mvc.spring.*;
import org.springframework.context.*;

public class DispatchServlet implements Servlet
{
    private static final Log log;
    private static final long serialVersionUID = -2495148947376229331L;
    protected static final String ACTION_SCAN_PATH = "actionPackage";
    protected static final String ACTION_INTERCEPTOR = "actionInterceptor";
    protected static final String ACTION_COMPRESS = "compress";
    protected static Map<String, ActionInvocation> handleMap;
    protected static ObjectFactory objectFactory;
    protected ServletConfig config;
    protected ServletContext context;
    protected boolean compress;
    protected static ResponseView DEFAULT_VIEW;
    protected static List<Interceptor> interceptors;
    
    static {
        log = LogFactory.getLog(DispatchServlet.class);
        DispatchServlet.handleMap = new HashMap<String, ActionInvocation>();
        DispatchServlet.DEFAULT_VIEW = new NullView();
        DispatchServlet.interceptors = new ArrayList<Interceptor>();
    }
    
    public DispatchServlet() {
        this.compress = false;
    }
    
    @Override
    public void init(final ServletConfig config, final ServletContext context) {
        this.config = config;
        this.context = context;
        DispatchServlet.log.info("Init Servlet Start");
        final String scanPath = (String)config.getInitParam("actionPackage");
        if (StringUtils.isBlank(scanPath)) {
            DispatchServlet.log.fatal("can't find actionPackage config");
            throw new ServletConfigException("can't find actionPackage config");
        }
        final StopWatch sw = StopWatch.begin();
        try {
            this.initCompress();
            this.initInterceptorList();
            this.initHandleAction(scanPath);
        }
        catch (Exception e) {
            DispatchServlet.log.fatal("", e);
            throw new ServletConfigException("", e);
        }
        sw.stop();
        this.context.setAttribute(ServletContext.ROOT_WEB_APPLICATION_SERVLET_ATTRIBUTE, this);
        DispatchServlet.log.info("Init Servlet Success in " + sw.getElapsedTime() + "ms");
    }
    
    private void initInterceptorList() throws ServletException {
        final String value = (String)this.config.getInitParam("actionInterceptor");
        if (StringUtils.isBlank(value)) {
            return;
        }
        final String[] classNames = value.split(",");
        String[] array;
        for (int length = (array = classNames).length, i = 0; i < length; ++i) {
            String className = array[i];
            try {
                className = trim(className);
                final Class<? extends Interceptor> clazz = (Class<? extends Interceptor>)Thread.currentThread().getContextClassLoader().loadClass(className);
                DispatchServlet.interceptors.add((Interceptor)this.createObject(clazz));
            }
            catch (Exception e) {
                throw new ServletException("unkonw interceptor " + className);
            }
        }
    }
    
    private static String trim(final String value) {
        return value.trim().replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
    }
    
    private void initCompress() {
        final String value = (String)this.config.getInitParam("compress");
        if (StringUtils.isBlank(value)) {
            return;
        }
        this.compress = Boolean.valueOf(value);
        WrapperUtil.compress = this.compress;
    }
    
    public void loadPlugin(final PluginInfo pluginInfo) throws Exception {
        final Enumeration<URL> urls = pluginInfo.getScanPackage();
        if (urls == null || !urls.hasMoreElements()) {
            return;
        }
        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            final String className = url.getFile();
            if (StringUtils.isNotBlank(className) && className.indexOf("$") == -1) {
                final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                final List<Tuple<String, ActionInvocation>> list = this.initHandleAction(clazz);
                if (list == null || list.size() <= 0) {
                    continue;
                }
                for (final Tuple<String, ActionInvocation> tuple : list) {
                    pluginInfo.addCommands(tuple.left);
                }
            }
        }
    }
    
    public void removePlugin(final PluginInfo pluginInfo) {
        if (pluginInfo.commands != null) {
            for (final String command : pluginInfo.commands) {
                DispatchServlet.handleMap.remove(command);
            }
        }
    }
    
    private void initHandleAction(final String scanPath) throws Exception {
        final Set<Class<?>> set = Scans.getClasses(scanPath);
        final Map<String, PluginInfo> pluginMap = PluginLoader.getInstance().getPlugins();
        for (final Map.Entry<String, PluginInfo> entry : pluginMap.entrySet()) {
            final PluginInfo pluginInfo = entry.getValue();
            final Enumeration<URL> urls = pluginInfo.getScanPackage();
            if (urls != null) {
                if (!urls.hasMoreElements()) {
                    continue;
                }
                while (urls.hasMoreElements()) {
                    final URL url = urls.nextElement();
                    final String className = url.getFile();
                    if (StringUtils.isNotBlank(className) && className.indexOf("$") == -1) {
                        final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                        set.add(clazz);
                    }
                }
            }
        }
        for (final Class<?> clazz2 : set) {
            this.initHandleAction(clazz2);
        }
    }
    
    private List<Tuple<String, ActionInvocation>> initHandleAction(final Class<?> clazz) throws Exception {
        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            return null;
        }
        final Action action = Lang.getAnnotation(clazz, Action.class);
        if (action != null) {
            final ViewManager vm = new ViewManager(DispatchServlet.DEFAULT_VIEW);
            final Views views = Lang.getAnnotation(clazz, Views.class);
            if (views != null) {
                View[] value;
                for (int length = (value = views.value()).length, i = 0; i < length; ++i) {
                    final View view = value[i];
                    vm.addView(view.name(), this.getView(view));
                }
            }
            else {
                final View view = Lang.getAnnotation(clazz, View.class);
                if (view != null) {
                    vm.addView(view.name(), this.getView(view));
                }
            }
            final List<Tuple<Validation, Rule<?>>> validationList = new ArrayList<Tuple<Validation, Rule<?>>>();
            final Validations validations = Lang.getAnnotation(clazz, Validations.class);
            if (validations != null) {
                com.reign.framework.netty.mvc.annotation.Validation[] value2;
                for (int length2 = (value2 = validations.value()).length, j = 0; j < length2; ++j) {
                    final com.reign.framework.netty.mvc.annotation.Validation validation = value2[j];
                    final Tuple<Validation, Rule<?>> tuple = (Tuple<Validation, Rule<?>>)new Tuple();
                    tuple.left = Lang.createObject(this.getServletContext(), validation.handler());
                    final com.reign.framework.netty.mvc.annotation.Rule ruleAnnotation = validation.rule();
                    final Rule<?> rule = (Rule<?>)Lang.createObject(ruleAnnotation.rule());
                    rule.parse(ruleAnnotation.expression());
                    tuple.right = rule;
                    validationList.add(tuple);
                }
            }
            else {
                final com.reign.framework.netty.mvc.annotation.Validation validationAnnotation = Lang.getAnnotation(clazz, com.reign.framework.netty.mvc.annotation.Validation.class);
                if (validationAnnotation != null) {
                    final Tuple<Validation, Rule<?>> tuple2 = (Tuple<Validation, Rule<?>>)new Tuple();
                    tuple2.left = Lang.createObject(this.getServletContext(), validationAnnotation.handler());
                    final com.reign.framework.netty.mvc.annotation.Rule ruleAnnotation2 = validationAnnotation.rule();
                    final Rule<?> rule2 = (Rule<?>)Lang.createObject(ruleAnnotation2.rule());
                    rule2.parse(ruleAnnotation2.expression());
                    tuple2.right = rule2;
                    validationList.add(tuple2);
                }
            }
            final Sync sync = Lang.getAnnotation(clazz, Sync.class);
            boolean isSync = false;
            if (sync != null && sync.value()) {
                isSync = true;
            }
            return this.createActionInvocation(clazz, vm, validationList, isSync);
        }
        return null;
    }
    
    private ResponseView getView(final View view) throws Exception {
        final Class<? extends ResponseView> viewType = view.type();
        final ResponseView hv = (ResponseView)this.createObject(viewType);
        final String value = view.compress();
        if ("true".equalsIgnoreCase(value)) {
            hv.setCompress(true);
        }
        else if ("false".equalsIgnoreCase(value)) {
            hv.setCompress(false);
        }
        else {
            hv.setCompress(this.compress);
        }
        return hv;
    }
    
    private List<Tuple<String, ActionInvocation>> createActionInvocation(final Class<?> clazz, final ViewManager vm, final List<Tuple<Validation, Rule<?>>> validationList, final boolean isSync) throws Exception {
        final List<Tuple<String, ActionInvocation>> list = new ArrayList<Tuple<String, ActionInvocation>>();
        final Object obj = Lang.createObject(this.getServletContext(), clazz);
        final Method[] methods = clazz.getDeclaredMethods();
        Method[] array;
        for (int length = (array = methods).length, i = 0; i < length; ++i) {
            final Method method = array[i];
            if (!Lang.isStaticMethod(method)) {
                final Command cmd = method.getAnnotation(Command.class);
                if (cmd != null) {
                    final ActionInvocation ai = this.createInvocationAction(obj, method, vm, validationList, isSync);
                    if (DispatchServlet.handleMap.containsKey(cmd.value())) {
                        throw new ServletConfigException("exists same command handler[command:" + cmd.value() + ", handler1:[" + DispatchServlet.handleMap.get(cmd.value()).toString() + "], " + "handler2:[" + ai.toString() + "]");
                    }
                    DispatchServlet.handleMap.put(cmd.value(), ai);
                    DispatchServlet.log.info("found command handler [command:" + cmd.value() + ", handler:" + ai.toString());
                    list.add(new Tuple(cmd.value(), ai));
                }
            }
        }
        return list;
    }
    
    private ActionInvocation createInvocationAction(final Object obj, final Method method, final ViewManager vm, final List<Tuple<Validation, Rule<?>>> validationList, final boolean isSync) throws Exception {
        final ActionInvocation invocation = new ActionInvocation(this.context, obj, method);
        invocation.setViewManager(vm);
        invocation.setValidationList(validationList);
        invocation.setSync(isSync);
        invocation.init();
        return invocation;
    }
    
    @Override
    public ServletConfig getServletConfig() {
        return this.config;
    }
    
    @Override
    public ServletContext getServletContext() {
        return this.context;
    }
    
    @Override
    public void service(final Request request, final Response response) {
        final ActionInvocation invocation = DispatchServlet.handleMap.get(request.getCommand());
        try {
            final Result<?> result = invocation.invoke(DispatchServlet.interceptors.iterator(), request, response);
            invocation.render(result, request, response);
            if (result == null) {
                DispatchServlet.log.warn("result is null, [command:" + request.getCommand() + ", className:" + invocation.getActionName() + ", methodName:" + invocation.getMethodName() + "]");
            }
        }
        catch (Throwable t) {
            DispatchServlet.log.error("handle command error, command: " + request.getCommand(), t);
            throw new RuntimeException(t);
        }
    }
    
    @Override
    public void destroy() {
    }
    
    private Object createObject(final Class<?> clazz) throws Exception {
        return this.getObjectFactory().buildBean(clazz);
    }
    
    private ObjectFactory getObjectFactory() {
        if (DispatchServlet.objectFactory == null) {
            final SpringObjectFactory factory = new SpringObjectFactory();
            final ApplicationContext applicationContext = (ApplicationContext)this.getServletContext().getAttribute(ServletContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            if (applicationContext == null) {
                DispatchServlet.log.info("ApplicationContext could not be found. Action classes will not be autowired");
                DispatchServlet.objectFactory = new ObjectFactory();
            }
            else {
                factory.setApplicationContext(applicationContext);
                DispatchServlet.objectFactory = factory;
            }
        }
        return DispatchServlet.objectFactory;
    }
}
