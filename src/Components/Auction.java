package Components;

import org.json.JSONException;
import org.json.JSONObject;

public class Auction {

    Integer auctionId,
            gameId;
    String  type;
    double betValue,
           odd;

    public Auction(JSONObject auctionData) throws JSONException {
        gameId = auctionData.getInt("game_id");
        type = auctionData.getString("type");
        betValue = auctionData.getDouble("bet_value");
        odd = auctionData.getDouble("odd");
    }

    public Integer getGameId() {
        return gameId;
    }

    public String getType() {
        return type;
    }

    public double getBetValue() {
        return betValue;
    }

    public double getOdd() {
        return odd;
    }
}
