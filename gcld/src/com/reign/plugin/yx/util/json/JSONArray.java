package com.reign.plugin.yx.util.json;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

public class JSONArray
{
    private ArrayList myArrayList;
    
    public JSONArray() {
        this.myArrayList = new ArrayList();
    }
    
    public JSONArray(final JSONTokener x) throws JSONException {
        this();
        char c = x.nextClean();
        char q;
        if (c == '[') {
            q = ']';
        }
        else {
            if (c != '(') {
                throw x.syntaxError("A JSONArray text must start with '['");
            }
            q = ')';
        }
        if (x.nextClean() == ']') {
            return;
        }
        x.back();
        while (true) {
            if (x.nextClean() == ',') {
                x.back();
                this.myArrayList.add(null);
            }
            else {
                x.back();
                this.myArrayList.add(x.nextValue());
            }
            c = x.nextClean();
            switch (c) {
                case ',':
                case ';': {
                    if (x.nextClean() == ']') {
                        return;
                    }
                    x.back();
                    continue;
                }
                case ')':
                case ']': {
                    if (q != c) {
                        throw x.syntaxError("Expected a '" + new Character(q) + "'");
                    }
                }
                default: {
                    throw x.syntaxError("Expected a ',' or ']'");
                }
            }
        }
    }
    
    public JSONArray(final String source) throws JSONException {
        this(new JSONTokener(source));
    }
    
    public JSONArray(final Collection collection) {
        this.myArrayList = new ArrayList();
        if (collection != null) {
            for (final Object o : collection) {
                this.myArrayList.add(JSONObject.wrap(o));
            }
        }
    }
    
    public JSONArray(final Object array) throws JSONException {
        this();
        if (array.getClass().isArray()) {
            for (int length = Array.getLength(array), i = 0; i < length; ++i) {
                this.put(JSONObject.wrap(Array.get(array, i)));
            }
            return;
        }
        throw new JSONException("JSONArray initial value should be a string or collection or array.");
    }
    
    public Object get(final int index) throws JSONException {
        final Object o = this.opt(index);
        if (o == null) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        return o;
    }
    
