package com.reign.plugin.yx.util.json;

import java.lang.reflect.*;
import java.beans.*;
import java.util.*;
import java.io.*;

public class JSONObject
{
    private Map map;
    public static final Object NULL;
    
    static {
        NULL = new Null(null);
    }
    
    public JSONObject() {
        this.map = new HashMap();
    }
    
    public JSONObject(final JSONObject jo, final String[] names) {
        this();
        for (int i = 0; i < names.length; ++i) {
            try {
                this.putOnce(names[i], jo.opt(names[i]));
            }
            catch (Exception ex) {}
        }
    }
    
    public JSONObject(final JSONTokener x) throws JSONException {
        this();
        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        while (true) {
            char c = x.nextClean();
            switch (c) {
                case '\0': {
                    throw x.syntaxError("A JSONObject text must end with '}'");
                }
                case '}': {}
                default: {
                    x.back();
                    final String key = x.nextValue().toString();
                    c = x.nextClean();
                    if (c == '=') {
                        if (x.next() != '>') {
                            x.back();
                        }
                    }
                    else if (c != ':') {
                        throw x.syntaxError("Expected a ':' after a key");
                    }
                    this.putOnce(key, x.nextValue());
                    switch (x.nextClean()) {
                        case ',':
                        case ';': {
                            if (x.nextClean() == '}') {
                                return;
                            }
                            x.back();
                            continue;
                        }
                        case '}': {
                            return;
                        }
                        default: {
                            throw x.syntaxError("Expected a ',' or '}'");
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public JSONObject(final Map map) {
        this.map = new HashMap();
        if (map != null) {
            for (final Map.Entry e : map.entrySet()) {
                this.map.put(e.getKey(), wrap(e.getValue()));
            }
        }
    }
    
    public JSONObject(final Object bean) {
        this();
        this.populateMap(bean);
    }
    
    public JSONObject(final Object object, final String[] names) {
        this();
        final Class c = object.getClass();
        for (int i = 0; i < names.length; ++i) {
            final String name = names[i];
            try {
                this.putOpt(name, c.getField(name).get(object));
            }
            catch (Exception ex) {}
        }
    }
    
    public JSONObject(final String source) throws JSONException {
        this(new JSONTokener(source));
    }
    
    public JSONObject accumulate(final String key, final Object value) throws JSONException {
        testValidity(value);
        final Object o = this.opt(key);
        if (o == null) {
            this.put(key, (value instanceof JSONArray) ? new JSONArray().put(value) : value);
        }
        else if (o instanceof JSONArray) {
            ((JSONArray)o).put(value);
        }
        else {
            this.put(key, new JSONArray().put(o).put(value));
        }
        return this;
    }
    
    public JSONObject append(final String key, final Object value) throws JSONException {
        testValidity(value);
        final Object o = this.opt(key);
        if (o == null) {
            this.put(key, new JSONArray().put(value));
        }
        else {
            if (!(o instanceof JSONArray)) {
                throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
            }
            this.put(key, ((JSONArray)o).put(value));
        }
        return this;
    }
    
    public static String doubleToString(final double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }
        String s = Double.toString(d);
        if (s.indexOf(46) > 0 && s.indexOf(101) < 0 && s.indexOf(69) < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
    
    public Object get(final String key) throws JSONException {
        final Object o = this.opt(key);
        return o;
    }
    
    public boolean getBoolean(final String key) throws JSONException {
        final Object o = this.get(key);
        if (o.equals(Boolean.FALSE) || (o instanceof String && ((String)o).equalsIgnoreCase("false"))) {
            return false;
        }
        if (o.equals(Boolean.TRUE) || (o instanceof String && ((String)o).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] is not a Boolean.");
    }
    
    public double getDouble(final String key) throws JSONException {
        final Object o = this.get(key);
        try {
            return (o instanceof Number) ? ((Number)o).doubleValue() : Double.valueOf((String)o);
        }
        catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key) + "] is not a number.");
        }
    }
    
    public int getInt(final String key) throws JSONException {
        final Object o = this.get(key);
        try {
            return (o instanceof Number) ? ((Number)o).intValue() : Integer.parseInt((String)o);
        }
        catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key) + "] is not an int.");
        }
    }
    
    public JSONArray getJSONArray(final String key) throws JSONException {
        final Object o = this.get(key);
        if (o instanceof JSONArray) {
            return (JSONArray)o;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONArray.");
    }
    
    public JSONObject getJSONObject(final String key) throws JSONException {
        final Object o = this.get(key);
        if (o instanceof JSONObject) {
            return (JSONObject)o;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONObject.");
    }
    
    public long getLong(final String key) throws JSONException {
        final Object o = this.get(key);
        try {
            return (o instanceof Number) ? ((Number)o).longValue() : Long.parseLong((String)o);
        }
        catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key) + "] is not a long.");
        }
    }
    
    public static String[] getNames(final JSONObject jo) {
        final int length = jo.length();
        if (length == 0) {
            return null;
        }
        final Iterator i = jo.keys();
        final String[] names = new String[length];
        int j = 0;
        while (i.hasNext()) {
            names[j] = i.next();
            ++j;
        }
        return names;
    }
    
    public static String[] getNames(final Object object) {
        if (object == null) {
            return null;
        }
        final Class klass = object.getClass();
        final Field[] fields = klass.getFields();
        final int length = fields.length;
        if (length == 0) {
            return null;
        }
        final String[] names = new String[length];
        for (int i = 0; i < length; ++i) {
            names[i] = fields[i].getName();
        }
        return names;
    }
    
    public String getString(final String key) throws JSONException {
        final Object o = this.get(key);
        if (o == null) {
            return "";
        }
        return o.toString();
    }
    
    public boolean has(final String key) {
        return this.map.containsKey(key);
    }
    
    public JSONObject increment(final String key) throws JSONException {
        final Object value = this.opt(key);
        if (value == null) {
            this.put(key, 1);
        }
        else if (value instanceof Integer) {
            this.put(key, (int)value + 1);
        }
        else if (value instanceof Long) {
            this.put(key, (long)value + 1L);
        }
        else if (value instanceof Double) {
            this.put(key, (double)value + 1.0);
        }
        else {
            if (!(value instanceof Float)) {
                throw new JSONException("Unable to increment [" + key + "].");
            }
            this.put(key, (float)value + 1.0f);
        }
        return this;
    }
    
    public boolean isNull(final String key) {
        return JSONObject.NULL.equals(this.opt(key));
    }
    
    public Iterator keys() {
        return this.map.keySet().iterator();
    }
    
    public int length() {
        return this.map.size();
    }
    
    public JSONArray names() {
        final JSONArray ja = new JSONArray();
        final Iterator keys = this.keys();
        while (keys.hasNext()) {
            ja.put(keys.next());
        }
        return (ja.length() == 0) ? null : ja;
    }
    
    public static String numberToString(final Number n) throws JSONException {
        if (n == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(n);
        String s = n.toString();
        if (s.indexOf(46) > 0 && s.indexOf(101) < 0 && s.indexOf(69) < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
    
    public Object opt(final String key) {
        return (key == null) ? null : this.map.get(key);
    }
    
    public boolean optBoolean(final String key) {
        return this.optBoolean(key, false);
    }
    
    public boolean optBoolean(final String key, final boolean defaultValue) {
        try {
            return this.getBoolean(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public double optDouble(final String key) {
        return this.optDouble(key, Double.NaN);
    }
    
    public double optDouble(final String key, final double defaultValue) {
        try {
            final Object o = this.opt(key);
            return (o instanceof Number) ? ((Number)o).doubleValue() : new Double((String)o);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public int optInt(final String key) {
        return this.optInt(key, 0);
    }
    
    public int optInt(final String key, final int defaultValue) {
        try {
            return this.getInt(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public JSONArray optJSONArray(final String key) {
        final Object o = this.opt(key);
        return (o instanceof JSONArray) ? ((JSONArray)o) : null;
    }
    
    public JSONObject optJSONObject(final String key) {
        final Object o = this.opt(key);
        return (o instanceof JSONObject) ? ((JSONObject)o) : null;
    }
    
    public long optLong(final String key) {
        return this.optLong(key, 0L);
    }
    
    public long optLong(final String key, final long defaultValue) {
        try {
            return this.getLong(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public String optString(final String key) {
        return this.optString(key, "");
    }
    
    public String optString(final String key, final String defaultValue) {
        final Object o = this.opt(key);
        return (o != null) ? o.toString() : defaultValue;
    }
    
    private void populateMap(final Object bean) {
        final Class klass = bean.getClass();
        final boolean includeSuperClass = klass.getClassLoader() != null;
        final Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            try {
                final Method method = methods[i];
                if (Modifier.isPublic(method.getModifiers())) {
                    final String name = method.getName();
                    String key = "";
                    if (name.startsWith("get")) {
                        if (name.equals("getClass") || name.equals("getDeclaringClass")) {
                            key = "";
                        }
                        else {
                            key = name.substring(3);
                        }
                    }
                    else if (name.startsWith("is")) {
                        key = name.substring(2);
                    }
                    if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0) {
                        if (key.length() == 1) {
                            key = key.toLowerCase();
                        }
                        else if (!Character.isUpperCase(key.charAt(1))) {
                            key = String.valueOf(key.substring(0, 1).toLowerCase()) + key.substring(1);
                        }
                        final Object result = method.invoke(bean, (Object[])null);
                        this.map.put(key, wrap(result));
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    public JSONObject put(final String key, final boolean value) throws JSONException {
        this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }
    
    public JSONObject put(final String key, final Collection value) throws JSONException {
        this.put(key, new JSONArray(value));
        return this;
    }
    
    public JSONObject put(final String key, final double value) throws JSONException {
        this.put(key, new Double(value));
        return this;
    }
    
    public JSONObject put(final String key, final int value) throws JSONException {
        this.put(key, new Integer(value));
        return this;
    }
    
    public JSONObject put(final String key, final long value) throws JSONException {
        this.put(key, new Long(value));
        return this;
    }
    
    public JSONObject put(final String key, final Map value) throws JSONException {
        this.put(key, new JSONObject(value));
        return this;
    }
    
    public JSONObject put(final String key, final Object value) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            this.map.put(key, value);
        }
        else {
            this.remove(key);
        }
        return this;
    }
    
    public JSONObject putOnce(final String key, final Object value) throws JSONException {
        if (key != null && value != null) {
            if (this.opt(key) != null) {
                throw new JSONException("Duplicate key \"" + key + "\"");
            }
            this.put(key, value);
        }
        return this;
    }
    
    public JSONObject putOpt(final String key, final Object value) throws JSONException {
        if (key != null && value != null) {
            this.put(key, value);
        }
        return this;
    }
    
    public static String quote(final String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }
        char c = '\0';
        final int len = string.length();
        final StringBuffer sb = new StringBuffer(len + 4);
        sb.append('\"');
        for (int i = 0; i < len; ++i) {
            final char b = c;
            c = string.charAt(i);
            switch (c) {
                case '\"':
                case '\\': {
                    sb.append('\\');
                    sb.append(c);
                    break;
                }
                case '/': {
                    if (b == '<') {
                        sb.append('\\');
                    }
                    sb.append(c);
                    break;
                }
                case '\b': {
                    sb.append("\\b");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\f': {
                    sb.append("\\f");
                    break;
                }
                case '\r': {
                    sb.append("\\r");
                    break;
                }
                default: {
                    if (c < ' ' || (c >= '\u0080' && c < '?') || (c >= '\u2000' && c < '\u2100')) {
                        final String t = "000" + Integer.toHexString(c);
                        sb.append("\\u" + t.substring(t.length() - 4));
                        break;
                    }
                    sb.append(c);
                    break;
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }
    
    public Object remove(final String key) {
        return this.map.remove(key);
    }
    
    public Iterator sortedKeys() {
        return new TreeSet(this.map.keySet()).iterator();
    }
    
    public static Object stringToValue(final String s) {
        if (s.equals("")) {
            return s;
        }
        if (s.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (s.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("null")) {
            return JSONObject.NULL;
        }
        final char b = s.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            Label_0140: {
                if (b == '0' && s.length() > 2) {
                    if (s.charAt(1) != 'x') {
                        if (s.charAt(1) != 'X') {
                            break Label_0140;
                        }
                    }
                    try {
                        return new Integer(Integer.parseInt(s.substring(2), 16));
                    }
                    catch (Exception ex) {}
                }
                try {
                    if (s.indexOf(46) > -1 || s.indexOf(101) > -1 || s.indexOf(69) > -1) {
                        return Double.valueOf(s);
                    }
                    final Long myLong = new Long(s);
                    if (myLong == (int)(Object)myLong) {
                        return new Integer((int)(Object)myLong);
                    }
                    return myLong;
                }
                catch (Exception ex2) {}
            }
        }
        return s;
    }
    
    static void testValidity(final Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers.");
                }
            }
            else if (o instanceof Float && (((Float)o).isInfinite() || ((Float)o).isNaN())) {
                throw new JSONException("JSON does not allow non-finite numbers.");
            }
        }
    }
    
    public JSONArray toJSONArray(final JSONArray names) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        final JSONArray ja = new JSONArray();
        for (int i = 0; i < names.length(); ++i) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }
    
    @Override
    public String toString() {
        try {
            final Iterator keys = this.keys();
            final StringBuffer sb = new StringBuffer("{");
            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                final Object o = keys.next();
                sb.append(quote(o.toString()));
                sb.append(':');
                sb.append(valueToString(this.map.get(o)));
            }
            sb.append('}');
            return sb.toString();
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public String toString(final int indentFactor) throws JSONException {
        return this.toString(indentFactor, 0);
    }
    
    String toString(final int indentFactor, final int indent) throws JSONException {
        final int n = this.length();
        if (n == 0) {
            return "{}";
        }
        final Iterator keys = this.sortedKeys();
        final StringBuffer sb = new StringBuffer("{");
        final int newindent = indent + indentFactor;
        if (n == 1) {
            final Object o = keys.next();
            sb.append(quote(o.toString()));
            sb.append(": ");
            sb.append(valueToString(this.map.get(o), indentFactor, indent));
        }
        else {
            while (keys.hasNext()) {
                final Object o = keys.next();
                if (sb.length() > 1) {
                    sb.append(",\n");
                }
                else {
                    sb.append('\n');
                }
                for (int j = 0; j < newindent; ++j) {
                    sb.append(' ');
                }
                sb.append(quote(o.toString()));
                sb.append(": ");
                sb.append(valueToString(this.map.get(o), indentFactor, newindent));
            }
            if (sb.length() > 1) {
                sb.append('\n');
                for (int j = 0; j < indent; ++j) {
                    sb.append(' ');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }
    
    static String valueToString(final Object value) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof JSONString) {
            Object o;
            try {
                o = ((JSONString)value).toJSONString();
            }
            catch (Exception e) {
                throw new JSONException(e);
            }
            if (o instanceof String) {
                return (String)o;
            }
            throw new JSONException("Bad value from toJSONString: " + o);
        }
        else {
            if (value instanceof Number) {
                return numberToString((Number)value);
            }
            if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray) {
                return value.toString();
            }
            if (value instanceof Map) {
                return new JSONObject((Map)value).toString();
            }
            if (value instanceof Collection) {
                return new JSONArray((Collection)value).toString();
            }
            if (value.getClass().isArray()) {
                return new JSONArray(value).toString();
            }
            return quote(value.toString());
        }
    }
    
    public static String deSerializer(final Object source) {
        final JSONObject object = new JSONObject(source);
        return object.toString();
    }
    
    public static Object serializer(final String jsonText, final Class clazz) {
        Object target = null;
        JSONObject object = null;
        try {
            target = clazz.newInstance();
            object = new JSONObject(jsonText);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e2) {
            e2.printStackTrace();
        }
        catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        try {
            final BeanInfo targetbean = Introspector.getBeanInfo(clazz);
            final PropertyDescriptor[] propertyDescriptors = targetbean.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; ++i) {
                final PropertyDescriptor pro = propertyDescriptors[i];
                final Method wm = pro.getWriteMethod();
                if (wm != null) {
                    final Object _obj = object.get(pro.getName());
                    if (_obj != null && !_obj.equals(JSONObject.NULL)) {
                        if (_obj instanceof JSONObject) {
                            final JSONObject temp = (JSONObject)_obj;
                            final Iterator<String> it = temp.keys();
                            final Map map = new HashMap();
                            while (it.hasNext()) {
                                final String key = it.next();
                                map.put(key, temp.get(key));
                            }
                            wm.invoke(target, map);
                        }
                        else if (_obj instanceof JSONArray) {
                            final JSONArray array = (JSONArray)_obj;
                            final List<Object> objList = new ArrayList<Object>();
                            for (int j = 0; j < array.length(); ++j) {
                                objList.add(array.get(j));
                            }
                            wm.invoke(target, objList);
                        }
                        else {
                            wm.invoke(target, _obj);
                        }
                    }
                }
            }
        }
        catch (IntrospectionException e4) {
            e4.printStackTrace();
        }
        catch (IllegalArgumentException e5) {
            e5.printStackTrace();
        }
        catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        catch (InvocationTargetException e6) {
            e6.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return target;
    }
    
    static String valueToString(final Object value, final int indentFactor, final int indent) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        try {
            if (value instanceof JSONString) {
                final Object o = ((JSONString)value).toJSONString();
                if (o instanceof String) {
                    return (String)o;
                }
            }
        }
        catch (Exception ex) {}
        if (value instanceof Number) {
            return numberToString((Number)value);
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof JSONObject) {
            return ((JSONObject)value).toString(indentFactor, indent);
        }
        if (value instanceof JSONArray) {
            return ((JSONArray)value).toString(indentFactor, indent);
        }
        if (value instanceof Map) {
            return new JSONObject((Map)value).toString(indentFactor, indent);
        }
        if (value instanceof Collection) {
            return new JSONArray((Collection)value).toString(indentFactor, indent);
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString(indentFactor, indent);
        }
        return quote(value.toString());
    }
    
    static Object wrap(final Object object) {
        try {
            if (object == null) {
                return JSONObject.NULL;
            }
            if (object instanceof JSONObject || object instanceof JSONArray || object instanceof Byte || object instanceof Character || object instanceof Short || object instanceof Integer || object instanceof Long || object instanceof Boolean || object instanceof Float || object instanceof Double || object instanceof String || JSONObject.NULL.equals(object)) {
                return object;
            }
            if (object instanceof Collection) {
                return new JSONArray((Collection)object);
            }
            if (object.getClass().isArray()) {
                return new JSONArray(object);
            }
            if (object instanceof Map) {
                return new JSONObject((Map)object);
            }
            final Package objectPackage = object.getClass().getPackage();
            final String objectPackageName = (objectPackage != null) ? objectPackage.getName() : "";
            if (objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.") || object.getClass().getClassLoader() == null) {
                return object.toString();
            }
            return new JSONObject(object);
        }
        catch (Exception exception) {
            return null;
        }
    }
    
    public Writer write(final Writer writer) throws JSONException {
        try {
            boolean b = false;
            final Iterator keys = this.keys();
            writer.write(123);
            while (keys.hasNext()) {
                if (b) {
                    writer.write(44);
                }
                final Object k = keys.next();
                writer.write(quote(k.toString()));
                writer.write(58);
                final Object v = this.map.get(k);
                if (v instanceof JSONObject) {
                    ((JSONObject)v).write(writer);
                }
                else if (v instanceof JSONArray) {
                    ((JSONArray)v).write(writer);
                }
                else {
                    writer.write(valueToString(v));
                }
                b = true;
            }
            writer.write(125);
            return writer;
        }
        catch (IOException exception) {
            throw new JSONException(exception);
        }
    }
    
    private static final class Null
    {
        @Override
        protected final Object clone() {
            return this;
        }
        
        @Override
        public boolean equals(final Object object) {
            return object == null || object == this;
        }
        
        @Override
        public String toString() {
            return "null";
        }
    }
}
