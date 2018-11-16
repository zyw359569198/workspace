package com.reign.framework.hibernate.hql;

import org.springframework.beans.factory.*;
import org.apache.commons.logging.*;
import javax.xml.transform.stream.*;
import com.reign.framework.exception.*;
import java.util.jar.*;
import javax.xml.bind.*;
import javax.xml.validation.*;
import java.util.*;
import org.springframework.core.io.*;
import java.io.*;

public class HqlFactory implements InitializingBean
{
    private static final Log log;
    private Map<String, String> dic;
    private Resource[] resources;
    private Resource schemaResource;
    
    static {
        log = LogFactory.getLog(HqlFactory.class);
    }
    
    public HqlFactory() {
        this.dic = new HashMap<String, String>();
    }
    
    public void setResources(final Resource[] resources) {
        this.resources = resources;
    }
    
    public void setSchemaResource(final Resource resource) {
        this.schemaResource = resource;
    }
    
    public String get(final String key) {
        return (this.dic.get(key) == null) ? key : this.dic.get(key);
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
                        if (entry.getName().endsWith("hql.xml")) {
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
        if (resList.size() == 0) {
            throw new FileNotFoundException("not found hql file");
        }
        final JAXBContext jaxbContext = JAXBContext.newInstance("com.reign.framework.hibernate.hql");
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        final SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        final Schema schema = factory.newSchema(new StreamSource(this.schemaResource.getInputStream()));
        unmarshaller.setSchema(schema);
        for (final Resource res2 : resList) {
            final JAXBElement<Hqls> element = (JAXBElement<Hqls>)unmarshaller.unmarshal(res2.getInputStream());
            final Hqls hqls = element.getValue();
            for (final Hql hql : hqls.getHql()) {
                if (this.dic.containsKey(hql.getId())) {
                    throw new InternalException("\u5b9a\u4e49\u4e86\u91cd\u590d\u7684SQL:" + hql.getId());
                }
                this.dic.put(hql.getId(), hql.getValue());
                HqlFactory.log.info("HQL\u8bed\u53e5\u52a0\u8f7d\u5b8c\u6bd5:" + hql.getId());
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
                    return file.isDirectory() || file.getName().equals("hql.xml");
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
