package Components;

import App.Main;
import jade.core.AID;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Auction implements Serializable {

    Integer auctionId,
            gameId,
            repeatRound,
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
        this.repeatRound = 0;
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
        resetRepeatRound();
    }

    public void incrementRound() {
        this.round++;
    }

    public int getRound(){
        return round;
    }

    public double getActualOdd() {
        return actualOdd;
    }

    public void nextRepeatRound(){
        this.repeatRound++;
    }

    public int getRepeatRound(){
        return repeatRound;
    }

    public void resetRepeatRound (){
        this.repeatRound = 0;
    }

    public void setBuyerId(String id) {
        this.buyerId = id;
    }

    public String getBuyerId(){
        if(buyerId == null){
            return "Nobody bought";
        }
        return buyerId;
    }

    public String getownerId(){
        return ownerId;
    }

    public void setOwnerId(String id){this.ownerId = id;}

    public void generateResult() {
        System.out.println("type: " + type);
        if (type.equals("homeWin") || type.equals("awayWin")) {
            double probability = 1 / actualOdd;
            boolean won = Math.random() > probability;

            if (won) {
                System.out.println("backer won");
            } else {
                System.out.println("backer lost");
            }
        }
    }
}
