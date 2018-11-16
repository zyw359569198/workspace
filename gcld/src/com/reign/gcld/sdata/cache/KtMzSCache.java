package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.util.*;

@Component("KtMzSCache")
public class KtMzSCache extends AbstractCache<Integer, KtMzS>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<KtMzS> list;
    private Set<Integer> touFangCitySet;
    
    public KtMzSCache() {
        this.list = null;
        this.touFangCitySet = new HashSet<Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final Set<Integer> citySet = new HashSet<Integer>();
        final List<WorldCity> cityList = this.dataLoader.getModels((Class)WorldCity.class);
        for (final WorldCity wc : cityList) {
            citySet.add(wc.getId());
        }
        this.list = this.dataLoader.getModels((Class)KtMzS.class);
        for (final KtMzS ktmzs : this.list) {
            final String[] weiCityArray = ktmzs.getWei().split(";");
            final String[] shuCityArray = ktmzs.getShu().split(";");
            final String[] wuCityArray = ktmzs.getWu().split(";");
            int cityId = 0;
            try {
                String[] array;
                for (int length = (array = weiCityArray).length, i = 0; i < length; ++i) {
                    final String s = array[i];
                    cityId = Integer.parseInt(s);
                    ktmzs.getWeiSet().add(cityId);
                }
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("KtMzSCache init fail in parse weiCityArray. id=" + ktmzs.getId());
            }
            try {
                String[] array2;
                for (int length2 = (array2 = shuCityArray).length, j = 0; j < length2; ++j) {
                    final String s = array2[j];
                    cityId = Integer.parseInt(s);
                    ktmzs.getShuSet().add(cityId);
                }
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("KtMzSCache init fail in parse shuCityArray. id=" + ktmzs.getId());
            }
            try {
                String[] array3;
                for (int length3 = (array3 = wuCityArray).length, k = 0; k < length3; ++k) {
                    final String s = array3[k];
                    cityId = Integer.parseInt(s);
                    ktmzs.getWuSet().add(cityId);
                }
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("KtMzSCache init fail in parse wuCityArray. id=" + ktmzs.getId());
            }
            this.touFangCitySet.addAll(ktmzs.getWeiSet());
            this.touFangCitySet.addAll(ktmzs.getShuSet());
            this.touFangCitySet.addAll(ktmzs.getWuSet());
            super.put((Object)ktmzs.getId(), (Object)ktmzs);
        }
    }
    
    public KtMzS getKtMzSByTime(final long timeDifference) {
        if (timeDifference < 0L) {
            ErrorSceneLog.getInstance().appendErrorMsg("timeDifference is negative").append("timeDifference", timeDifference).appendClassName("KtMzSCache").flush();
            return null;
        }
        final long minuteCount = timeDifference / 60000L;
        KtMzS result = null;
        for (final KtMzS temp : this.list) {
            if (temp.getT() > minuteCount) {
                break;
            }
            result = temp;
        }
        return result;
    }
    
    public Tuple<Integer, Long> getNextInvadeInfo(final long startTime) {
        final long now = System.currentTimeMillis();
        if (startTime <= 0L || startTime > now) {
            ErrorSceneLog.getInstance().appendErrorMsg("startTime is valid").append("startTime", startTime).append("now", now).appendClassName("KtMzSCache").flush();
            return null;
        }
        final long timeDiff = now - startTime;
        KtMzS nextKtMzS = null;
        for (int i = this.list.size(); i > 0; --i) {
            final KtMzS temp = this.list.get(i - 1);
            if (timeDiff >= temp.getT() * 60000L) {
                break;
            }
            nextKtMzS = temp;
        }
        final Tuple<Integer, Long> result = new Tuple();
        if (nextKtMzS == null) {
            result.left = 0;
            result.right = 0L;
        }
        else {
            final int round = nextKtMzS.getId();
            final long countDown = nextKtMzS.getT() * 60000L - timeDiff;
            result.left = round;
            result.right = countDown;
        }
        return result;
    }
    
    public List<KtMzS> getKtMzSList() {
        return this.list;
    }
    
    public Set<Integer> getTouFangCitySet() {
        return this.touFangCitySet;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.list.clear();
    }
}
