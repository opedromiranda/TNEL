package Components;


public class Game {

    private int ID;
    private String home;
    private String away;

    public Game(String home, String away, int ID) {
        this.home = home;
        this.away = away;
        this.ID = ID;
    }

    public Game() {
    }

    public int getID() {
        return ID;
    }


}
