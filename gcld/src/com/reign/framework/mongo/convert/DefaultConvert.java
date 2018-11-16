package com.reign.framework.mongo.convert;

import com.reign.framework.common.*;
import com.reign.framework.mongo.*;
import java.lang.reflect.*;
import com.reign.framework.mongo.util.*;
import com.reign.util.*;
import java.lang.annotation.*;
import com.reign.framework.mongo.annotation.*;
import java.util.*;

public class DefaultConvert extends AbstractConvert implements DBObjectToObject, ObjectToDBObject
{
    public static Map<Class<?>, Object> OBJECT_MAP;
    
    static {
        DefaultConvert.OBJECT_MAP = new HashMap<Class<?>, Object>(1024);
    }
    
    @Override
    public void register(final Class<?> clazz, final Object obj) {
        if (obj instanceof Convertible) {
            DefaultConvert.OBJECT_MAP.put(clazz, obj);
        }
    }
    
    @Override
    public <E> E convert(final DBObject dbObject, final Class<E> clazz) {
        if (dbObject == null) {
            return null;
        }
        if (Convertible.class.isAssignableFrom(clazz)) {
            Object obj = DefaultConvert.OBJECT_MAP.get(clazz);
            if (obj == null) {
                synchronized (DefaultConvert.OBJECT_MAP) {
                    obj = DefaultConvert.OBJECT_MAP.get(clazz);
                    if (obj == null) {
                        try {
                            obj = clazz.newInstance();
                            DefaultConvert.OBJECT_MAP.put(clazz, obj);
                        }
                        catch (Exception e) {
                            throw new RuntimeException("create bean error", e);
                        }
                    }
                }
                // monitorexit(DefaultConvert.OBJECT_MAP)
            }
            final Convertible<E> convertible = (Convertible<E>)obj;
            return convertible.parse(dbObject);
        }
        if (Object.class == clazz) {
            if (dbObject instanceof BasicDBList) {
                final BasicDBList dbList = (BasicDBList)dbObject;
                return (E)dbList.toArray(new Object[0]);
            }
            return (E)dbObject.toMap();
        }
        else {
            try {
                final MongoField[] fields = parse(Lang.getFields(clazz));
                final Object obj2 = clazz.newInstance();
                MongoField[] array2;
                for (int length = (array2 = fields).length, j = 0; j < length; ++j) {
                    final MongoField field = array2[j];
                    if (field != null) {
                        final Method method = field.writter;
                        switch (field.type) {
                            case ARRAY_TYPE: {
                                final BasicDBList dbList2 = (BasicDBList)dbObject.get(field.fieldName);
                                final Object array = Array.newInstance(field.field.getType().getComponentType(), dbList2.size());
                                for (int i = 0; i < dbList2.size(); ++i) {
                                    Array.set(array, i, this.convert(dbList2.get(i), field.field.getType().getComponentType()));
                                }
                                method.invoke(obj2, array);
                                break;
                            }
                            case DATE_TYPE: {
                                method.invoke(obj2, new Date((long)dbObject.get(field.fieldName)));
                                break;
                            }
                            default: {
                                method.invoke(obj2, dbObject.get(field.fieldName));
                                break;
                            }
                        }
                    }
                }
                return (E)obj2;
            }
            catch (Exception e2) {
                throw new RuntimeException("create bean error", e2);
            }
        }
    }
    
    private Object convert(final Object object, final Class<?> componentType) {
        if (object instanceof DBObject) {
            return this.convert(object, componentType);
        }
        return object;
    }
    
