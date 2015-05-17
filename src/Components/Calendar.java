package Components;


import java.util.ArrayList;

public class Calendar {

    ArrayList<Game> gamesCalendar;

    public Calendar(ArrayList<Game> games) {
        this.gamesCalendar = games;
    }

    public ArrayList<Game> getGamesCalendar() {
        return gamesCalendar;
    }

    public void addGame (Game game) {
        this.gamesCalendar.add(game);
    }
}
