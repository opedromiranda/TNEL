package Agents.behaviours;

import Agents.Player;
import App.Utils;
import Components.Game;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class FixtureBehaviour extends SimpleBehaviour {

    ArrayList<Game> games;
    int auctionsFinished = 0;
    int numberOfAuctions = 0;
    boolean calledOnce = false;


    public FixtureBehaviour(Agent a, ArrayList<Game> games) {
        super(a);
        this.games = games;
    }

    @Override
    public void action() {
        if (!calledOnce) {
            for (Game g : games) {
                lookForSellers(g);
            }
            calledOnce = true;
        }
    }

    @Override
    public boolean done() {
        return auctionsFinished >= numberOfAuctions;
    }

    @Override
    public int onEnd() {
        System.out.println("Day finished");
        return super.onEnd();
    }

    private SellerSearch lookForSellers(Game g) {
        SellerSearch s = new SellerSearch(getAgent(), new ACLMessage(ACLMessage.REQUEST), this, g);
        getAgent().addBehaviour(s);
        return s;
    }

    public void incrementAuctionsFinished() {
        auctionsFinished++;
    }

    public void onEndSellerSearch(ArrayList<ACLMessage> sellers) {
        numberOfAuctions += sellers.size();
        for(ACLMessage seller : sellers) {
            startAuction(seller);
        }
    }

    private void startAuction(ACLMessage message) {
        int gameId = Integer.parseInt(message.getContent());
        Game game = null;
        Player player = null;
        AID playerAID =  message.getSender();

        for (Game g : games) {
            if(g.getId() == gameId) {
                game = g;
                break;
            }
        }

        for (Player p : Utils.players) {
            if (p.getAID().getName().compareTo(playerAID.getName()) == 0) {
                player = p;
                break;
            }
        }

        AuctionBehaviour auctionBehavior = new AuctionBehaviour(getAgent(), new ACLMessage(ACLMessage.CFP), this, game, player, new ArrayList<AID>());
        System.out.println("Auction started of game : " + game.getId());
        getAgent().addBehaviour(auctionBehavior);
    }
}
