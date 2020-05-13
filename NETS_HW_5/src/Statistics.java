import java.util.*;

public class Statistics {
    private Parse teamParser;
    //List to store the points each player has collected based on their career stats
    private List<PlayerEntry> totalPts;

    /**
     * Constructor for the statistics class. Makes all preliminary calls to functions in Parse.Java
     * to load in all data needed to collect the stats for players.
     *
     * @param - N/A
     * @return - N/A
     */
    public Statistics() {
        teamParser = new Parse();
        totalPts = new ArrayList<PlayerEntry>();
        teamParser.getTeamsStatsPages();
        teamParser.getTeamsGamesPages();
        getPlayers();
        teamParser.initPlayerGraph();

    }

    /**
     * This function loads the players from each team and creates an instance of PlayerEntry for
     * each player.
     *
     * @param - N/A
     * @return - N/A
     */
    public void getPlayers() {
        for (String teamName : teamParser.getStatPageMap().keySet()) {
            teamParser.getPlayers(teamName);
        }
        for (String player : teamParser.getPlayers()) {
            totalPts.add(new PlayerEntry(player, 0.0));
        }
    }

    /**
     * This function records the stats for the top 5 players from each team: assists (AST), PPG
     * (points per game), free throws (FT), total rebounds (TRB), and FG (field goals). It then
     * calls a helper function, givePoints() to allocate points to the top 5 players. From there
     * it compares all players that were tracked and allocates points based on standings.
     *
     * @param - N/A
     * @return - N/A
     */
    public void trackStats() {
        for (String teamName : teamParser.getStatPageMap().keySet()) {
            teamParser.getTeamStatPage(teamName);
            PriorityQueue<PlayerEntry> tempAST = teamParser.statPointAllocator("AST", teamName);
            givePoints(tempAST);
            PriorityQueue<PlayerEntry> tempPPG = teamParser.statPointAllocator("PPG", teamName);
            givePoints(tempPPG);
            PriorityQueue<PlayerEntry> tempFT = teamParser.statPointAllocator("FT", teamName);
            givePoints(tempFT);
            PriorityQueue<PlayerEntry> tempTRB = teamParser.statPointAllocator("TRB", teamName);
            givePoints(tempTRB);
            PriorityQueue<PlayerEntry> tempFG = teamParser.statPointAllocator("FG", teamName);
            givePoints(tempFG);
        }
        //organize list in descending order of points
        Collections.sort(totalPts);
        Collections.reverse(totalPts);
        //Top 5 get 50 points
        for (int i = 0; i < 5; i++) {
            addPointsAllPlayers(50.0, i);
        }

        //Next 10 get 47 points
        for (int i = 5; i < 15; i++) {
            addPointsAllPlayers(47.0, i);
        }

        //Next 10 get 45 points
        for (int i = 15; i < 25; i++) {
            addPointsAllPlayers(45.0, i);
        }

        //Next 10 players get 40 points
        for (int i = 25; i < 35; i++) {
            addPointsAllPlayers(40.0, i);
        }

        //Next 15 players get 35 points
        for (int i = 35; i < 50; i++) {
            addPointsAllPlayers(35.0, i);
        }

        //Rest of the players get 20 points
        for (int i = 50; i < totalPts.size(); i++) {
            addPointsAllPlayers(20.0, i);
        }
        //organize list in descending order of points again after points are added
        Collections.sort(totalPts);
        Collections.reverse(totalPts);
    }

    /**
     * Helper method to allocate pts based on a player's # of wins in playerGraph.
     *
     * @param - double points, int i - number of points to be added and the current index in for loop
     * @return - N/A
     */
    public void addPointsAllPlayers(double points, int i) {
        if (i < teamParser.getPlayerGraph().size()) {
            PlayerEntry curr = teamParser.getPlayerGraph().get(i);
                for (PlayerEntry player : totalPts) {
                if (player.equals(curr)) {
                    player.setValue((double) player.getValue() + points);
                }
            }
        }
    }

    /**
     * Adds points to the top 5 of each team for each specific stat based on how they compare
     * to their teammates. The lowest player gets 2 points, and the points given incr by 2, stopping
     * at 10.
     *
     * @param - PriorityQueue<PlayerEntry> topFive - PQ of the five players from the curr team
     * @return - N/A
     */
    public void givePoints(PriorityQueue<PlayerEntry> topFive) {
        for (int i = 2; i <= 10; i += 2) {
            if (topFive.size() > 0) {
                PlayerEntry curr = topFive.poll();
                int index = Integer.MAX_VALUE;
                //due to referential equality must search for each player in list
                for (PlayerEntry player : totalPts) {
                    if (player.getKey().equals(curr.getKey())) {
                        index = totalPts.indexOf(player);
                    }
                }
                //adds the appropriate amount of points to a player's current score
                totalPts.get(index).setValue(totalPts.get(index).value + i);
            }
        }
    }

    /**
     * Returns the top X number of players as a list, based on num of pts
     *
     * @param - int num - the number of players to be returned
     * @return - List<PlayerEntry>
     */
    public List<PlayerEntry> topXPlayers(int num) {
        List<PlayerEntry> topPlayers = new ArrayList<PlayerEntry>();
        for (int i = 0; i < num; i++) {
            topPlayers.add(totalPts.get(i));
        }
        return topPlayers;
    }

    /**
     * Gets an encapsulated totalPts ArrayList
     *
     * @param - N/A
     * @return - List<PlayerEntry>
     */
    public List<PlayerEntry> getTotalPts() {
        List<PlayerEntry> copy = new ArrayList<>();
        for (PlayerEntry player : totalPts) {
            copy.add(new PlayerEntry(player.getKey(), player.getValue()));
        }
        return copy;
    }

}