    public boolean getBoolean(final int index) throws JSONException {
        final Object o = this.get(index);
        if (o.equals(Boolean.FALSE) || (o instanceof String && ((String)o).equalsIgnoreCase("false"))) {
            return false;
        }
        if (o.equals(Boolean.TRUE) || (o instanceof String && ((String)o).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONArray[" + index + "] is not a Boolean.");
    }
    
    public double getDouble(final int index) throws JSONException {
        final Object o = this.get(index);
        try {
            return (o instanceof Number) ? ((Number)o).doubleValue() : Double.valueOf((String)o);
        }
        catch (Exception e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }
    
    public int getInt(final int index) throws JSONException {
        final Object o = this.get(index);
        return (o instanceof Number) ? ((Number)o).intValue() : ((int)this.getDouble(index));
    }
    
    public JSONArray getJSONArray(final int index) throws JSONException {
        final Object o = this.get(index);
        if (o instanceof JSONArray) {
            return (JSONArray)o;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
    }
    
    public JSONObject getJSONObject(final int index) throws JSONException {
        final Object o = this.get(index);
        if (o instanceof JSONObject) {
            return (JSONObject)o;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
    }
    
    public long getLong(final int index) throws JSONException {
        final Object o = this.get(index);
        return (o instanceof Number) ? ((Number)o).longValue() : ((long)this.getDouble(index));
    }
    
    public String getString(final int index) throws JSONException {
        return this.get(index).toString();
    }
    
    public boolean isNull(final int index) {
        return JSONObject.NULL.equals(this.opt(index));
    }
    
    public String join(final String separator) throws JSONException {
        final int len = this.length();
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(JSONObject.valueToString(this.myArrayList.get(i)));
        }
        return sb.toString();
    }
    
    public int length() {
        return this.myArrayList.size();
    }
    
    public Object opt(final int index) {
        return (index < 0 || index >= this.length()) ? null : this.myArrayList.get(index);
    }
    
    public boolean optBoolean(final int index) {
        return this.optBoolean(index, false);
    }
    
    public boolean optBoolean(final int index, final boolean defaultValue) {
        try {
            return this.getBoolean(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public double optDouble(final int index) {
        return this.optDouble(index, Double.NaN);
    }
    
    public double optDouble(final int index, final double defaultValue) {
        try {
            return this.getDouble(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public int optInt(final int index) {
        return this.optInt(index, 0);
    }
    
    public int optInt(final int index, final int defaultValue) {
        try {
            return this.getInt(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public JSONArray optJSONArray(final int index) {
        final Object o = this.opt(index);
        return (o instanceof JSONArray) ? ((JSONArray)o) : null;
    }
    
    public JSONObject optJSONObject(final int index) {
        final Object o = this.opt(index);
        return (o instanceof JSONObject) ? ((JSONObject)o) : null;
    }
    
    public long optLong(final int index) {
        return this.optLong(index, 0L);
    }
    
    public long optLong(final int index, final long defaultValue) {
        try {
            return this.getLong(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public String optString(final int index) {
        return this.optString(index, "");
    }
    
    public String optString(final int index, final String defaultValue) {
        final Object o = this.opt(index);
        return (o != null) ? o.toString() : defaultValue;
    }
    
    public JSONArray put(final boolean value) {
        this.put(value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }
    
    public JSONArray put(final Collection value) {
        this.put(new JSONArray(value));
        return this;
    }
    
    public JSONArray put(final double value) throws JSONException {
        final Double d = new Double(value);
        JSONObject.testValidity(d);
        this.put(d);
        return this;
    }
    
    public JSONArray put(final int value) {
        this.put(new Integer(value));
        return this;
    }
    
    public JSONArray put(final long value) {
        this.put(new Long(value));
        return this;
    }
    
    public JSONArray put(final Map value) {
        this.put(new JSONObject(value));
        return this;
    }
    
    public JSONArray put(final Object value) {
        this.myArrayList.add(value);
        return this;
    }
    
    public JSONArray put(final int index, final boolean value) throws JSONException {
        this.put(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }
    
    public JSONArray put(final int index, final Collection value) throws JSONException {
        this.put(index, new JSONArray(value));
        return this;
    }
    
    public JSONArray put(final int index, final double value) throws JSONException {
        this.put(index, new Double(value));
        return this;
    }
    
    public JSONArray put(final int index, final int value) throws JSONException {
        this.put(index, new Integer(value));
        return this;
    }
    
    public JSONArray put(final int index, final long value) throws JSONException {
        this.put(index, new Long(value));
        return this;
    }
    
    public JSONArray put(final int index, final Map value) throws JSONException {
        this.put(index, new JSONObject(value));
        return this;
    }
    
    public JSONArray put(final int index, final Object value) throws JSONException {
        JSONObject.testValidity(value);
        if (index < 0) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < this.length()) {
            this.myArrayList.set(index, value);
        }
        else {
            while (index != this.length()) {
                this.put(JSONObject.NULL);
            }
            this.put(value);
        }
        return this;
    }
    
    public Object remove(final int index) {
        final Object o = this.opt(index);
        this.myArrayList.remove(index);
        return o;
    }
    
    public JSONObject toJSONObject(final JSONArray names) throws JSONException {
        if (names == null || names.length() == 0 || this.length() == 0) {
            return null;
        }
        final JSONObject jo = new JSONObject();
        for (int i = 0; i < names.length(); ++i) {
            jo.put(names.getString(i), this.opt(i));
        }
        return jo;
    }
    
    @Override
    public String toString() {
        try {
            return String.valueOf('[') + this.join(",") + ']';
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public String toString(final int indentFactor) throws JSONException {
        return this.toString(indentFactor, 0);
    }
    
    String toString(final int indentFactor, final int indent) throws JSONException {
        final int len = this.length();
        if (len == 0) {
            return "[]";
        }
        final StringBuffer sb = new StringBuffer("[");
        if (len == 1) {
            sb.append(JSONObject.valueToString(this.myArrayList.get(0), indentFactor, indent));
        }
        else {
            final int newindent = indent + indentFactor;
            sb.append('\n');
            for (int i = 0; i < len; ++i) {
                if (i > 0) {
                    sb.append(",\n");
                }
                for (int j = 0; j < newindent; ++j) {
                    sb.append(' ');
                }
                sb.append(JSONObject.valueToString(this.myArrayList.get(i), indentFactor, newindent));
            }
            sb.append('\n');
            for (int i = 0; i < indent; ++i) {
                sb.append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    public Writer write(final Writer writer) throws JSONException {
        try {
            boolean b = false;
            final int len = this.length();
            writer.write(91);
            for (int i = 0; i < len; ++i) {
                if (b) {
                    writer.write(44);
                }
                final Object v = this.myArrayList.get(i);
                if (v instanceof JSONObject) {
                    ((JSONObject)v).write(writer);
                }
                else if (v instanceof JSONArray) {
                    ((JSONArray)v).write(writer);
                }
                else {
                    writer.write(JSONObject.valueToString(v));
                }
                b = true;
            }
            writer.write(93);
            return writer;
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
    }
}
