package com.reign.gcld.system.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.system.dao.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.lang.*;
import java.io.*;
import com.reign.gcld.common.*;
import com.reign.framework.common.cache.*;
import com.reign.gcld.common.component.*;
import com.reign.gcld.system.domain.*;
import java.util.*;
import com.reign.gcld.*;

@Component("versionService")
public class VersionService implements IVersionService, InitializingBean
{
    @Autowired
    private IDbVersionDao dbVersionDao;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        String path = Configuration.getProperty("gcld.sdata.url");
        if (StringUtils.isBlank(path)) {
            final String path2 = String.valueOf(System.getProperty("start.home", System.getProperty("user.dir"))) + File.separator + "apps" + File.separator + "app.properties";
            final File f = new File(path2);
            if (f.exists()) {
                final FileInputStream fis = new FileInputStream(f);
                final Properties p = new Properties();
                p.load(fis);
                path = p.getProperty("sdata.path");
            }
        }
        if (!StringUtils.isBlank(path)) {
            ComponentManager.getInstance().addComponent(new ComponentMessage("sdata", SDataXMLLoader.getInstance(path).getVersion()));
        }
        final List<DbVersion> dbList = this.dbVersionDao.getModels();
        if (dbList == null || dbList.size() <= 0) {
            ComponentManager.getInstance().addComponent(new ComponentMessage("db", "-1"));
        }
        else {
            final Iterator<DbVersion> iterator = dbList.iterator();
            if (iterator.hasNext()) {
                final DbVersion dbv = iterator.next();
                ComponentManager.getInstance().addComponent(new ComponentMessage("db", dbv.getDbVersion()));
            }
        }
        this.checkComponent();
    }
    
    @Override
    public void checkComponent() {
        final List<ComponentMessage> componentList = ComponentManager.getInstance().getAllComponent();
        for (final ComponentMessage cm : componentList) {
            final String version = Environment.COMPONENT_MAP.get(cm.getComponentName());
            if (version != null && !this.isValidateComponent(version.trim(), cm.getVersion().trim())) {
                throw new RuntimeException("unexpected component [" + cm.getComponentName() + ":" + cm.getVersion() + "], expecting component [" + cm.getComponentName() + ":" + version + "]");
            }
        }
    }
    
    private boolean isValidateComponent(final String expectedVersion, final String currentVersion) {
        if (expectedVersion.equalsIgnoreCase(currentVersion)) {
            return true;
        }
        final int[] expectedVersions = this.StringArrayToIntArray(expectedVersion.split("\\."));
        final int[] currentVersions = this.StringArrayToIntArray(currentVersion.split("\\."));
        final int len = Math.min(expectedVersions.length, currentVersions.length);
        for (int i = 0; i < len; ++i) {
            if (currentVersions[i] < expectedVersions[i]) {
                return false;
            }
            if (currentVersions[i] > expectedVersions[i]) {
                return true;
            }
        }
        return len != currentVersions.length || len >= expectedVersions.length;
    }
    
    private int[] StringArrayToIntArray(final String[] strs) {
        final int[] ints = new int[strs.length];
        int index = 0;
        for (final String str : strs) {
            ints[index++] = Integer.valueOf(str);
        }
        return ints;
    }
    
    @Override
    public String getVersion() {
        return Environment.COMPONENT_MAP.toString();
    }
}
