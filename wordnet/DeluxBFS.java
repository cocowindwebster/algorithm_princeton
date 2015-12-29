package Week1WordNet;

import edu.princeton.cs.algs4.*;
import java.util.LinkedHashMap;

public class DeluxBFS{
    private LinkedHashMap<Integer, Integer> reachableV;
    public DeluxBFS(Digraph G, int s) {
        bfs(G, s);
    }


    public DeluxBFS(Digraph G, Iterable<Integer> sources) {
        bfs(G, sources);
    }

    // BFS from single source
    private void bfs(Digraph G, int s) {
        Queue<Integer> q = new Queue<Integer>();
        q.enqueue(s);
        reachableV = new LinkedHashMap<>();
        reachableV.put(s, 0);
        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (int w : G.adj(v)) {
                if (!reachableV.containsKey(w)) {
                    reachableV.put(w, reachableV.get(v) + 1);
                    q.enqueue(w);
                }
            }
        }
    }

    // BFS from multiple sources
    private void bfs(Digraph G, Iterable<Integer> sources) {
        Queue<Integer> q = new Queue<Integer>();
        reachableV = new LinkedHashMap<>();
        for (int s : sources) {
            reachableV.put(s, 0);
            q.enqueue(s);
        }

        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (int w : G.adj(v)) {
                 if (!reachableV.containsKey(w)) {
                    reachableV.put(w, reachableV.get(v) + 1);
                    q.enqueue(w);
                }
            }
        }
    }

    public boolean hasPathTo(int v) {
        return reachableV.containsKey(v);
    }


    public int distTo(int v) {
        return reachableV.get(v);
    }

    public LinkedHashMap<Integer, Integer> getReachableV() {
        return reachableV;
    }
}
