package App;

import jade.Boot;

public class Main {

    public static void main(String[] args){

        String[] param = new String[2];
        param[0] = "-gui";
        param[1] = "Auctioneer:Agents.Loader";
        Boot.main(param);

        Utils.loadPlayers();
        Utils.loadCalendar();
    }
}
