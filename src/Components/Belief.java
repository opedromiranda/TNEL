package Components;

import org.json.JSONException;
import org.json.JSONObject;

public class Belief {

    Integer gameId,
            playerId;
    double homeWinOdd,
            awayWinOdd,
            drawOdd,
            beliefDegree;

    public Belief(JSONObject beliefData) throws JSONException {
        gameId = beliefData.getInt("game_id");
        playerId =  beliefData.getInt("player_id");
        homeWinOdd = beliefData.getDouble("home_win_odd");
        awayWinOdd = beliefData.getDouble("away_win_odd");
        drawOdd = beliefData.getDouble("draw_odd");
        beliefDegree = beliefData.getDouble("belief_degree");
    }

    public Integer getGameId() {
        return gameId;
    }

    public double getOdd(String market){

        if(market.equals("homeWin"))
            return homeWinOdd;
        else if (market.equals("awayWin"))
            return awayWinOdd;
        else if (market.equals("draw"))
            return drawOdd;

        return 0.0;
    }

    public double getBeliefDegree() {
        return beliefDegree;
    }
}
