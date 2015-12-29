package Week1WordNet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SAP {
    private Digraph digraph;
    private int vertexCount;
    private HashMap<String, Integer> cacheLen;
    private HashMap<String, Integer> cacheAncestor;
    public SAP(Digraph G) { // skipping DAG check, since it is already done by the caller of SAP, i.e. WordNet,
        checkNull(G);
        this.digraph = new Digraph(G); // make it immutable
        this.vertexCount = digraph.V();
        this.cacheLen = new HashMap<>();
        this.cacheAncestor = new HashMap<>();
    }
    private void checkNull(Object ... args) {
        for (Object input : args) {
            if (input == null) {
                throw new java.lang.NullPointerException();
            }
        }
    }
    private void checkValidity(int ...args) {
        for (int i : args) {
            if (i >= vertexCount || i < 0) {
                throw new java.lang.IndexOutOfBoundsException();
            }
        }
    }
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        checkValidity(v, w);
        String key;
        if (v < w) {
            key = Integer.toString(v) + Integer.toString(w);
        } else {
            key = Integer.toString(w) + Integer.toString(v);
        }
        if (cacheLen.containsKey(key)) {
            return cacheLen.get(key);
        } else {
            DeluxBFS deluxBFSforV = new DeluxBFS(digraph, v);
            DeluxBFS deluxBFSforW = new DeluxBFS(digraph, w);
            LinkedHashMap<Integer, Integer> mapW = deluxBFSforW.getReachableV();
            int distance = 0, minDistance = Integer.MAX_VALUE;
            boolean changed = false;
            for (int reachableVertex : deluxBFSforV.getReachableV().keySet()) {
                if (mapW.containsKey(reachableVertex)) {
                    distance = deluxBFSforV.getReachableV().get(reachableVertex) + mapW.get(reachableVertex);
                    minDistance = Math.min(distance, minDistance);
                    changed = true;
                    if (distance > minDistance) {
                        if (deluxBFSforV.getReachableV().get(reachableVertex) > minDistance) {
                            break;
                        }
                    }
                }
            }
            if (changed) {
                cacheLen.put(key, minDistance);
                return minDistance;
            } else {
                cacheLen.put(key, -1);
                return -1;
            }
        }
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        checkValidity(v, w);
        DeluxBFS deluxBFSforV = new DeluxBFS(digraph, v);
        DeluxBFS deluxBFSforW = new DeluxBFS(digraph, w);
        String key;
        if (v < w) {
            key = Integer.toString(v) + Integer.toString(w);
        } else {
            key = Integer.toString(w) + Integer.toString(v);
        }
        if (cacheAncestor.containsKey(key)) {
            return cacheAncestor.get(key);
        } else {
            LinkedHashMap<Integer, Integer> mapW = deluxBFSforW.getReachableV();
            int distance = 0, minDistance = Integer.MAX_VALUE;
            int ancestor = -1;
            for (int reachableVertex : deluxBFSforV.getReachableV().keySet()) {
                if (mapW.containsKey(reachableVertex)) {
                    distance = deluxBFSforV.getReachableV().get(reachableVertex) + mapW.get(reachableVertex);
                    if (distance < minDistance) {
                        minDistance = Math.min(distance, minDistance);
                        ancestor = reachableVertex;
                    } else {
                        if (deluxBFSforV.getReachableV().get(reachableVertex) > minDistance) {
                            break;
                        }
                    }
                }
            }
            cacheAncestor.put(key, ancestor);
            return ancestor;
        }
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkNull(v, w);
        for (int i : v) {
            checkNull(i);
            checkValidity(i);
        }
        for (int i : w) {
            checkNull(i);
            checkValidity(i);
        }
        DeluxBFS deluxBFSforV = new DeluxBFS(digraph, v);
        DeluxBFS deluxBFSforW = new DeluxBFS(digraph, w);
        LinkedHashMap<Integer, Integer> mapW = deluxBFSforW.getReachableV();
        int distance = 0, minDistance = Integer.MAX_VALUE;
        boolean changed = false;
        for (int reachableVertex : deluxBFSforV.getReachableV().keySet()) {
            if (mapW.containsKey(reachableVertex)) {
                distance = deluxBFSforV.getReachableV().get(reachableVertex) + mapW.get(reachableVertex);
                minDistance = Math.min(distance, minDistance);
                changed = true;
                if (deluxBFSforV.getReachableV().get(reachableVertex) > minDistance) {
                    break;
                }
            }
        }
        if (changed) {
            return minDistance;
        } else {
            return -1;
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkNull(v, w);
        for (int i : v) {
            checkNull(i);
            checkValidity(i);
        }
        for (int i : w) {
            checkNull(i);
            checkValidity(i);
        }
        DeluxBFS deluxBFSforV = new DeluxBFS(digraph, v);
        DeluxBFS deluxBFSforW = new DeluxBFS(digraph, w);
        HashMap<Integer, Integer> mapW = deluxBFSforW.getReachableV();
        int distance = 0, minDistance = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int reachableVertex : deluxBFSforV.getReachableV().keySet()) {
            if (mapW.containsKey(reachableVertex)) {
                distance = deluxBFSforV.getReachableV().get(reachableVertex) + mapW.get(reachableVertex);
                if (distance < minDistance) {
                    minDistance = Math.min(distance, minDistance);
                    ancestor = reachableVertex;
                } else {
                    if (deluxBFSforV.getReachableV().get(reachableVertex) > minDistance ) {
                        break;
                    }
                }

            }
        }
        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        String dir = "~/input/";
        String graphInputFile = dir + "/" +  "digraph-wordnet.txt";
        In in = new In(graphInputFile);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            System.out.println("A");
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}

