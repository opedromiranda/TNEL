package App;

import Agents.Player;
import Components.Auction;
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

        players = Utils.loadPlayers();
        calendar = Utils.loadCalendar();

        String[] param = new String[2];
        param[0] = "-gui";
        param[1] = "Loader:Agents.Loader";
        Boot.main(param);
    }

    private void play() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        for (ArrayList<Game> games : Utils.calendar.days) {

            for (Game game : games) {
                Integer gameId = game.getId();
                System.out.println("Beliefs/Auctions for game " + gameId + " : ");

                for (Player player : Utils.players) {
                    Belief belief = player.getBeliefForGame(gameId);
                    Auction auction = player.getAuctionForGame(gameId);

                    if(auction == null){
                        System.out.println(player.getPlayerName() + ": \nbelief: " + belief.getBeliefDegree());
                    }
                    else{
                        System.out.println(player.getPlayerName() + ": \nbelief: " + belief.getBeliefDegree() +
                                "\nauction: " + auction.getType() + " with value " + auction.getBetValue());
                    }

                }

            }
            // press enter to go to next day
            br.readLine();
        }
    }
}
