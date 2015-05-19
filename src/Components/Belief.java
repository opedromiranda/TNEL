package Components;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pedro on 19/05/15.
 */
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

    public double getBeliefDegree() {
        return beliefDegree;
    }
}
