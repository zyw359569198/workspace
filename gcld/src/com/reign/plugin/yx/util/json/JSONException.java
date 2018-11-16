package com.reign.plugin.yx.util.json;

public class JSONException extends Exception
{
    private static final long serialVersionUID = 0L;
    private Throwable cause;
    
    public JSONException(final String message) {
        super(message);
    }
    
    public JSONException(final Throwable t) {
        super(t.getMessage());
        this.cause = t;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
