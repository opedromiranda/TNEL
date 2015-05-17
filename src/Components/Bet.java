package Components;


public class Bet {

    private Game game;
    private double houseWinnerOdd;
    private double drawOdd;
    private double visitantWinnerOdd;
    private int belief; // 0% - 100% of belief in his Odds
    private int ID;

    public Bet(Game game, double houseWinnerOdd, double drawOdd, double visitantWinnerOdd, int belief, int ID) {
        this.game = game;
        this.houseWinnerOdd = houseWinnerOdd;
        this.drawOdd = drawOdd;
        this.visitantWinnerOdd = visitantWinnerOdd;
        this.belief = belief;
        this.ID = ID;
    }

    public double getHouseWinnerOdd() {
        return houseWinnerOdd;
    }

    public double getDrawOdd() {
        return drawOdd;
    }

    public double getVisitantWinnerOdd() {
        return visitantWinnerOdd;
    }

    public int getBelief() {
        return belief;
    }

    public int getID() {
        return ID;
    }

}
