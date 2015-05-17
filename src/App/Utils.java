package App;


import Agents.Player;
import Components.Bet;
import Components.Calendar;
import Components.Game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {

    public static Calendar calendar;
    public static ArrayList<Bet> bets = new ArrayList<Bet>();
    public static ArrayList<Player> players = new ArrayList<Player>();

    public static void getElements() {
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader("calendar"));
            ArrayList<Game> games = new ArrayList<Game>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                games.add(new Game(parts[0], parts[1], parts[2], parts[3]));
            }

            calendar = new Calendar(games);

            reader = new BufferedReader(new FileReader("bets"));

            while((line = reader.readLine()) != null){
                String[] parts = line.split(" ");

                Game g = searchGame(parts[1]);
                int id = Integer.parseInt(parts[0]);
                double winOdd = Double.parseDouble(parts[2]);
                double drawOdd = Double.parseDouble(parts[3]);
                double looseOdd = Double.parseDouble(parts[4]);
                int belief = Integer.parseInt(parts[5]);

                bets.add(new Bet(g, winOdd, drawOdd, looseOdd, belief, id));
            }

            reader = new BufferedReader(new FileReader("players"));

            while((line = reader.readLine()) != null){
                String[] parts = line.split(" ");

                String name = parts[0];
                String type = parts[1];
                double balance = Double.parseDouble(parts[2]);
                ArrayList<Bet> playerBets = betsSearch(parts[3].toCharArray());
                players.add(new Player(name, type, balance, playerBets));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Bet> betsSearch(char[] betArray) {
        ArrayList<Bet> res = new ArrayList<Bet>();

        for(int i = 0; i < betArray.length; i++){
            for(Bet b : bets){
                if(b.getID() == Character.getNumericValue(betArray[i])){
                    res.add(b);
                }
            }
        }
        return res;
    }

    public static Game searchGame(String id) {

        for(Game g : calendar.getGamesCalendar()){
            if(g.getID() == Integer.parseInt(id)){
                return g;
            }
        }

        return new Game();
    }
}
