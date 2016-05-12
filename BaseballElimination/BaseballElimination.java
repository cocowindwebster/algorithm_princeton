package Week3BaseballElimination;
import edu.princeton.cs.algs4.*;
import java.util.*;

/**
 * Created by ff on 4/23/16.
 */
public class BaseballElimination {
    private class Team {
        private String name;
        private int id;
        private int winCount;
        private int loseCount;
        private int remainCount;

        public Team(String n, int i, int w, int l, int r) {
            this.name = n;
            this.id = i;
            this.winCount = w;
            this.loseCount = l;
            this.remainCount = r;
        }

        public String getName() { return this.name; }

        public int getId() {
            return this.id;
        }

        public int getWinCount() {
            return this.winCount;
        }

        public int getLoseCount() {
            return this.loseCount;
        }

        public int getRemainCount() {
            return this.remainCount;
        }

        public String toString() {
            return id + ", " + winCount  + ", " + loseCount  + ", " +remainCount + ", " + name + "\n";
        }
    }
    private Map<String, Integer> name2Id = new HashMap<>();
    private Map<Integer, Team> id2Team = new HashMap<>();
    private Set<String> setOfElimination = new HashSet<>();
    private Map<Integer, ArrayList<String>> certification = new  HashMap<>();
    private int[][] matrix = null;
    private int count = 0;
    private int totalGamesLeftToPlay = 0; // capacity of the flow

    public BaseballElimination(String filename) {
        // create a baseball division from given filename in format specified below
        if (filename == null || filename.length() == 0) {
            throw new java.lang.IllegalArgumentException();
        }

        init(filename); //1 initialization
        for (int i : id2Team.keySet()) {
            //2.1 trivial eliminate hidden in the if block
            if (!ifTrivialEliminated(i)) {
                //2.2 build flow network.
                String[] idMapping = new String [(count - 1) * (count - 2) / 2 + 2 + count - 1];
                int[] new_2_old = new int [count - 1];
                FlowNetwork flw  = buildFlownNetWork(i, idMapping, new_2_old);
                //2.3 run FordFulkerson
                FordFulkerson ff = new FordFulkerson(flw, 0, idMapping.length - 1);
//                StdOut.printf("-------A1--------flownetwork for %d, %s[:\n", i, id2Team.get(i).getName());
//                StdOut.println(flw.toString());
//                StdOut.printf("totalGamesLeftToPlay for %s: %d\n", id2Team.get(i).getName(), totalGamesLeftToPlay );
//                StdOut.printf("-------A1--------flownetwork for %d, %s:]\n\n", i, id2Team.get(i).getName());
                if (ff.value() !=  totalGamesLeftToPlay) {
                    setOfElimination.add(id2Team.get(i).getName());
                    //StdOut.printf("Eliminated: %s flow/capacity: %f/%d\n\n\n\n", id2Team.get(i).getName(), ff.value(),
                    // totalGamesLeftToPlay);
                    //2.3.1 find certification of elimination.
                    for (int k = 0; k < count - 1; ++k) {
                        int index = idMapping.length - k - 2;
                        if (ff.inCut(index)) {
                            if (!certification.containsKey(i)) {
                                certification.put(i, new ArrayList<String>());
                            }
                            certification.get(i).add(id2Team.get(new_2_old[index - (count - 1) * (count - 2) / 2  - 1]).
                                    getName());
                        }
                    }
//                    StdOut.printf("%s %s%s.\n\n\n", id2Team.get(i).getName(), "Eliminated by: ",
//                     certification.get(i).toString());
                } else {
                    //StdOut.printf("Not Eliminated: %s flow/capacity: %f/%d\n\n\n\n", id2Team.get(i).getName(),
                    // ff.value(), totalGamesLeftToPlay);
                }

            }
        }
    }

