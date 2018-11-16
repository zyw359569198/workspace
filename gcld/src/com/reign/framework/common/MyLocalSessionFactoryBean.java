package com.reign.framework.common;

import org.springframework.orm.hibernate3.*;
import org.springframework.core.io.*;
import java.util.*;
import java.util.jar.*;
import java.io.*;

public class MyLocalSessionFactoryBean extends LocalSessionFactoryBean
{
    @Override
	public void setMappingJarLocations(final Resource[] mappingJarLocations) {
        if (mappingJarLocations != null) {
            final List<Resource> list = new ArrayList<Resource>();
            for (final Resource res : mappingJarLocations) {
                try {
                    final JarInputStream jar = new JarInputStream(res.getInputStream());
                    JarEntry entry = null;
                    while ((entry = jar.getNextJarEntry()) != null) {
                        if (entry.getName().toLowerCase().endsWith(".hbm.xml")) {
                            list.add(new InputStreamResource(this.getResource(jar)));
                        }
                    }
                    jar.close();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            super.setMappingLocations(list.toArray(new Resource[0]));
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
}
