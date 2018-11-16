package com.reign.gcld.world.graph;

import java.util.*;
import com.reign.gcld.juben.common.*;

public class ShortestPath
{
    public static int INF;
    public static int[][] matrix;
    
    static {
        ShortestPath.INF = Integer.MAX_VALUE;
        ShortestPath.matrix = new int[280][280];
    }
    
    public ShortestPath() {
        for (int i = 0; i < 280; ++i) {
            for (int j = 0; j < 280; ++j) {
                ShortestPath.matrix[i][j] = ShortestPath.INF;
            }
        }
    }
    
    public static List<Integer> dijkstra(final int[][] matrix_num, final int[][] matrix_dis, final int start, final int end, final int[] arr) {
        final boolean[] isLabel = new boolean[arr.length];
        final int[] indexs = new int[arr.length];
        int i_count = -1;
        final int[] distance = new int[arr.length];
        final int[] distanceDis = new int[arr.length];
        final int[] forIndex = new int[arr.length];
        for (int i = 0; i < distance.length; ++i) {
            distance[i] = matrix_num[arr[start]][arr[i]];
            distanceDis[i] = matrix_dis[arr[start]][arr[i]];
            forIndex[i] = start;
        }
        int index = start;
        int presentShortest = 0;
        int presentShortestDis = 0;
        isLabel[indexs[++i_count] = index] = true;
        while (i_count < arr.length) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < distance.length; ++j) {
                if (!isLabel[j] && distance[j] != -1 && j != index && distance[j] < min) {
                    min = distance[j];
                    index = j;
                }
            }
            if (min == Integer.MAX_VALUE) {
                break;
            }
            isLabel[index] = true;
            if ((indexs[++i_count] = index) == end) {
                break;
            }
            if (matrix_num[arr[indexs[i_count - 1]]][arr[index]] == -1 || presentShortest + matrix_num[arr[indexs[i_count - 1]]][arr[index]] > distance[index]) {
                presentShortest = distance[index];
                presentShortestDis = distanceDis[index];
            }
            else {
                presentShortest += matrix_num[arr[indexs[i_count - 1]]][arr[index]];
                presentShortestDis += matrix_dis[arr[indexs[i_count - 1]]][arr[index]];
            }
            for (int j = 0; j < distance.length; ++j) {
                if (distance[j] == -1 && matrix_num[arr[index]][arr[j]] != -1) {
                    distance[j] = presentShortest + matrix_num[arr[index]][arr[j]];
                    distanceDis[j] = presentShortestDis + matrix_dis[arr[index]][arr[j]];
                    forIndex[j] = index;
                }
                else if (matrix_num[arr[index]][arr[j]] != -1 && presentShortest + matrix_num[arr[index]][arr[j]] <= distance[j]) {
                    if (presentShortest + matrix_num[arr[index]][arr[j]] < distance[j]) {
                        distance[j] = presentShortest + matrix_num[arr[index]][arr[j]];
                        distanceDis[j] = presentShortestDis + matrix_dis[arr[index]][arr[j]];
                        forIndex[j] = index;
                    }
                    else if (presentShortestDis + matrix_dis[arr[index]][arr[j]] < distanceDis[j]) {
                        distance[j] = presentShortest + matrix_num[arr[index]][arr[j]];
                        distanceDis[j] = presentShortestDis + matrix_dis[arr[index]][arr[j]];
                        forIndex[j] = index;
                    }
                }
            }
        }
        final List<Integer> list = new ArrayList<Integer>();
        if (distance[end] - distance[start] <= 0) {
            return list;
        }
        final int[] res = new int[forIndex.length];
        int m = 0;
        int n = end;
        res[m++] = arr[end];
        while (start != forIndex[n] && m <= forIndex.length) {
            res[m] = arr[forIndex[n]];
            n = forIndex[n];
            ++m;
        }
        res[m] = arr[start];
        for (int k = m; k >= 0; --k) {
            list.add(res[k]);
        }
        return list;
    }
    
    public static List<Integer> dijkstraExceptFireCities(final int[][] matrix_num, final int[][] matrix_dis, final int start, final int end, final int[] arr, final Set<Integer> fireSet) {
        final boolean[] isLabel = new boolean[arr.length];
        final int[] indexs = new int[arr.length];
        int i_count = -1;
        final int[] distance = new int[arr.length];
        final int[] distanceDis = new int[arr.length];
        final int[] forIndex = new int[arr.length];
        for (int i = 0; i < distance.length; ++i) {
            distance[i] = matrix_num[arr[start]][arr[i]];
            distanceDis[i] = matrix_dis[arr[start]][arr[i]];
            forIndex[i] = start;
        }
        int index = start;
        int presentShortest = 0;
        int presentShortestDis = 0;
        isLabel[indexs[++i_count] = index] = true;
        while (i_count < arr.length) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < distance.length; ++j) {
                if (!isLabel[j] && distance[j] != -1 && j != index && distance[j] < min) {
                    min = distance[j];
                    index = j;
                }
            }
            if (min == Integer.MAX_VALUE) {
                break;
            }
            isLabel[index] = true;
            if ((indexs[++i_count] = index) == end) {
                break;
            }
            if (matrix_num[arr[indexs[i_count - 1]]][arr[index]] == -1 || presentShortest + matrix_num[arr[indexs[i_count - 1]]][arr[index]] > distance[index] || fireSet.contains(arr[indexs[i_count - 1]])) {
                presentShortest = distance[index];
                presentShortestDis = distanceDis[index];
            }
            else {
                presentShortest += matrix_num[arr[indexs[i_count - 1]]][arr[index]];
                presentShortestDis += matrix_dis[arr[indexs[i_count - 1]]][arr[index]];
            }
            for (int j = 0; j < distance.length; ++j) {
                if (distance[j] == -1 && matrix_num[arr[index]][arr[j]] != -1 && !fireSet.contains(arr[index])) {
                    distance[j] = presentShortest + matrix_num[arr[index]][arr[j]];
                    distanceDis[j] = presentShortestDis + matrix_dis[arr[index]][arr[j]];
                    forIndex[j] = index;
                }
                else if (matrix_num[arr[index]][arr[j]] != -1 && presentShortest + matrix_num[arr[index]][arr[j]] <= distance[j] && !fireSet.contains(arr[index])) {
                    if (presentShortest + matrix_num[arr[index]][arr[j]] < distance[j]) {
                        distance[j] = presentShortest + matrix_num[arr[index]][arr[j]];
                        distanceDis[j] = presentShortestDis + matrix_dis[arr[index]][arr[j]];
                        forIndex[j] = index;
                    }
                    else if (presentShortestDis + matrix_dis[arr[index]][arr[j]] < distanceDis[j]) {
                        distance[j] = presentShortest + matrix_num[arr[index]][arr[j]];
                        distanceDis[j] = presentShortestDis + matrix_dis[arr[index]][arr[j]];
                        forIndex[j] = index;
                    }
                }
            }
        }
        final List<Integer> list = new ArrayList<Integer>();
        if (distance[end] - distance[start] <= 0) {
            return list;
        }
        final int[] res = new int[forIndex.length];
        int m = 0;
        int n = end;
        res[m++] = arr[end];
        while (start != forIndex[n] && m <= forIndex.length) {
            res[m] = arr[forIndex[n]];
            n = forIndex[n];
            ++m;
        }
        res[m] = arr[start];
        for (int k = m; k >= 0; --k) {
            list.add(res[k]);
        }
        return list;
    }
    
    public static List<Integer> dijkstraJuBen(final int[][] matrix_num, final int[][] matrix_dis, final int start, final int end, final int[] arr, final Set<Integer> set) {
        final boolean[] isLabel = new boolean[arr.length];
        final int[] indexs = new int[arr.length];
        int i_count = -1;
        final int[] distance = new int[arr.length];
        final int[] distanceDis = new int[arr.length];
        final int[] forIndex = new int[arr.length];
        for (int i = 0; i < distance.length; ++i) {
            distance[i] = matrix_num[arr[start]][arr[i]];
            distanceDis[i] = matrix_dis[arr[start]][arr[i]];
            forIndex[i] = start;
        }
        int index = start;
        int presentShortest = 0;
        int presentShortestDis = 0;
        isLabel[indexs[++i_count] = index] = true;
        while (i_count < arr.length) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < distance.length; ++j) {
                if (!isLabel[j] && distance[j] != -1 && j != index && distance[j] < min) {
                    min = distance[j];
                    index = j;
                }
            }
            if (min == Integer.MAX_VALUE) {
                break;
            }
            isLabel[index] = true;
            if ((indexs[++i_count] = index) == end) {
                break;
            }
            if (matrix_num[arr[indexs[i_count - 1]]][arr[index]] == -1 || set.contains(JuBenDataCache.getXy(arr[indexs[i_count - 1]], arr[index])) || presentShortest + matrix_num[arr[indexs[i_count - 1]]][arr[index]] > distance[index]) {
                presentShortest = distance[index];
                presentShortestDis = distanceDis[index];
            }
            else {
                presentShortest += matrix_num[arr[indexs[i_count - 1]]][arr[index]];
                presentShortestDis += matrix_dis[arr[indexs[i_count - 1]]][arr[index]];
            }
            for (int j = 0; j < distance.length; ++j) {
                if (distance[j] == -1 && matrix_num[arr[index]][arr[j]] != -1 && !set.contains(JuBenDataCache.getXy(arr[index], arr[j]))) {
                    distance[j] = presentShortest + matrix_num[arr[index]][arr[j]];
                    distanceDis[j] = presentShortestDis + matrix_dis[arr[index]][arr[j]];
                    forIndex[j] = index;
                }
                else if (matrix_num[arr[index]][arr[j]] != -1 && !set.contains(JuBenDataCache.getXy(arr[index], arr[j])) && presentShortest + matrix_num[arr[index]][arr[j]] <= distance[j]) {
                    if (presentShortest + matrix_num[arr[index]][arr[j]] < distance[j]) {
                        distance[j] = presentShortest + matrix_num[arr[index]][arr[j]];
                        distanceDis[j] = presentShortestDis + matrix_dis[arr[index]][arr[j]];
                        forIndex[j] = index;
                    }
                    else if (presentShortestDis + matrix_dis[arr[index]][arr[j]] < distanceDis[j]) {
                        distance[j] = presentShortest + matrix_num[arr[index]][arr[j]];
                        distanceDis[j] = presentShortestDis + matrix_dis[arr[index]][arr[j]];
                        forIndex[j] = index;
                    }
                }
            }
        }
        final List<Integer> list = new ArrayList<Integer>();
        if (distance[end] - distance[start] <= 0) {
            return list;
        }
        final int[] res = new int[forIndex.length];
        int m = 0;
        int n = end;
        res[m++] = arr[end];
        while (start != forIndex[n] && m <= forIndex.length) {
            res[m] = arr[forIndex[n]];
            n = forIndex[n];
            ++m;
        }
        res[m] = arr[start];
        for (int k = m; k >= 0; --k) {
            list.add(res[k]);
        }
        return list;
    }
    
    public static void main(final String[] args) {
        final int[][] W3 = { { 0, 1, 4, -1, -1, -1, -1 }, { 1, 0, 2, 7, 5, -1, -1 }, { 4, 2, 0, -1, 1, -1, -1 }, { -1, 7, -1, 0, 3, -1, 1 }, { -1, 5, 1, 3, 0, -1, -1 }, { -1, -1, -1, -1, -1, 0, -1 }, { -1, -1, -1, 1, -1, -1, 0 } };
        final int[] arr = { 0, 1, 2, 3, 4, 5, 6 };
        dijkstra(W3, W3, 0, 5, arr);
    }
}
