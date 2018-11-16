package com.reign.gcld.world.graph;

import java.util.*;

public class MinPathFloy
{
    public static int INF;
    private int[][] dist;
    private int[][] path;
    
    static {
        MinPathFloy.INF = Integer.MAX_VALUE;
    }
    
    public void initMatrix(final int[][] matrix) {
        this.floyd(matrix);
    }
    
    public List<Integer> findCheapestPath(final int begin, final int end) {
        final List<Integer> result = new ArrayList<Integer>();
        result.add(begin);
        this.findPath(begin, end, result);
        result.add(end);
        return result;
    }
    
    public void findPath(final int i, final int j, final List<Integer> result) {
        final int k = this.path[i][j];
        if (k == -1) {
            return;
        }
        this.findPath(i, k, result);
        result.add(k);
        this.findPath(k, j, result);
    }
    
    public void floyd(final int[][] matrix) {
        final int size = matrix.length;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                this.path[i][j] = -1;
                this.dist[i][j] = matrix[i][j];
            }
        }
        for (int k = 0; k < size; ++k) {
            for (int l = 0; l < size; ++l) {
                for (int m = 0; m < size; ++m) {
                    if (this.dist[l][k] != MinPathFloy.INF && this.dist[k][m] != MinPathFloy.INF && this.dist[l][k] + this.dist[k][m] < this.dist[l][m]) {
                        this.dist[l][m] = this.dist[l][k] + this.dist[k][m];
                        this.path[l][m] = k;
                    }
                }
            }
        }
    }
    
    public MinPathFloy(final int size) {
        this.path = new int[size][size];
        this.dist = new int[size][size];
    }
    
    public int[][] getDist() {
        return this.dist;
    }
    
    public void setDist(final int[][] dist) {
        this.dist = dist;
    }
    
    public int[][] getPath() {
        return this.path;
    }
    
    public void setPath(final int[][] path) {
        this.path = path;
    }
    
    public static void main(final String[] args) {
        final MinPathFloy graph = new MinPathFloy(5);
        final int[][] matrix = { { MinPathFloy.INF, 30, MinPathFloy.INF, 10, 50 }, { MinPathFloy.INF, MinPathFloy.INF, 60, MinPathFloy.INF, MinPathFloy.INF }, { MinPathFloy.INF, MinPathFloy.INF, MinPathFloy.INF, MinPathFloy.INF, MinPathFloy.INF }, { MinPathFloy.INF, MinPathFloy.INF, MinPathFloy.INF, MinPathFloy.INF, 30 }, { 50, MinPathFloy.INF, 40, MinPathFloy.INF, MinPathFloy.INF } };
        graph.initMatrix(matrix);
        final int begin = 0;
        int end = 2;
        for (int i = 0; i < 5; ++i) {
            end = i;
            final List<Integer> list = graph.findCheapestPath(begin, end);
            System.out.println(String.valueOf(begin) + " to " + end + ",the cheapest path is:");
            System.out.println(list.toString());
            System.out.println(graph.dist[begin][end]);
            System.out.println();
        }
    }
}
