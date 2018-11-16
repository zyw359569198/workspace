package com.reign.framework.jdbc;

import org.springframework.beans.factory.*;
import org.apache.commons.logging.*;
import com.reign.framework.startup.*;
import javax.xml.transform.stream.*;
import com.reign.framework.jdbc.sql.*;
import java.util.jar.*;
import javax.xml.bind.*;
import javax.xml.validation.*;
import java.util.*;
import org.apache.commons.lang.*;
import org.springframework.core.io.*;
import com.reign.framework.plugin.*;
import java.io.*;

public class SqlFactory implements InitializingBean
{
    private static Log log;
    private Map<String, String> dic;
    private Resource[] resources;
    private Resource schemaResource;
    
    static {
        SqlFactory.log = LogFactory.getLog(SqlFactory.class);
    }
    
    public SqlFactory() {
        this.dic = new HashMap<String, String>();
    }
    
    public void setResources(final Resource[] resources) {
        this.resources = resources;
    }
    
    public void setSchemaResource(final Resource resource) {
        this.schemaResource = resource;
    }
    
    public String get(final String key) {
        return this.dic.get(key);
    }
    
    public void loadPlugin(final PluginInfo pluginInfo) throws Exception {
        final Resource res = this.getSqlResource(pluginInfo);
        final List<Resource> resList = new ArrayList<Resource>();
        if (res != null) {
            if (res.getFilename().endsWith(".jar")) {
                final JarInputStream jar = new JarInputStream(res.getInputStream());
                for (JarEntry entry = jar.getNextJarEntry(); entry != null; entry = jar.getNextJarEntry()) {
                    if (entry.getName().endsWith("sql.xml")) {
                        resList.add(new InputStreamResource(this.getResource(jar)));
                    }
                }
                jar.close();
            }
            else {
                this.getResources(resList, res.getFile());
            }
        }
        if (resList.size() == 0) {
            return;
        }
        final JAXBContext jaxbContext = JAXBContext.newInstance("com.reign.framework.jdbc.sql");
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        final SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        final Schema schema = factory.newSchema(new StreamSource(this.schemaResource.getInputStream()));
        unmarshaller.setSchema(schema);
        for (final Resource resource : resList) {
            final JAXBElement<Sqls> element = (JAXBElement<Sqls>)unmarshaller.unmarshal(resource.getInputStream());
            final Sqls sqls = element.getValue();
            for (final Sql sql : sqls.getSql()) {
                if (!this.dic.containsKey(sql.getId())) {
                    this.dic.put(sql.getId(), sql.getValue());
                    pluginInfo.addSqls(sql.getId());
                    SqlFactory.log.info("SQL\u8bed\u53e5\u52a0\u8f7d\u5b8c\u6bd5:" + sql.getId());
                }
            }
        }
    }
    
    public void removePlugin(final PluginInfo pluginInfo) {
        if (pluginInfo.sqls != null) {
            for (final String sqlId : pluginInfo.sqls) {
                this.dic.remove(sqlId);
            }
        }
    }
    
    private Resource getSqlResource(final PluginInfo pluginInfo) {
        if (StringUtils.isBlank(pluginInfo.sqlFile)) {
            return null;
        }
        final Resource sqlResource = new FileSystemResource(Thread.currentThread().getContextClassLoader().getResource(pluginInfo.sqlFile).getFile());
        return sqlResource;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Resource> resList = new ArrayList<Resource>();
        Resource[] resources;
        for (int length = (resources = this.resources).length, i = 0; i < length; ++i) {
            final Resource res = resources[i];
            try {
                if (res.getFilename().endsWith(".jar")) {
                    final JarInputStream jar = new JarInputStream(res.getInputStream());
                    for (JarEntry entry = jar.getNextJarEntry(); entry != null; entry = jar.getNextJarEntry()) {
                        if (entry.getName().endsWith("sql.xml")) {
                            resList.add(new InputStreamResource(this.getResource(jar)));
                        }
                    }
                    jar.close();
                }
                else {
                    this.getResources(resList, res.getFile());
                }
            }
            catch (FileNotFoundException ex) {}
        }
        this.getPluginResources(resList);
        if (resList.size() == 0) {
            throw new FileNotFoundException("not found sql file");
        }
        final JAXBContext jaxbContext = JAXBContext.newInstance("com.reign.framework.jdbc.sql");
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        final SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        final Schema schema = factory.newSchema(new StreamSource(this.schemaResource.getInputStream()));
        unmarshaller.setSchema(schema);
        for (final Resource res2 : resList) {
            final JAXBElement<Sqls> element = (JAXBElement<Sqls>)unmarshaller.unmarshal(res2.getInputStream());
            final Sqls sqls = element.getValue();
            for (final Sql sql : sqls.getSql()) {
                if (!this.dic.containsKey(sql.getId())) {
                    this.dic.put(sql.getId(), sql.getValue());
                    SqlFactory.log.info("SQL\u8bed\u53e5\u52a0\u8f7d\u5b8c\u6bd5:" + sql.getId());
                }
            }
        }
    }
    
    private void getPluginResources(final List<Resource> resList) throws IOException {
        final Map<String, PluginInfo> pluginMap = PluginLoader.getInstance().getPlugins();
        for (final Map.Entry<String, PluginInfo> entry : pluginMap.entrySet()) {
            final PluginInfo pluginInfo = entry.getValue();
            final Resource res = this.getSqlResource(pluginInfo);
            if (res != null) {
                if (res.getFilename().endsWith(".jar")) {
                    final JarInputStream jar = new JarInputStream(res.getInputStream());
                    for (JarEntry jarEntry = jar.getNextJarEntry(); jarEntry != null; jarEntry = jar.getNextJarEntry()) {
                        if (jarEntry.getName().endsWith("sql.xml")) {
                            resList.add(new InputStreamResource(this.getResource(jar)));
                        }
                    }
                    jar.close();
                }
                else {
                    this.getResources(resList, res.getFile());
                }
            }
        }
    }
    
    private InputStream getResource(final JarInputStream jar) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final byte[] buff = new byte[1024];
        int len = -1;
        while (jar.available() > 0) {
            len = jar.read(buff);
            if (len > 0) {
                bos.write(buff, 0, len);
            }
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }
    
    private void getResources(final List<Resource> resources, final File file) {
        if (file.isFile()) {
            resources.add(new FileSystemResource(file));
        }
        else {
            final File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(final File file) {
                    return file.isDirectory() || file.getName().equals("sql.xml");
                }
            });
            File[] array;
            for (int length = (array = files).length, i = 0; i < length; ++i) {
                final File f = array[i];
                this.getResources(resources, f);
            }
        }
    }
}
