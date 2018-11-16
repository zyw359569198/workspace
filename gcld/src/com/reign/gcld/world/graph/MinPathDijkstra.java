package com.reign.gcld.world.graph;

public class MinPathDijkstra
{
    public static int INF;
    public static int[][] matrix;
    
    static {
        MinPathDijkstra.INF = Integer.MAX_VALUE;
        MinPathDijkstra.matrix = new int[280][280];
    }
    
    public MinPathDijkstra() {
        for (int i = 0; i < 280; ++i) {
            for (int j = 0; j < 280; ++j) {
                MinPathDijkstra.matrix[i][j] = MinPathDijkstra.INF;
            }
        }
    }
    
    public void shorttesPath(final int v, final int[] open) {
        final int[] dist = new int[open.length];
        final int[] s = new int[open.length];
        final int[] path = new int[open.length];
        for (int i = 0; i < open.length; ++i) {
            dist[i] = MinPathDijkstra.matrix[v][i];
            s[i] = 0;
            if (i != v && dist[i] < MinPathDijkstra.INF) {
                path[i] = v;
            }
            else {
                path[i] = -1;
            }
        }
        s[v] = 1;
        dist[v] = 0;
        for (int i = 0; i < open.length; ++i) {
            int min = MinPathDijkstra.INF;
            int u = v;
            for (int j = 0; j < open.length; ++j) {
                if (s[j] == 0 && dist[j] < min) {
                    u = j;
                    min = dist[j];
                }
            }
            s[u] = 1;
            for (int w = 0; w < open.length; ++w) {
                if (s[w] == 0 && MinPathDijkstra.matrix[u][w] > 0 && min + MinPathDijkstra.matrix[u][w] < dist[w]) {
                    dist[w] = min + MinPathDijkstra.matrix[u][w];
                    path[w] = u;
                }
            }
        }
        for (int i = 0; i < path.length; ++i) {}
    }
    
    public static void main(final String[] args) {
        final MinPathDijkstra mp = new MinPathDijkstra();
        final int[] open = { 0, 1, 2, 3, 4 };
        mp.shorttesPath(1, open);
    }
}
