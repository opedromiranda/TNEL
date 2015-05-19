package App;

import Agents.Player;
import Components.Belief;
import Components.Calendar;
import Components.Game;
import jade.Boot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    static Calendar calendar;
    static ArrayList<Player> players;

    public static void main(String[] args){

        String[] param = new String[2];
        param[0] = "-gui";
        param[1] = "Auctioneer:Agents.Loader";
        Boot.main(param);

        players = Utils.loadPlayers();
        calendar = Utils.loadCalendar();

        try {
            // iterate through every day of the calendar and start the auctions for each day
            play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void play() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        for (ArrayList<Game> games : calendar.days) {

            for (Game game : games) {
                Integer gameId = game.getId();
                System.out.println("Beliefs for game " + gameId + " : ");

                for (Player player : players) {
                    Belief belief = player.getBeliefForGame(gameId);
                    System.out.println(player.getPlayerName() + " : " + belief.getBeliefDegree());
                }
            }

            // press enter to go to next day
            br.readLine();
        }
    }
}
