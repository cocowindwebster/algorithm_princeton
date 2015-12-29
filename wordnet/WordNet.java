package Week1WordNet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class WordNet {
    private Digraph digraph;
    private int synsetsCount; //equals Vertex, different from noun count
    private TreeMap<String, Set<Integer>> word2ID;
    private HashMap<Integer, String> iD2word;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        checkNull(synsets, hypernyms);
        word2ID = new TreeMap<String, Set<Integer>>();
        iD2word = new HashMap<Integer, String>();
        populateSynsets(synsets);
        digraph = new Digraph(synsetsCount);
        populateHypernyms(hypernyms);
        noCycleCheck();
        noTwoRootsCheck();
        sap = new SAP(digraph);
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
            if (i >= synsetsCount || i < 0) {
                throw new java.lang.IndexOutOfBoundsException();
            }
        }
    }

    private void noCycleCheck() {
        HashMap<Integer, Integer> visited = new HashMap<>();
        for (int current = 0; current < digraph.V(); ++current) {
            if (!dfs(visited, current)) {
                throw new java.lang.IllegalArgumentException();
            }
        }
    }

    private void noTwoRootsCheck() {
        int count = 0;
        for (int current = 0; current < digraph.V(); ++current) {
            if (digraph.outdegree(current) == 0) {
                ++count;
                if (count > 1) {
                    throw new java.lang.IllegalArgumentException();
                }
            }
        }
        return;
    }

    private boolean dfs(HashMap<Integer, Integer> visited, int current) {
        if (visited.containsKey(current)) {
            return visited.get(current) != -1;
        }
        visited.put(current, -1);
        for (int child : digraph.adj(current)) {
            if (!dfs(visited, child)) {
                return false;
            }
        }
        visited.put(current, 1);
        return true;
    }

    private void populateSynsets(String synsets) {
        String eachLine = "";
        //1 synsets
        In in1 = new In(synsets);
        while (!in1.isEmpty()) {
            eachLine = in1.readLine();
            String [] splitted = eachLine.split(",");
            if (splitted[1] != null && !splitted[1].equals("")) {
                ++synsetsCount;
                String [] wordsFromSynsets = splitted[1].split(" ");
                for (String wordFromSynset : wordsFromSynsets) {
                    if (!word2ID.containsKey(wordFromSynset)) {
                        word2ID.put(wordFromSynset, new HashSet<Integer>());
                    }
                    word2ID.get(wordFromSynset).add(Integer.parseInt(splitted[0]));
                }
                iD2word.put(Integer.parseInt(splitted[0]), splitted[1]);
            }
        }
    }

    private void populateHypernyms(String hypernyms) {
        //2 hypernyms
        In in2 = new In(hypernyms);
        String eachLine;
        while (!in2.isEmpty()) {
            eachLine = in2.readLine();
            String [] splitted =  eachLine.split(",");
            for (int i = 1; i < splitted.length; ++i) {
                digraph.addEdge(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[i]));
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return word2ID.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        checkNull(word);
        return word2ID.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkNull(nounA, nounB);
        if (!word2ID.containsKey(nounA) || !word2ID.containsKey(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }
        Set<Integer> synsetsA = word2ID.get(nounA);
        Set<Integer> synsetsB = word2ID.get(nounB);
        return sap.length(synsetsA, synsetsB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkNull(nounA, nounB);
        if (!word2ID.containsKey(nounA) || !word2ID.containsKey(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }
        Set<Integer> synsetsA = word2ID.get(nounA);
        Set<Integer> synsetsB = word2ID.get(nounB);
        int ancestor = sap.ancestor(synsetsA, synsetsB);
        if (iD2word.containsKey(ancestor)) {
            return iD2word.get(ancestor);
        } else {
            return "";
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        String dir = "~/input/";
        String synsets = dir + "synsets6.txt";
        String hypernyms = dir + "hypernyms6InvalidTwoRoots.txt";
        WordNet wordNet = new WordNet(synsets, hypernyms);
        System.out.println(wordNet.isNoun("acre"));
        System.out.println(wordNet.isNoun("Adams"));
    }
}

