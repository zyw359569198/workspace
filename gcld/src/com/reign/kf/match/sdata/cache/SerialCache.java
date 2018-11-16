package com.reign.kf.match.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.sdata.domain.*;
import java.util.*;

@Component("serialCache")
public class SerialCache extends AbstractCache<String, Integer>
{
    @Autowired
    private SDataLoader dataLoader;
    private static SerialCache staicSerialCache;
    List<Integer> intiLv;
    
    public SerialCache() {
        this.intiLv = new ArrayList<Integer>();
    }
    
    public static Integer getValue(final int id, final int index) {
        return SerialCache.staicSerialCache.get(id, index);
    }
    
    public List<Integer> getIntiLv() {
        return this.intiLv;
    }
    
    public void setIntiLv(final List<Integer> intiLv) {
        this.intiLv = intiLv;
    }
    
    public Integer get(final int id, final int index) {
        if (id == 0) {
            return index;
        }
        final StringBuffer buff = new StringBuffer();
        buff.append(id).append(":").append(index);
        return this.get(buff.toString());
    }
    
    @Override
	public Integer get(final String key) {
        final Integer result = (Integer)super.get((Object)key);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Serial> sList = this.dataLoader.getModels((Class)Serial.class);
        for (final Serial s : sList) {
            final StringBuffer buff = new StringBuffer();
            buff.append(s.getId()).append(":").append(s.getIndex());
            super.put((Object)buff.toString(), (Object)s.getPoint());
            if (s.getId() == 3) {
                this.intiLv.add(s.getPoint());
            }
        }
        Collections.sort(this.intiLv);
        SerialCache.staicSerialCache = this;
    }
    
    public int getIntiLv(final int inti) {
        for (int i = 0; i < this.intiLv.size(); ++i) {
            if (inti < this.intiLv.get(i)) {
                return i + 1;
            }
        }
        if (inti >= this.intiLv.get(this.intiLv.size() - 1)) {
            return this.intiLv.size();
        }
        return 1;
    }
    
    public int getLvMax(final int lv) {
        final int curMax = this.intiLv.get(lv - 1);
        int lastMax = 0;
        if (lv > 1) {
            lastMax = this.intiLv.get(lv - 2);
        }
        return curMax - lastMax;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.intiLv.clear();
    }
}
