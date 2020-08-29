import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parse {
    private String baseURL;
    private Document currentDoc;
    private Map<String, String> statPageMap;
    private Map<String, String> gamePageMap;
    private List<String> players = new ArrayList<>();
    private List<PlayerEntry> playerGraph = new ArrayList<>();

    /**
     * Parse Constructor using JSoup
     */
    public Parse() {
        this.baseURL = "https://bleacherreport.com/articles/2216547-each-nba-franchises-best-team" +
                "-ever#slide1";
        try {
            this.currentDoc = Jsoup.connect(this.baseURL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function parses through Bleacher Report Website and returns a map of all of the team
     * names with their Stat Pages
     */
    public List<String> getTeamsStatsPages() {
        List<String> teamNames = new ArrayList<>();
        this.statPageMap = new HashMap<String, String>();
        Elements pageElements = this.currentDoc.select("h1");
        Pattern pattern = Pattern.compile("([\\d]*)-[\\d]{2}([\\w|\\s]*) ([\\w|\\s]*)");
        for (Element page : pageElements) {
            teamNames.add(page.text());
        }
        for (String team : teamNames) {
            Matcher matcher = pattern.matcher(team);
            if (matcher.find()) {
                String city = getTeamCity(matcher.group(3));
                int year = Integer.parseInt(matcher.group(1));
                year++;
                if(city.equals("TOR")){
                    year = 2018;
                }
                if(city.equals("GSW")){
                    year = 2016;
                }
                if(city.equals("LAL")){
                    year = 2001;
                }
                String yearForUrl = "" + year;
                if (!city.equals("HOR")) {
                    this.statPageMap.put(matcher.group(3),
                            "https://www.basketball-reference.com/teams/" + city + "/" + yearForUrl
                                    + ".html");
                }
            } else {
                continue;
            }
        }
        return teamNames;
    }

    /**
     * This function parses through Bleacher Report Website and returns a map of all of the team
     * Names with their URL
     */
    public List<String> getTeamsGamesPages() {
        List<String> teamNames = new ArrayList<>();
        this.gamePageMap = new HashMap<String, String>();
        Elements pageElements = this.currentDoc.select("h1");
        Pattern pattern = Pattern.compile("([\\d]*)-[\\d]{2}([\\w|\\s]*) ([\\w|\\s]*)");
        for (Element page : pageElements) {
            teamNames.add(page.text());
        }
        for (String team : teamNames) {
            Matcher matcher = pattern.matcher(team);
            if (matcher.find()) {
                String city = getTeamCity(matcher.group(3));
                int year = Integer.parseInt(matcher.group(1));
                year++;
                String yearForUrl = "" + year;
                if (!city.equals("HOR")) {
                    this.gamePageMap.put(matcher.group(3),
                            "https://www.basketball-reference.com/teams/" + city + "/" + yearForUrl
                                    + "_games.html");
                }
            } else {
                continue;
            }
        }
        return teamNames;
    }

    /**
     * This function acts as a helper function for getTeamsStatsPages. It goes through the each of the teams and gets
     * the city abbreviation as written in a txt file using Scanners
     *
     * @param teamName - string of the name of the team EX: Mavericks
     * @return - returns a string with the city abbreviation EX: DAL (Dallas)
     */
    public String getTeamCity(String teamName) {
        File file = new File("NBA_TEAM_CITIES.txt");
        Scanner cityScanner = null;
        try {
            cityScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String city = "No Such City";
        while (cityScanner.hasNext()) {
            String teamData = cityScanner.nextLine();
            if (teamData.contains(teamName)) {
                String[] teamNameAndCity = teamData.split(":");
                city = teamNameAndCity[1].trim();
                return city;
            }
        }
        return city;
    }

    /**
     * This function switches the parser to the team page that is relevant as well as prints out the relevant URL
     *
     * @param teamName - the name of the team that you want to switch to
     */
    public void getTeamStatPage(String teamName) {
        String relevantURL = this.statPageMap.get(teamName);
        try {
            this.currentDoc = Jsoup.connect(relevantURL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function switches the parser to the team page that is relevant as well as prints out the relevant URL
     *
     * @param teamName - the name of the team that you want to switch to
     */
    public void getTeamGamePage(String teamName) {
        String relevantURL = this.gamePageMap.get(teamName);
        try {
            this.currentDoc = Jsoup.connect(relevantURL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This function will initialize the Parser's list of players by taking the data and using REGEX to match for names
     * in the HTML. We loop 5 times, to get the top 5 players on a "statistics per-game" basis.
     * This will print out an array of all top players which will be followed their team to distinguish repeat players
     * EX: "Chamberlain, Wilt" 76ers, "Chamberlain, Wilt" Lakers.
     */
    public void getPlayers(String teamName) {
        getTeamStatPage(teamName);
        Pattern pattern = Pattern.compile("data-stat=\\\"player\\\" csk=(\\\"[\\w|\\s|,]*\\\")");
        Element perGame = this.currentDoc.getElementById("all_per_game");
        Matcher matcher = pattern.matcher(perGame.data());
        for (int i = 0; i < 5; i++) {
            if (matcher.find()) {
                players.add(matcher.group(1) + ": " + teamName);
            }
        }
    }

    /**
     * This function maps each of the players to the number of other players in the list that they beat.
     */
    public void initPlayerGraph() {
        List<String> teamsCovered = new ArrayList<>();
        //iterates through all the players
        for (String player : this.players) {
            String teamName = player.split(": ")[1].trim();
            if (!teamsCovered.contains(teamName)) {
                teamsCovered.add(teamName);
                //creates a list of all of the team opponents
                ArrayList<String> teamOpponents = new ArrayList<>();
                ArrayList<String> OpposingTeams = new ArrayList<>();
                //switches the doc to the game page of the team
                getTeamGamePage(player.split(": ")[1].trim());
                //gets all elements with opponent teams as well as the result of the game
                Elements opponents = this.currentDoc.select("[data-stat='opp_name']");
                Elements gameResults = this.currentDoc.select("[data-stat='game_result']");
                //iterate through all of the contests of the season
                for (int i = 0; i < opponents.size(); i++) {
                    if (!OpposingTeams.contains(opponents.get(i).text())) {
                        OpposingTeams.add(opponents.get(i).text());
                        //print out the name of the opponent as well as the result
                        Elements links = opponents.get(i).getElementsByTag("a");
                        //if the result was a win then
                        if (gameResults.get(i).text().equals("W")) {
                            for (Element element : links) {
                                if (element.hasAttr("href")) {
                                    String opponentPage = (element.attr("href"));
                                    //change the doc to the page of the opponent
                                    try {
                                        this.currentDoc = Jsoup.connect("https://www.basketball-reference.com" + opponentPage).get();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //match for names of the best players per game for the opponents
                                    Pattern pattern = Pattern.compile("data-stat=\\\"player\\\" csk=(\\\"[\\w|\\s|,]*\\\")");
                                    Element perGame = this.currentDoc.getElementById("all_per_game");
                                    Matcher matcher = pattern.matcher(perGame.data());
                                    for (int j = 0; j < 5; j++) {
                                        if (matcher.find()) {
                                            String[] teamNames = opponents.get(i).text().split(" ");
                                            String fullOpponent = (matcher.group(1) + ": " + teamNames[teamNames.length - 1]);
                                            if (this.players.contains(fullOpponent)) {
                                                teamOpponents.add(fullOpponent);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        continue;
                    }
                }
                for (int i = 0; i < this.players.size() - 1; i++) {
                    if (this.players.get(i).split(": ")[1].equals(teamName)) {
                        //this.playerGraph.put(this.players.get(i), teamOpponents.size());
                        this.playerGraph.add(new PlayerEntry(this.players.get(i), (double) teamOpponents.size()));
                    }
                }
            }
        }
        Collections.sort(playerGraph);
        Collections.reverse(playerGraph);
    }


    public PriorityQueue<PlayerEntry> statPointAllocator(String statistic, String teamName) {
        PriorityQueue<PlayerEntry> stats = new PriorityQueue<>();
        if (statistic.equals("AST")) {
            statistic = "ast_per_g";
        } else if (statistic.equals("PPG")) {
            statistic = "pts_per_g";
        } else if (statistic.equals("FT")) {
            statistic = "ft_pct";
        } else if (statistic.equals("TRB")) {
            statistic = "trb_per_g";
        } else if (statistic.equals("FG")) {
            statistic = "fg_pct";
        } else if (statistic.equals("STL")) {
            statistic = "stl";
        } else if (statistic.equals("BLK")){
            statistic = "blk";
        }
        //only want to check stats for curr team, iterates through players list to find team
        int startIndex = Integer.MAX_VALUE;
        getTeamStatPage(teamName);
        for (String player : players) {
            if (player.contains(teamName)) {
                startIndex = players.indexOf(player);
                break;
            }
        }
        Pattern pattern = Pattern.compile("data-stat=\\\"" + statistic + "\\\" >([\\d|.]*)<");
        Element perGameStat = this.currentDoc.getElementById("all_per_game");
        Matcher matcher = pattern.matcher(perGameStat.data());
        //ensures index is in bounds then checks the specific stat for all 5 players on team
        if (startIndex < players.size() - 5) {
            for (int i = 0; i < 5; i++) {
                if (matcher.find()) {
                    Double playerStat = Double.parseDouble(matcher.group(1));
                    stats.add(new PlayerEntry(this.players.get(startIndex + i), playerStat));
                }
            }
        }
        return stats;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public Document getCurrentDoc() {
        return currentDoc;
    }

    public Map<String, String> getStatPageMap() {
        Map<String, String> copy = new HashMap<String, String>();
        for (String key : statPageMap.keySet()) {
            copy.put(key, statPageMap.get(key));
        }
        return copy;
    }

    public Map<String, String> getGamePageMap() {
        Map<String, String> copy = new HashMap<String, String>();
        for (String key : gamePageMap.keySet()) {
            copy.put(key, gamePageMap.get(key));
        }
        return copy;
    }

    public List<String> getPlayers() {
        List<String> copy = new ArrayList<String>();
        for (String player : players) {
            copy.add(player);
        }
        return copy;
    }

    public List<PlayerEntry> getPlayerGraph() {
        List<PlayerEntry> copy = new ArrayList<PlayerEntry>();
        for (PlayerEntry player : playerGraph) {
            copy.add(new PlayerEntry(player.getKey(), player.getValue()));
        }
        return copy;
    }
}