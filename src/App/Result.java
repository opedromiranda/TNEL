package App;

import Agents.Player;
import Components.Auction;
import Components.Game;

public class Result {
    Game game;
    Auction auction;
    Player winner;

    public Result(Game game, Auction auction, Player winner){
        this.game = game;
        this.auction = auction;
        this.winner = winner;
    }

    public Game getGame() {
        return game;
    }

    public Auction getAuction() {
        return auction;
    }

    public Player getWinner() {
        return winner;
    }
}
