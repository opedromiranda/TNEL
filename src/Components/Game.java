package Components;


public class Game {

    private int id;
    private String home;
    private String away;

    public Game(String home, String away, int id) {
        this.home = home;
        this.away = away;
        this.id = id;
    }

    public Game() {
    }

    public int getId() {
        return id;
    }


}
