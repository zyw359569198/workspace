package com.reign.gcld.world.common;

import com.reign.util.*;
import com.reign.gcld.common.*;
import java.io.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.sdata.domain.*;
import org.apache.commons.lang.*;
import java.util.*;

public class BoxLocation
{
    private final String path;
    private final String name = "RoadMap.txt";
    private Map<Integer, Tuple<Double, Double>> map;
    
    public BoxLocation() throws IOException {
        this.path = ListenerConstants.WEB_PATH;
        this.map = new HashMap<Integer, Tuple<Double, Double>>();
        final File f = new File(String.valueOf(this.path) + "RoadMap.txt");
        final FileReader reader = new FileReader(f);
        final BufferedReader bReader = new BufferedReader(reader);
        String temp = null;
        while ((temp = bReader.readLine()) != null) {
            final String[] s = temp.split("#");
            final int cityId = Integer.parseInt(s[0].split(":")[1]);
            final String[] xy = s[1].split("y");
            final double x = Double.valueOf(xy[0].split(":")[1]);
            final double y = Double.valueOf(xy[1].split(":")[1]);
            final Tuple<Double, Double> lTuple = new Tuple();
            lTuple.left = x;
            lTuple.right = y;
            this.map.put(cityId, lTuple);
        }
        bReader.close();
        reader.close();
    }
    
    public List<RoadDto> getRoadMap(final WorldRoadCache worldRoadCache) {
        final Map<String, WorldRoad> sw = worldRoadCache.getRoadMap();
        final List<RoadDto> list = new ArrayList<RoadDto>();
        final Set<Map.Entry<String, WorldRoad>> entry = sw.entrySet();
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double newX = 0.0;
        double newY = 0.0;
        WorldRoad tempRoad = null;
        for (final Map.Entry<String, WorldRoad> e : entry) {
            final String key = e.getKey();
            final String[] citys = key.split("-");
            final RoadDto dto = new RoadDto();
            dto.setKey(key);
            final Integer cityOne = Integer.valueOf(citys[0]);
            final Integer cityTwo = Integer.valueOf(citys[1]);
            x1 = this.map.get(cityOne).left;
            y1 = this.map.get(cityOne).right;
            x2 = this.map.get(cityTwo).left;
            y2 = this.map.get(cityTwo).right;
            newX = (x1 + x2) / 2.0 + 26.0;
            newY = (y1 + y2) / 2.0;
            dto.setX(newX);
            dto.setY(newY);
            tempRoad = e.getValue();
            final int wei = StringUtils.isBlank(tempRoad.getWeiReward()) ? 0 : Integer.valueOf(tempRoad.getWeiReward());
            final int wu = StringUtils.isBlank(tempRoad.getWuReward()) ? 0 : Integer.valueOf(tempRoad.getWuReward());
            final int shu = StringUtils.isBlank(tempRoad.getShuReward()) ? 0 : Integer.valueOf(tempRoad.getShuReward());
            dto.setWei(wei);
            dto.setShu(shu);
            dto.setWu(wu);
            dto.setId(tempRoad.getId());
            list.add(dto);
        }
        Collections.sort(list, new RoadComparator());
        return list;
    }
    
    class RoadComparator implements Comparator<RoadDto>
    {
        @Override
        public int compare(final RoadDto o1, final RoadDto o2) {
            if (o1.getId() >= o2.getId()) {
                return 1;
            }
            return 0;
        }
    }
}
