package com.reign.framework.common;

import org.springframework.beans.factory.*;
import org.apache.commons.logging.*;
import org.logicalcobwebs.proxool.configuration.*;
import java.net.*;
import com.sun.org.apache.xml.internal.security.utils.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.transform.*;

public class ProxoolStartup implements InitializingBean
{
    private static final Log log;
    private String driverUrl;
    private String configUrl;
    
    static {
        log = LogFactory.getLog(ProxoolStartup.class);
    }
    
    public String getDriverUrl() {
        return this.driverUrl;
    }
    
    public void setDriverUrl(final String driverUrl) {
        this.driverUrl = driverUrl;
    }
    
    public String getConfigUrl() {
        return this.configUrl;
    }
    
    public void setConfigUrl(final String configUrl) {
        this.configUrl = configUrl;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        ProxoolStartup.log.info("init proxool config");
        final InputStreamReader reader = this.loadConfig();
        JAXPConfigurator.configure(reader, false);
    }
    
    private InputStreamReader loadConfig() throws Exception {
        final URL url = new URL(this.configUrl);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Authorization", new String("Basic " + Base64.encode(new String("rzcore:rzcore2012!@#$").getBytes())));
        conn.connect();
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.getInputStream());
        final NodeList nodeList = doc.getElementsByTagName("proxool");
        if (nodeList.getLength() != 1) {
            throw new RuntimeException("proxool.xml is error, can't found proxool node");
        }
        final Element element = (Element)nodeList.item(0);
        final Element e = doc.createElement("driver-url");
        e.setTextContent(this.driverUrl);
        element.appendChild(e);
        final DOMSource ds = new DOMSource(doc);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        final StringWriter writer = new StringWriter();
        final Result result = new StreamResult(writer);
        transformer.transform(ds, result);
        return new InputStreamReader(new ByteArrayInputStream(writer.toString().getBytes()));
    }
}
