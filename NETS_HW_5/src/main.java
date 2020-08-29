import org.jsoup.nodes.Element;

import java.util.*;

//main function to call functions in Statistics and find top X players
public class main{

    public static void main(String[] args) {
        Scanner keyboardInput = new Scanner(System.in);
        System.out.print("Enter categories to be considered (Categories include AST, PPG, FT, TRB, FG, STL, and BLK): ");
        String input = keyboardInput.nextLine();
        String[] categories = input.split(", ");
        System.out.println("You selected: ");
        for(String stat: categories){
            stat.trim();
            System.out.println(stat);
        }
        Statistics stats = new Statistics();
        stats.trackStats(categories);
        List<PlayerEntry> topFive = stats.topXPlayers(5);
        for (PlayerEntry player : topFive) {
            System.out.println(player);
        }
    }
}