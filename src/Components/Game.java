package Components;


public class Game {

    private int id;
    private String home;
    private String away;
    private String result;

    public Game(String home, String away, int id) {
        this.home = home;
        this.away = away;
        this.id = id;
        this.result = null;
    }

    public Game() {
    }

    public int getId() {
        return id;
    }

    public String getResult(){return this.result;}

    public void setResult(String result){this.result = result;}


}
