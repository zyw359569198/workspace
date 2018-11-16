package com.reign.framework.common.cache;

import org.xml.sax.*;
import javax.xml.parsers.*;
import org.hibernate.metadata.*;
import org.hibernate.persister.entity.*;
import java.util.*;
import org.w3c.dom.*;
import org.hibernate.type.*;
import com.reign.util.*;
import java.util.jar.*;
import com.sun.org.apache.xml.internal.security.utils.*;
import java.io.*;
import java.net.*;

public class SDataXMLLoader
{
    private static SDataXMLLoader loader;
    private Document doc;
    private String version;
    
    public static synchronized SDataXMLLoader getInstance(final String path) {
        if (SDataXMLLoader.loader == null) {
            SDataXMLLoader.loader = new SDataXMLLoader(path);
        }
        return SDataXMLLoader.loader;
    }
    
    public static synchronized void reset() {
        if (SDataXMLLoader.loader != null) {
            SDataXMLLoader.loader = null;
        }
    }
    
    public String getVersion() {
        return this.version;
    }
    
    protected SDataXMLLoader(final String path) {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(false);
            final DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(final SAXParseException exception) throws SAXException {
                    throw exception;
                }
                
                @Override
                public void fatalError(final SAXParseException exception) throws SAXException {
                    throw exception;
                }
                
                @Override
                public void warning(final SAXParseException exception) {
                }
            });
            this.doc = db.parse(this.getClass().getClassLoader().getResourceAsStream("sdata"));
            this.parseVersion(this.getClass().getClassLoader().getResourceAsStream("sdataVersion"));
        }
        catch (Exception e) {
            throw new RuntimeException("Error loading configuration file ", e);
        }
    }
    
    private void parseVersion(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final byte[] buff = new byte[1024];
        int len = -1;
        while (inputStream.available() > 0) {
            len = inputStream.read(buff);
            if (len > 0) {
                bos.write(buff, 0, len);
            }
        }
        this.version = new String(bos.toByteArray()).trim();
    }
    
    public <E> List<E> getModels(final Class<E> clazz, final ClassMetadata data) {
        final SingleTableEntityPersister persister = (SingleTableEntityPersister)data;
        return this.loadModelsFromFile(clazz, persister);
    }
    
    private Object getValue(final Type type, final String value) {
        if ("NULL".equalsIgnoreCase(value)) {
            return null;
        }
        if (type.getName().equalsIgnoreCase("int") || type.getName().equalsIgnoreCase("integer")) {
            return Integer.parseInt(value);
        }
        if (type.getName().equalsIgnoreCase("float")) {
            return Float.parseFloat(value);
        }
        if (type.getName().equalsIgnoreCase("long")) {
            return Long.parseLong(value);
        }
        if (type.getName().equalsIgnoreCase("double")) {
            return Double.parseDouble(value);
        }
        return value;
    }
    
    private NodeList loadConfigurationFile(final String tableName) {
        final Element rootElement = this.doc.getDocumentElement();
        final NodeList children = rootElement.getChildNodes();
        return this.parseNode(children, tableName);
    }
    
    private <E> List<E> loadModelsFromFile(final Class<E> clazz, final SingleTableEntityPersister data) {
        final NodeList nodeList = this.loadConfigurationFile(data.getTableName());
        final List<E> list = new ArrayList<E>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node node = nodeList.item(i);
            if (node instanceof Element) {
                final Element e = (Element)node;
                final E obj = this.loadModelsFromXML(clazz, data, e.getChildNodes());
                list.add(obj);
            }
        }
        return list;
    }
    
    private <E> E loadModelsFromXML(final Class<E> clazz, final SingleTableEntityPersister data, final NodeList nodeList) {
        try {
            final E obj = clazz.newInstance();
            final String[] identifierColumnNames = data.getIdentifierColumnNames();
            final String identifierPropertyName = data.getIdentifierPropertyName();
            final Type type = data.getIdentifierType();
            final String[] propertyNames = data.getPropertyNames();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node node = nodeList.item(i);
                if (node instanceof Element) {
                    final Element element = (Element)node;
                    final String fieldName = element.getAttribute("name").toUpperCase();
                    boolean isIdColumn = false;
                    String[] array;
                    for (int length = (array = identifierColumnNames).length, j = 0; j < length; ++j) {
                        final String identifierColumnName = array[j];
                        if (fieldName.equalsIgnoreCase(identifierColumnName)) {
                            if (type.isComponentType()) {
                                final ComponentType ct = (ComponentType)type;
                                final String[] components = ct.getPropertyNames();
                                String[] array2;
                                for (int length2 = (array2 = components).length, k = 0; k < length2; ++k) {
                                    final String idProperty = array2[k];
                                    final String[] names = data.getPropertyColumnNames(idProperty);
                                    if (names.length != 0 && names[0].equalsIgnoreCase(fieldName)) {
                                        final Object value = this.getValue(data.getPropertyType(idProperty), node.getTextContent());
                                        DataUtil.setObject(obj, idProperty, value);
                                        break;
                                    }
                                }
                            }
                            else {
                                final Object value2 = this.getValue(type, node.getTextContent());
                                DataUtil.setObject(obj, identifierPropertyName, value2);
                            }
                            isIdColumn = true;
                        }
                    }
                    if (!isIdColumn) {
                        String[] array3;
                        for (int length3 = (array3 = propertyNames).length, l = 0; l < length3; ++l) {
                            final String propertyName = array3[l];
                            final String[] names2 = data.getPropertyColumnNames(propertyName);
                            if (names2.length != 0 && names2[0].equalsIgnoreCase(fieldName)) {
                                final Object value3 = this.getValue(data.getPropertyType(propertyName), node.getTextContent());
                                DataUtil.setObject(obj, propertyName, value3);
                                break;
                            }
                        }
                    }
                }
            }
            return obj;
        }
        catch (Exception e) {
            throw new RuntimeException("unable parse xml", e);
        }
    }
    
    private NodeList parseNode(final NodeList nodeList, final String tableName) {
        for (int childSize = nodeList.getLength(), i = 0; i < childSize; ++i) {
            final Node childNode = nodeList.item(i);
            if (childNode instanceof Element) {
                final Element child = (Element)childNode;
                final String nodeName = child.getNodeName();
                if (nodeName.equalsIgnoreCase("mysql")) {
                    return this.parseNode(child.getChildNodes(), tableName);
                }
                if (nodeName.equalsIgnoreCase("database")) {
                    return this.parseNode(child.getChildNodes(), tableName);
                }
                if (nodeName.equalsIgnoreCase("table")) {
                    final String name = child.getAttribute("name");
                    if (tableName.equalsIgnoreCase(name)) {
                        return child.getChildNodes();
                    }
                }
            }
        }
        return null;
    }
    
    public JarInputStream getJarInputStream(final String urlpath) throws Exception {
        final URL url = new URL(urlpath);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Authorization", new String("Basic " + Base64.encode(new String("rzcore:rzcore2012!@#$").getBytes())));
        conn.connect();
        final JarInputStream jar = new JarInputStream(conn.getInputStream());
        return jar;
    }
    
    private File getFile(final String urlpath) {
        try {
            final URL url = new URL(urlpath);
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            final InputStream is = conn.getInputStream();
            File file = null;
            FileOutputStream fos = null;
            try {
                final byte[] buff = new byte[1024];
                file = File.createTempFile("sxfe", ".tmp");
                fos = new FileOutputStream(file);
                while (true) {
                    final int length = is.read(buff, 0, buff.length);
                    if (-1 == length) {
                        break;
                    }
                    fos.write(buff, 0, length);
                }
                fos.flush();
                fos.close();
                return file;
            }
            catch (IOException ex) {
                if (fos != null) {
                    try {
                        fos.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                return null;
            }
            finally {
                if (fos != null) {
                    try {
                        fos.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (MalformedURLException e2) {
            e2.printStackTrace();
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
        return null;
    }
}
