package Week1WordNet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashMap;

public class Outcast {
    private WordNet myWordNet;
    public Outcast(WordNet wordnet) {
        checkNull(wordnet);
        this.myWordNet = wordnet;
    }

    private void checkNull(Object ... args) {
        for (Object input : args) {
            if (input == null) {
                throw new java.lang.NullPointerException();
            }
        }
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new java.lang.NullPointerException();
        }
        for (String each : nouns) {
            checkNull(each);
        }
        HashMap<String, Integer> cache = new HashMap<String, Integer>();
        int maxDistance = Integer.MIN_VALUE;
        String outcastWord = "";
        for (int i = 0; i < nouns.length; ++i) {
            int distance = 0;
            for (int j = 0; j < i; ++j) {
                distance += cache.get(nouns[j] + "," + nouns[i]);
            }
            for (int j = i + 1; j < nouns.length; ++j) {
                int segmentDistance = myWordNet.distance(nouns[i], nouns[j]);
                distance += segmentDistance;
                cache.put(nouns[i] + "," + nouns[j], segmentDistance);
            }
            if (distance > maxDistance) {
                maxDistance = distance;
                outcastWord = nouns[i];
            }
        }
        return outcastWord;
    }


    // see test client below
    public static void main(String[] args) {
        String dir = "~/input/";
        String synsets = dir + "/" + "synsets.txt";
        String hypernyms = dir + "/" + "hypernyms.txt";
        String outcastFile1 = dir + "/" + "outcast5.txt";
        String outcastFile2 = dir + "/" + "outcast8.txt";
        String outcastFile3 = dir + "/" + "outcast11.txt";
        In in1 = new In(outcastFile1);
        In in2 = new In(outcastFile2);
        In in3 = new In(outcastFile3);
        WordNet wordnet = new WordNet(synsets, hypernyms);
        Outcast outcast = new Outcast(wordnet);
        StdOut.println("A" + ": " + outcast.outcast(in1.readAllLines()));
        StdOut.println("A" + ": " + outcast.outcast(in2.readAllLines()));
        StdOut.println("A" + ": " + outcast.outcast(in3.readAllLines()));
    }
}