package Components;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Calendar {

    HashMap<String, ArrayList<Game>> gamesCalendar;

    public ArrayList<Game> getGamesCalendar(String day) {
        return this.gamesCalendar.get(day);
    }

    public void setGames(ArrayList<Game> games, String day) {
        this.gamesCalendar.put(day, games);
    }
}
