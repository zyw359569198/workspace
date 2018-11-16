package com.reign.util.characterFilter;

import java.util.concurrent.locks.*;
import com.reign.util.log.*;
import org.springframework.context.support.*;
import org.dom4j.io.*;
import org.springframework.context.*;
import org.dom4j.*;
import java.util.*;

public class CharacterFilterFactory
{
    Logger logger;
    private static CharacterFilterFactory instance;
    private HashMap<String, ICharacterFilter> map;
    private final ReentrantReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    
    static {
        CharacterFilterFactory.instance = new CharacterFilterFactory();
    }
    
    private CharacterFilterFactory() {
        this.logger = CommonLog.getLog(CharacterFilterFactory.class);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.map = new HashMap<String, ICharacterFilter>();
    }
    
    public static CharacterFilterFactory getInstance() {
        return CharacterFilterFactory.instance;
    }
    
    private void loadCharacterFilter() {
        this.writeLock.lock();
        try {
            this.map.clear();
            final ApplicationContext ctx = new ClassPathXmlApplicationContext("characterFilter.xml");
            final SAXReader saxReader = new SAXReader();
            try {
                final Document doc = saxReader.read(this.getClass().getClassLoader().getResourceAsStream("characterFilterMap.xml"));
                final Element filters = doc.getRootElement();
                final Iterator i = filters.elementIterator();
                while (i.hasNext()) {
                    final Element filterElement = i.next();
                    final ICharacterFilter filter = (ICharacterFilter)ctx.getBean(filterElement.attribute("ref").getText());
                    if (filter != null) {
                        this.map.put(filterElement.attribute("name").getText(), filter);
                    }
                }
            }
            catch (DocumentException de) {
                this.logger.error("\u83b7\u5f97\u5c4f\u853d\u5b57\u8fc7\u6ee4\u5668\u6620\u5c04\u65f6\u53d1\u751f\u9519\u8bef!", de);
            }
        }
        finally {
            this.writeLock.unlock();
        }
        this.writeLock.unlock();
    }
    
    public void init() {
        this.loadCharacterFilter();
    }
    
    public void refresh() {
        this.loadCharacterFilter();
    }
    
    public ICharacterFilter getFilter(final String key) {
        this.readLock.lock();
        try {
            return this.map.get(key);
        }
        finally {
            this.readLock.unlock();
        }
    }
}
