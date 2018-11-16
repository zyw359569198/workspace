package com.reign.kf.match.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.sdata.common.*;
import com.reign.kf.match.sdata.domain.*;
import org.apache.commons.lang.*;
import java.util.*;

@Component("hallsCache")
public class HallsCache extends AbstractCache<Integer, Halls>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<String, ChiefNpc> cnMap;
    private HashMap<Integer, List<Halls>> hallsMap;
    private HashMap<Integer, List<Halls>> officialHallsMap;
    private Map<Integer, Map<Integer, Integer>> officerOutputMap;
    private List<Integer> tokenList;
    public static final int KINGPOS = 1;
    public static HallsCache staticHallCache;
    
    static {
        HallsCache.staticHallCache = null;
    }
    
    public HallsCache() {
        this.cnMap = new HashMap<String, ChiefNpc>();
        this.hallsMap = new HashMap<Integer, List<Halls>>();
        this.officialHallsMap = new HashMap<Integer, List<Halls>>();
        this.officerOutputMap = new HashMap<Integer, Map<Integer, Integer>>();
        this.tokenList = new ArrayList<Integer>();
    }
    
    public static Halls getHallsById(final int id) {
        return (Halls)HallsCache.staticHallCache.get((Object)id);
    }
    
    public List<Integer> getTokenList() {
        return this.tokenList;
    }
    
    public void setTokenList(final List<Integer> tokenList) {
        this.tokenList = tokenList;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Halls> list = this.dataLoader.getModels((Class)Halls.class);
        final List<General> gList = this.dataLoader.getModels((Class)General.class);
        final Set<Integer> gSet = new HashSet<Integer>();
        for (final General general : gList) {
            gSet.add(general.getId());
        }
        for (final Halls halls : list) {
            super.put((Object)halls.getPri(), (Object)halls);
            if (halls.getOrder() != 0) {
                this.tokenList.add(halls.getPri());
            }
            if (halls.getId() != 0) {
                List<Halls> tempList = this.hallsMap.get(halls.getId());
                if (tempList == null) {
                    tempList = new ArrayList<Halls>();
                    this.hallsMap.put(halls.getId(), tempList);
                }
                tempList.add(halls);
                List<Halls> officialList = this.officialHallsMap.get(halls.getOfficialId());
                if (officialList == null) {
                    officialList = new ArrayList<Halls>();
                    this.officialHallsMap.put(halls.getOfficialId(), officialList);
                }
                officialList.add(halls);
            }
            final String fight_key = getKey(halls.getId(), halls.getDegree());
            if (!this.cnMap.containsKey(fight_key)) {
                final ChiefNpc cn = new ChiefNpc();
                cn.setCheif(halls.getChief());
                final List<Integer> npcList = new ArrayList<Integer>();
                if (!StringUtils.isBlank(halls.getNpcs())) {
                    String[] split;
                    for (int length = (split = halls.getNpcs().split(";")).length, i = 0; i < length; ++i) {
                        final String str = split[i];
                        npcList.add(Integer.valueOf(str.trim()));
                    }
                }
                cn.setNpcList(npcList);
                this.cnMap.put(fight_key, cn);
            }
            final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            final String outPut = String.valueOf(halls.getBaseReward()) + ";" + halls.getIronReward();
            final String[] outputArrays = outPut.split(";");
            String[] array;
            for (int length2 = (array = outputArrays).length, j = 0; j < length2; ++j) {
                final String outputArray = array[j];
                outputArray.split(",");
            }
            this.officerOutputMap.put(halls.getPri(), map);
        }
        HallsCache.staticHallCache = this;
    }
    
    public Halls getHalls(final int buildingId, final int degree) {
        final List<Halls> tempList = this.hallsMap.get(buildingId);
        for (final Halls ha : tempList) {
            if (ha.getDegree() == degree) {
                return ha;
            }
        }
        return this.hallsMap.get(buildingId).get(degree - 1);
    }
    
    public List<Halls> getHalls(final int buildingId) {
        return this.hallsMap.get(buildingId);
    }
    
    public int getOutputByType(final int id, final int type) {
        final Map<Integer, Integer> map = this.officerOutputMap.get(id);
        Integer value = null;
        return (map == null) ? 0 : (((value = map.get(type)) == null) ? 0 : value);
    }
    
    public Halls getNextHalls(final int officerId) {
        final Halls nowHalls = (Halls)super.get((Object)officerId);
        if (nowHalls.getOfficialId() > 1) {
            final int nextOfficialId = nowHalls.getOfficialId() - 1;
            final List<Halls> optionalList = this.officialHallsMap.get(nextOfficialId);
            Halls result = optionalList.get(0);
            for (final Halls temp : optionalList) {
                if (temp.getId() > result.getId()) {
                    result = temp;
                }
            }
            return result;
        }
        return null;
    }
    
    public static String getKey(final int buildingId, final int nationId) {
        return String.valueOf(buildingId) + "_" + nationId;
    }
    
    public ChiefNpc getChiefNpc(final int buildingId, final int nationId) {
        return this.cnMap.get(getKey(buildingId, nationId));
    }
    
    @Override
	public void clear() {
        super.clear();
        this.cnMap.clear();
        this.hallsMap.clear();
        this.officialHallsMap.clear();
        this.officerOutputMap.clear();
        this.tokenList.clear();
    }
}
