package com.reign.gcld.juben.common;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.graph.*;
import java.util.*;

@Component("juBenDataCache")
public class JuBenDataCache implements IJuBenDataCache
{
    @Autowired
    private SoloRoadCache soloRoadCache;
    @Autowired
    private SoloDramaCache soloDramaCache;
    @Autowired
    private SoloCityCache soloCityCache;
    Map<Integer, int[][]> numMap;
    Map<Integer, int[][]> disMap;
    Map<Integer, Map<Integer, Integer>> rdMap;
    Map<Integer, Map<Integer, Integer>> reRdMap;
    public static final int JUBEN_X_Y = 10000;
    
    public JuBenDataCache() {
        this.numMap = new HashMap<Integer, int[][]>();
        this.disMap = new HashMap<Integer, int[][]>();
        this.rdMap = new HashMap<Integer, Map<Integer, Integer>>();
        this.reRdMap = new HashMap<Integer, Map<Integer, Integer>>();
    }
    
    @Override
    public void initPath() {
        for (final SoloDrama sd : this.soloDramaCache.getModels()) {
            final Map<Integer, SoloCity> scMap = this.soloCityCache.getBySoloId(sd.getId());
            Map<Integer, Integer> tempMap = this.rdMap.get(sd.getId());
            Map<Integer, Integer> tempReRdMap = this.reRdMap.get(sd.getId());
            if (tempMap == null) {
                tempMap = new HashMap<Integer, Integer>();
                this.rdMap.put(sd.getId(), tempMap);
                tempReRdMap = new HashMap<Integer, Integer>();
                this.reRdMap.put(sd.getId(), tempReRdMap);
            }
            int num = 0;
            if (scMap == null) {
                continue;
            }
            for (final SoloCity sc : scMap.values()) {
                tempMap.put(sc.getId(), num);
                tempReRdMap.put(num, sc.getId());
                ++num;
            }
            final int[][] matrix_num = new int[scMap.size()][scMap.size()];
            final int[][] matrix_dis = new int[scMap.size()][scMap.size()];
            for (int i = 0; i < matrix_num.length; ++i) {
                for (int j = 0; j < matrix_num.length; ++j) {
                    matrix_num[i][j] = -1;
                    matrix_dis[i][j] = -1;
                    if (i == j) {
                        matrix_num[i][j] = 0;
                        matrix_dis[i][j] = 0;
                    }
                }
            }
            final Map<String, SoloRoad> roadMap = this.soloRoadCache.getRoadMap(sd.getId());
            for (final String key : roadMap.keySet()) {
                final SoloRoad road = roadMap.get(key);
                matrix_num[tempMap.get(road.getStart())][tempMap.get(road.getEnd())] = 1;
                matrix_num[tempMap.get(road.getEnd())][tempMap.get(road.getStart())] = 1;
                matrix_dis[tempMap.get(road.getStart())][tempMap.get(road.getEnd())] = road.getLength();
                matrix_dis[tempMap.get(road.getEnd())][tempMap.get(road.getStart())] = road.getLength();
            }
            this.numMap.put(sd.getId(), matrix_num);
            this.disMap.put(sd.getId(), matrix_dis);
        }
    }
    
    @Override
    public List<Integer> getMinPath(final int soLoId, int start, int end, final int[] arr) {
        final Map<Integer, Integer> tempMap = this.rdMap.get(soLoId);
        final Map<Integer, Integer> tempReRdMap = this.reRdMap.get(soLoId);
        start = tempMap.get(start);
        end = tempMap.get(end);
        int s = -1;
        int e = -1;
        int temp = 0;
        for (int i = 0; i < arr.length; ++i) {
            temp = tempMap.get(arr[i]);
            arr[i] = temp;
            if (start == arr[i]) {
                s = i;
            }
            if (end == arr[i]) {
                e = i;
            }
        }
        if (s == -1 || e == -1) {
            return null;
        }
        final List<Integer> list = ShortestPath.dijkstra(this.numMap.get(soLoId), this.disMap.get(soLoId), s, e, arr);
        for (int j = 0; j < list.size(); ++j) {
            list.set(j, tempReRdMap.get(list.get(j)));
        }
        return list;
    }
    
    @Override
    public List<Integer> getMinPathJuBen(final int soLoId, int start, int end, final int[] arr, final int[] x, final int[] y) {
        final Map<Integer, Integer> tempMap = this.rdMap.get(soLoId);
        final Map<Integer, Integer> tempReRdMap = this.reRdMap.get(soLoId);
        start = tempMap.get(start);
        end = tempMap.get(end);
        int s = -1;
        int e = -1;
        int temp = 0;
        for (int i = 0; i < arr.length; ++i) {
            temp = tempMap.get(arr[i]);
            arr[i] = temp;
            if (start == arr[i]) {
                s = i;
            }
            if (end == arr[i]) {
                e = i;
            }
        }
        if (s == -1 || e == -1) {
            return null;
        }
        final Set<Integer> set = new HashSet<Integer>();
        if (x != null) {
            for (int j = 0; j < x.length; ++j) {
                set.add(getXy(tempMap.get(x[j]), tempMap.get(y[j])));
            }
        }
        final List<Integer> list = ShortestPath.dijkstraJuBen(this.numMap.get(soLoId), this.disMap.get(soLoId), s, e, arr, set);
        for (int k = 0; k < list.size(); ++k) {
            list.set(k, tempReRdMap.get(list.get(k)));
        }
        return list;
    }
    
    public static int getXy(final int x, final int y) {
        int xy = 0;
        if (x < y) {
            xy = x * 10000 + y;
        }
        else {
            xy = y * 10000 + x;
        }
        return xy;
    }
}
