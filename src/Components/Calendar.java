package Components;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Calendar {

    HashMap<String, ArrayList<Game>> gamesCalendar = new HashMap<String, ArrayList<Game>>();
    public ArrayList<ArrayList<Game>> days = new ArrayList<ArrayList<Game>>();

    public ArrayList<Game> getGamesCalendar(String day) {
        return this.gamesCalendar.get(day);
    }

    public void setGames(ArrayList<Game> games, String day) {
        this.gamesCalendar.put(day, games);
    }
}
