package App;


import Agents.Player;
import Components.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {

    public static ArrayList<Player> players = new ArrayList<Player>();
    public static Calendar calendar = new Calendar();
    public static ArrayList<Result> results = new ArrayList<Result>();

    public static ArrayList<Player> loadPlayers() {
        try {
            String playersData = readFile("players.json");

            JSONArray players = new JSONArray(playersData);

            for (int i = 0; i < players.length(); i++) {
                JSONObject playerData = (JSONObject) players.get(i);
                Player p = instantiatePlayer(playerData);

                ArrayList<Belief> playerBeliefs = instantiateBeliefs(playerData.getJSONArray("beliefs"));
                p.setBeliefs(playerBeliefs);

                ArrayList<Auction> playerAuctions = instantiateAuction(playerData.getJSONArray("auctions"));
                p.setAuctions(playerAuctions);

                Utils.players.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Utils.players;
    }

    public static Calendar loadCalendar() {
        try {
            String calendarData = readFile("calendar.json");

            JSONArray calendarDays = new JSONArray(calendarData);

            for (int i = 0; i < calendarDays.length(); i++) {
                JSONObject calendarEntry = (JSONObject) calendarDays.get(i);
                String day = calendarEntry.getString("name");
                JSONArray gamesData = (JSONArray) calendarEntry.get("games");
                ArrayList games = instantiateGames(gamesData);
                calendar.setGames(games, day);
                calendar.days.add(games);
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

    private static ArrayList<Belief> instantiateBeliefs(JSONArray beliefs) throws JSONException {
        ArrayList<Belief> result = new ArrayList<Belief>();
        for (int i = 0; i < beliefs.length(); i++) {
            JSONObject beliefData = beliefs.getJSONObject(i);
            result.add(new Belief(beliefData));
        }
        return result;
    }

    private static ArrayList<Auction> instantiateAuction(JSONArray auctions) throws JSONException {
        ArrayList<Auction> result = new ArrayList<Auction>();
        for (int i = 0; i < auctions.length(); i++) {
            JSONObject auctionData = auctions.getJSONObject(i);
            result.add(new Auction(auctionData));
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

    public static void saveResults() throws JSONException {

            JSONArray res = new JSONArray();

            for (Result r : results) {
                JSONObject main = new JSONObject();
                main.put("Home Team", r.getGame().getHome());
                main.put("Away Team", r.getGame().getAway());
                main.put("Result", r.getGame().getResult());
                main.put("Auction id", r.getAuction().getAuctionId());
                main.put("Auction seller id", r.getAuction().getownerId());
                main.put("Auction initial odd", r.getAuction().getOdd());
                main.put("Auction initial odd", r.getAuction().getOdd());
                main.put("Auction bet value", r.getAuction().getBetValue());
                main.put("Auction number final round", r.getAuction().getRound());
                main.put("Player Winner", r.getWinner().getPlayerName());
                main.put("Player Winner id", r.getWinner().getId());
                main.put("Player Winner strategy", r.getWinner().getStrategy());
                res.put(main);
            }

            try {
                FileWriter fstream = new FileWriter("results.json");
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(res.toString(5));
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }
}
