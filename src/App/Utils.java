package App;


import Agents.Player;
import Components.Bet;
import Components.Calendar;
import Components.Game;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Utils {

    public static Calendar calendar;
    public static ArrayList<Bet> bets = new ArrayList<Bet>();
    public static ArrayList<Player> players = new ArrayList<Player>();

    public static void getElements() {
        /*
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader("calendar"));
            ArrayList<Game> games = new ArrayList<Game>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                //games.add(new Game(parts[0], parts[1], parts[2], parts[3]));
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
        }*/
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

    public static ArrayList<Player> loadPlayers() {
        ArrayList<Player> result = new ArrayList<Player>();

        try {
            String playersData = readFile("players.json");

            JSONArray players = new JSONArray(playersData);

            for (int i = 0; i < players.length(); i++) {
                JSONObject playerData = (JSONObject) players.get(i);

                Player p = instantiatePlayer(playerData);
                result.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Calendar loadCalendar() {
        Calendar calendar = new Calendar();
        try {
            String calendarData = readFile("calendar.json");

            JSONArray calendarDays = new JSONArray(calendarData);

            for (int i = 0; i < calendarDays.length(); i++) {
                JSONObject calendarEntry = (JSONObject) calendarDays.get(i);
                String day = calendarEntry.getString("name");
                JSONArray gamesData = (JSONArray) calendarEntry.get("games");
                ArrayList games = instantiateGames(gamesData);
                calendar.setGames(games, day);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return calendar;
    }

    private static Player instantiatePlayer(JSONObject playerData) throws JSONException {
        Integer id = (Integer) playerData.get("id");
        Double balance = (Double) playerData.get("balance");
        String name = playerData.getString("name");

        return new Player(id, name, balance);
    }

    private static ArrayList<Game> instantiateGames(JSONArray games) throws JSONException {
        ArrayList<Game> result = new ArrayList<Game>();
        for (int i = 0; i < games.length(); i++) {
            JSONObject gameEntry = (JSONObject) games.get(i);
            String homeTeam = gameEntry.getString("home_team");
            String awayTeam = gameEntry.getString("away_team");
            Integer id = (Integer) gameEntry.get("id");
            Game g = new Game(homeTeam, awayTeam, id);
            result.add(g);
        }
        return result;
    }

    private static String readFile(String name) throws IOException {
        FileInputStream stream = new FileInputStream(name);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size());
			/* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }

}
