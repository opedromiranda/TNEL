package Components;


public class Game {

    private int ID;
    private String house;
    private String visitant;
    private int day;


    public Game(String house, String visitant, String day, String ID) {
        this.house = house;
        this.visitant = visitant;
        this.day = Integer.parseInt(day);
        this.ID = Integer.parseInt(ID);
    }

    public Game() {
    }

    public String getHouse() {
        return house;
    }

    public String getVisitant() {
        return visitant;
    }

    public int getDay() {
        return day;
    }

    public int getID() {
        return ID;
    }


}
