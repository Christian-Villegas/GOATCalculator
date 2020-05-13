import org.jsoup.nodes.Element;

import java.util.*;

//main function to call functions in Statistics and find top X players
public class main{

    public static void main(String[] args) {
        Statistics stats = new Statistics();
        stats.trackStats();
        List<PlayerEntry> topFive = stats.topXPlayers(15);
        for (PlayerEntry player : topFive) {
            System.out.println(player);
        }
    }
}