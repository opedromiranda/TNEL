package Components;

import jade.core.AID;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Auction implements Serializable {

    Integer auctionId,
            gameId,
            round;
    String  ownerId,
            buyerId;
    String  type;
    double betValue,
           startOdd,
           actualOdd;


    public Auction(JSONObject auctionData) throws JSONException {
        gameId = auctionData.getInt("game_id");
        auctionId = auctionData.getInt("id");
        type = auctionData.getString("type");
        betValue = auctionData.getDouble("bet_value");
        startOdd = auctionData.getDouble("odd");
        actualOdd = startOdd;
        buyerId = null;
        ownerId = null;
        this.round = 0;
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
        return startOdd;
    }

    public Integer getAuctionId() {
        return auctionId;
    }

    public void incrementActualOdd() {
        actualOdd += 0.01;
        resetRound();
    }

    public double getActualOdd() {
        return actualOdd;
    }

    public void nextRound(){
        this.round++;
    }

    public int getRound(){
        return round;
    }

    public void resetRound (){
        this.round = 0;
    }

    public void setBuyerId(String id) {
        this.buyerId = id;
    }

    public String getBuyerId(){return buyerId;}

    public String getownerId(){return ownerId;}

    public void setOwnerId(String id){this.ownerId = id;}
}