    private boolean init(String filename) {
        In file = new In(filename);
        if (file.hasNextLine()) {
            this.count = Integer.parseInt(file.readLine());
            this.matrix = new int [this.count][this.count];
        }
        while (file.hasNextLine()) {
            String[] listOfAllLines = file.readAllLines();
            for (int lineIndex  = 0; lineIndex < listOfAllLines.length; ++lineIndex) {
                String [] team_info = listOfAllLines[lineIndex].trim().split("\\s+");
                Team team = new Team(
                        team_info[0],
                        lineIndex,
                        Integer.parseInt(team_info[1]),
                        Integer.parseInt(team_info[2]),
                        Integer.parseInt(team_info[3])
                );
                //StdOut.print(team.toString());
                for (int i = 4; i < team_info.length; ++i) {
                    matrix[lineIndex][i - 4] = Integer.parseInt(team_info[i]);
                }
                name2Id.put(team_info[0], lineIndex);
                id2Team.put(lineIndex, team);
            }
        }
        return true;
    }
    private boolean ifTrivialEliminated(int id) {
        int maxWin = id2Team.get(id).getWinCount() + id2Team.get(id).getRemainCount();
        boolean isTrivial = false;
        for (int other : id2Team.keySet()) {
            if (id2Team.get(other).getWinCount() > maxWin) {
                if (!certification.containsKey(id)) {
                    certification.put(id, new ArrayList<String>());
                }
                certification.get(id).add(id2Team.get(other).getName());
                isTrivial = true;
                setOfElimination.add(id2Team.get(id).getName());
                //StdOut.printf("Trivial Elimination TRUE: %s is eliminated by %s\n\n\n",
                // id2Team.get(id).getName(), id2Team.get(other).getName());
            }
        }
        return isTrivial;
    }

    private FlowNetwork buildFlownNetWork(int i, String[] idMapping, int [] new2old) {
        FlowNetwork flw = new FlowNetwork((count- 1) * (count- 2) / 2 + 2 + count - 1);

        for (int k = 0; k < i; ++k) {
            new2old[k] = k;
        }
        for (int k = i; k < count - 1; ++k) {
            new2old[k] = k + 1;
        }
        int index = 1;
        totalGamesLeftToPlay = 0;
        for (int first = 0; first < count - 1; ++first) {
            for (int second = first + 1; second < count - 1; ++second) {
                flw.addEdge(new FlowEdge(0, index, (double) matrix[new2old[first]][new2old[second]]));
                flw.addEdge(new FlowEdge(index, 1 + (count - 1) * (count - 2) / 2  + first, Double.POSITIVE_INFINITY));
                flw.addEdge(new FlowEdge(index, 1 + (count - 1) * (count - 2) / 2  + second, Double.POSITIVE_INFINITY));
                totalGamesLeftToPlay += matrix[new2old[first]][new2old[second]];
                ++index;
            }
        }
        for (int first = 0; first < count - 1; ++first) {
            int tieValue = id2Team.get(i).getWinCount() + id2Team.get(i).getRemainCount();
            flw.addEdge(new FlowEdge(index, idMapping.length - 1, tieValue - id2Team.get(new2old[first]).getWinCount()));
            ++index;
        }
        return flw;
    }

    public  int numberOfTeams() {
        return this.count;
    }

    public Iterable<String> teams() {
        return name2Id.keySet();
    }

    public int wins(String team) {
        if (!name2Id.containsKey(team)) {
            throw new java.lang.IllegalArgumentException();
        }
        return id2Team.get(name2Id.get(team)).getWinCount();
    }

    public int losses(String team) {
        if (!name2Id.containsKey(team)) {
            throw new java.lang.IllegalArgumentException();
        }
        return id2Team.get(name2Id.get(team)).getLoseCount();
    }

    public int remaining(String team) {
        if (!name2Id.containsKey(team)) {
            throw new java.lang.IllegalArgumentException();
        }
        return id2Team.get(name2Id.get(team)).getRemainCount();
    }

    public int against(String team1, String team2) {
        // number of remaining games between team1 and team2
        if ((!name2Id.containsKey(team1)) || (!name2Id.containsKey(team2))) {
            throw new java.lang.IllegalArgumentException();
        }
        int id1 = name2Id.get(team1);
        int id2 = name2Id.get(team2);
        return matrix[id1][id2];
    }

    public boolean isEliminated(String team) {
        if (!name2Id.containsKey(team)) {
            throw new java.lang.IllegalArgumentException();
        }
        return setOfElimination.contains(team);
    }

    public Iterable<String> certificateOfElimination(String team) {
        if (!name2Id.containsKey(team)) {
            throw new java.lang.IllegalArgumentException();
        }
        if (isEliminated(team)) {
            return certification.get(name2Id.get(team));
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String file1 = "/Users/feliciafay/FeliciaAll/1AlgorithmDataStructure/Coursera_Princeton_ALGS2/Lab/" +
                "3BaseballElimination/baseball-testing/teams54.txt";
        BaseballElimination be = new BaseballElimination(file1);
    }
}
