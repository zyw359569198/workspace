package com.reign.framework.json;

import com.reign.util.*;
import java.util.*;

public class JsonBuilder
{
    public static final byte[] COMMA;
    public static final byte[] QUOTE;
    public static final byte[] COLON;
    public static final byte[] START_ARRAY;
    public static final byte[] END_ARRAY;
    public static final byte[] START_OBJECT;
    public static final byte[] END_OBJECT;
    
    static {
        COMMA = new byte[] { 44 };
        QUOTE = new byte[] { 34 };
        COLON = new byte[] { 58 };
        START_ARRAY = new byte[] { 91 };
        END_ARRAY = new byte[] { 93 };
        START_OBJECT = new byte[] { 123 };
        END_OBJECT = new byte[] { 125 };
    }
    
    public static byte[] getRedirectJson(final String msg, final String url) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", State.REDIRECT.getValue());
        doc.startObject("data");
        createNamedElement(doc, "msg", msg);
        createNamedElement(doc, "url", url);
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getJson(final State state, final String msg) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        doc.startObject("data");
        createNamedElement(doc, "msg", msg);
        doc.endObject();
        doc.endObject();
        if (state.equals(State.FAIL)) {
            if (msg.length() > 6) {
                ThreadLocalFactory.setTreadLocalLog("FAIL|" + msg.substring(0, 6));
            }
            else {
                ThreadLocalFactory.setTreadLocalLog("FAIL|" + msg);
            }
        }
        return doc.toByte();
    }
    
    public static byte[] getMjcsJson(final State state, final String msg) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        if (state.equals(State.FAIL) || state.equals(State.EXCEPTION)) {
            createNamedElement(doc, "message", msg);
        }
        else if (msg != "") {
            doc.startObject("data");
            createNamedElement(doc, "msg", msg);
            doc.endObject();
        }
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getJson(final State state, final String title, final String msg) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        doc.startObject("data");
        createNamedElement(doc, title, msg);
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static <T> byte[] getJson(final State state, final String name, final T t) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        doc.startObject("data");
        createNamedElement(doc, name, t);
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static <T> byte[] getJson(final State state, final String name, final byte[] bytes) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        doc.startObject("data");
        doc.appendJson(name, bytes);
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static <T> byte[] getMjcsJson(final State state, final String command, final byte[] bytes) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        createNamedElement(doc, "command", command);
        doc.startObject("data");
        doc.appendJson(bytes);
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getJson(final State state, final byte[] json) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        doc.appendJson("data", json);
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getObjectJson(final State state, final byte[] json) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        doc.appendObjectJson("data", json);
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getJson(final byte[] action) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.appendJson("action", action);
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getJson(final byte[] action, final byte[] extra) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.appendJson("action", action);
        doc.appendJson("extra", extra);
        doc.endObject();
        return doc.toByte();
    }
    
    public static <T> byte[] getJson(final State state, final String msg, final String name, final T t) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        doc.startObject("data");
        createNamedElement(doc, "msg", msg);
        createNamedElement(doc, name, t);
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static <T> byte[] getJson(final State state, final Map<String, byte[]> map) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        doc.startObject("data");
        for (final String key : map.keySet()) {
            doc.appendJson(key, map.get(key));
        }
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getSimpleJson(final String name, final Object value) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement(name, value);
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getExceptionJson(final State state, final String errorCode) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startObject("action");
        createNamedElement(doc, "state", state.getValue());
        doc.startObject("data");
        createNamedElement(doc, "errorCode", errorCode);
        doc.endObject();
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getExceptionWithoutActionJson(final State state, final String errorCode) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        createNamedElement(doc, "state", state.getValue());
        doc.startObject("data");
        createNamedElement(doc, "errorCode", errorCode);
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] getCodeJson(final State state) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startObject("action");
        createNamedElement(doc, "state", state.getValue());
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static void createElement(final JsonDocument doc, final Object value) {
        doc.createElement(value);
    }
    
    public static void createNamedElement(final JsonDocument doc, final String name, final Object value) {
        doc.createElement(name, value);
    }
    
    public static byte[] getJsonDocumentWithSuccess(final String key, final Object value) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement(key, value);
        doc.endObject();
        return getJson(State.SUCCESS, doc.toByte());
    }
}
