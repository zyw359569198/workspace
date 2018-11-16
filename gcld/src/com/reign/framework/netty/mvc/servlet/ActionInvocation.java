package com.reign.framework.netty.mvc.servlet;

import java.lang.reflect.*;
import com.reign.util.*;
import com.reign.framework.netty.mvc.validation.*;
import com.reign.framework.common.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.netty.mvc.view.*;
import com.reign.framework.netty.mvc.adaptor.*;
import java.util.*;
import com.reign.framework.netty.mvc.interceptor.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;

public class ActionInvocation
{
    public static final String EXCEPTION = "exception";
    protected Object obj;
    protected Method method;
    protected String actionName;
    protected String methodName;
    protected HttpAdaptor adaptor;
    protected ServletContext context;
    protected ViewManager vm;
    protected boolean sync;
    protected boolean needValidate;
    protected boolean isInChatTransactional;
    protected List<Tuple<Validation, Rule<?>>> validationList;
    
    public ActionInvocation(final ServletContext context, final Object obj, final Method method) {
        this.needValidate = false;
        this.isInChatTransactional = false;
        this.obj = obj;
        this.method = method;
        this.context = context;
        this.actionName = obj.getClass().getSimpleName();
        this.methodName = method.getName();
    }
    
    public void init() throws Exception {
        this.initAdaptor();
        this.initView();
        this.initSync();
        this.initValidation();
        this.initChatTransactional();
    }
    
    private void initChatTransactional() {
        if (this.method.getClass().getAnnotation(ChatTransactional.class) != null) {
            this.setIsInChatTransactional(true);
        }
        else if (this.method.getAnnotation(ChatTransactional.class) != null) {
            this.setIsInChatTransactional(true);
        }
    }
    
    private void initValidation() throws Exception {
        final List<Tuple<Validation, Rule<?>>> validationList = new ArrayList<Tuple<Validation, Rule<?>>>();
        final Validations validations = this.method.getAnnotation(Validations.class);
        if (validations != null) {
            com.reign.framework.netty.mvc.annotation.Validation[] value;
            for (int length = (value = validations.value()).length, i = 0; i < length; ++i) {
                final com.reign.framework.netty.mvc.annotation.Validation validation = value[i];
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
            final com.reign.framework.netty.mvc.annotation.Validation validationAnnotation = this.method.getAnnotation(com.reign.framework.netty.mvc.annotation.Validation.class);
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
        if (validationList.size() > 0) {
            this.setValidationList(validationList);
        }
    }
    
    private void initSync() {
        final Sync sync = this.method.getAnnotation(Sync.class);
        if (sync != null && sync.value()) {
            this.setSync(true);
        }
    }
    
    private void initView() throws Exception {
        final Views views = this.method.getAnnotation(Views.class);
        if (views != null) {
            View[] value;
            for (int length = (value = views.value()).length, i = 0; i < length; ++i) {
                final View view = value[i];
                this.vm.addView(view.name(), this.getView(view));
            }
        }
        else {
            final View view = this.method.getAnnotation(View.class);
            if (view != null) {
                this.vm.addView(view.name(), this.getView(view));
            }
        }
    }
    
    private ResponseView getView(final View view) throws Exception {
        final Class<? extends ResponseView> viewType = view.type();
        return (ResponseView)Lang.createObject(this.context, viewType);
    }
    
    private void initAdaptor() {
        (this.adaptor = new PairAdaptor()).init(this.method);
    }
    
    public Result<?> invoke(final Iterator<Interceptor> interceptors, final Request request, final Response response) throws Exception {
        if (this.isSync()) {
            synchronized (request.getSession()) {
                // monitorexit(request.getSession())
                return this._invoke(interceptors, request, response);
            }
        }
        return this._invoke(interceptors, request, response);
    }
    
    protected Result<?> _invoke(final Iterator<Interceptor> interceptors, final Request request, final Response response) throws Exception {
        if (interceptors != null && interceptors.hasNext()) {
            final Interceptor interceptor = interceptors.next();
            return interceptor.interceptor(this, interceptors, request, response);
        }
        final Object[] parameters = this.adaptor.adapt(this.getServletContext(), request, response);
        if (this.needValidate) {
            for (final Tuple<Validation, Rule<?>> tuple : this.validationList) {
                final Result<?> result = ((Validation)tuple.left).validate(request, (Rule<?>)tuple.right);
                if (result != null) {
                    return result;
                }
            }
        }
        return (Result<?>)this.method.invoke(this.obj, parameters);
    }
    
    public void render(final Result<?> result, final Request request, final Response response) throws Exception {
        this.getView((result != null) ? result.getViewName() : null).render(result, request, response);
    }
    
    private boolean isSync() {
        return this.sync;
    }
    
    public String getActionName() {
        return this.actionName;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public ResponseView getView(final String viewName) {
        return this.vm.getView(viewName);
    }
    
    public void setViewManager(final ViewManager vm) {
        this.vm = vm;
    }
    
    public ServletContext getServletContext() {
        return this.context;
    }
    
    public void setSync(final boolean sync) {
        this.sync = sync;
    }
    
    public List<Tuple<Validation, Rule<?>>> getValidationList() {
        return this.validationList;
    }
    
    public void setValidationList(final List<Tuple<Validation, Rule<?>>> validationList) {
        this.validationList = validationList;
        this.needValidate = (validationList.size() > 0);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.actionName) + " " + this.methodName;
    }
    
    public void setIsInChatTransactional(final boolean isInChatTransactional) {
        this.isInChatTransactional = isInChatTransactional;
    }
    
    public boolean getIsInChatTransactional() {
        return this.isInChatTransactional;
    }
}