    @Override
    public DBObject convert(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Convertible) {
            final Convertible convertible = (Convertible)obj;
            return convertible.toDBObject();
        }
        final DBObject dbo = (DBObject)new BasicDBObject();
        if (Lang.isSimpleClass(obj.getClass())) {
            dbo.put(obj.getClass().getSimpleName(), obj);
        }
        if (Map.class.isAssignableFrom(obj.getClass())) {
            this.writeMapInternal((Map<Object, Object>)obj, dbo);
            return dbo;
        }
        if (Collection.class.isAssignableFrom(obj.getClass())) {
            final BasicDBList dbList = new BasicDBList();
            this.writeCollectionInternal((Collection<?>)obj, dbList);
            dbo.put(MongoDBUtil.getCollectionName(obj.getClass()), (Object)dbList);
            return dbo;
        }
        if (obj.getClass().isArray()) {
            final BasicDBList dbList = new BasicDBList();
            this.writeArrayInternal(obj, dbList);
            dbo.put(MongoDBUtil.getCollectionName(obj.getClass()), (Object)dbList);
            return dbo;
        }
        this.writeBeanInternal(obj, dbo);
        return dbo;
    }
    
    private void writeBeanInternal(final Object obj, final DBObject dbo) {
        final MongoField[] fields = parse(Lang.getFields(obj.getClass()));
        MongoField[] array;
        for (int length = (array = fields).length, i = 0; i < length; ++i) {
            final MongoField field = array[i];
            try {
                if (field != null) {
                    final Object value = ReflectUtil.get(field.field, obj);
                    switch (field.type) {
                        case DATE_TYPE: {
                            dbo.put(field.fieldName, (Object)((Date)value).getTime());
                            break;
                        }
                        default: {
                            dbo.put(field.fieldName, value);
                            break;
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void writeArrayInternal(final Object obj, final BasicDBList dbList) {
        for (int len = Array.getLength(obj), i = 0; i < len; ++i) {
            final Object e = Array.get(obj, i);
            dbList.add(e);
        }
    }
    
    private void writeCollectionInternal(final Collection<?> collection, final BasicDBList dbo) {
        for (final Object obj : collection) {
            dbo.add(obj);
        }
    }
    
    private void writeMapInternal(final Map<Object, Object> map, final DBObject dbo) {
        for (final Map.Entry<Object, Object> entry : map.entrySet()) {
            final Object key = entry.getKey();
            final Object val = entry.getValue();
            if (!Lang.isSimpleClass(key.getClass())) {
                throw new RuntimeException("don't support complex key " + key.getClass());
            }
            final String simpleKey = key.toString();
            dbo.put(simpleKey, val);
        }
    }
    
    private static MongoField[] parse(final Lang.MyField[] fields) {
        if (fields == null) {
            return null;
        }
        if (fields.length == 0) {
            return new MongoField[0];
        }
        final MongoField[] mongoFields = new MongoField[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            mongoFields[i] = new MongoField(fields[i]);
            mongoFields[i].isPrimary = Lang.hasAnnotation(mongoFields[i].field, Primary.class);
            mongoFields[i].insertIgnore = Lang.hasAnnotation(mongoFields[i].field, InsertIgnore.class);
            mongoFields[i].jdbcType = Lang.getJdbcType(mongoFields[i].field.getType());
        }
        return mongoFields;
    }
    
    public static void main(final String[] args) {
        final DefaultConvert convert = new DefaultConvert();
        final Bean bean = new Bean();
        bean.id = "123";
        bean.name = "abc";
        (bean.abc = new ArrayList<String>()).add("123");
        (bean.def = new HashMap<Integer, Integer>()).put(12, 11);
        bean.date = new Date();
        final DBObject obj = convert.convert(bean.abc);
        System.out.println(obj);
    }
    
    public static class Bean
    {
        public String id;
        public String name;
        public List<String> abc;
        public Map<Integer, Integer> def;
        public Date date;
        
        public Date getDate() {
            return this.date;
        }
        
        public void setDate(final Date date) {
            this.date = date;
        }
        
        public Map<Integer, Integer> getDef() {
            return this.def;
        }
        
        public void setDef(final Map<Integer, Integer> def) {
            this.def = def;
        }
        
        public String getId() {
            return this.id;
        }
        
        public void setId(final String id) {
            this.id = id;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public List<String> getAbc() {
            return this.abc;
        }
        
        public void setAbc(final List<String> abc) {
            this.abc = abc;
        }
    }
}
