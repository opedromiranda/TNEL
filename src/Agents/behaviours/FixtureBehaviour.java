package Agents.behaviours;

import Agents.Player;
import App.Utils;
import Components.Auction;
import Components.Game;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Random;

public class FixtureBehaviour extends SimpleBehaviour {

    ArrayList<Game> games;
    int auctionsFinished = 0;
    int numberOfAuctions = 0;
    boolean calledOnce = false;
    int askedForSellers = 0;
    ArrayList<Auction> auctionsCompleted = new ArrayList<Auction>();


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
        return auctionsFinished >= numberOfAuctions && askedForSellers == games.size();
    }

    @Override
    public int onEnd() {
        finishGames();
        return super.onEnd();
    }

    private SellerSearch lookForSellers(Game g) {
        SellerSearch s = new SellerSearch(getAgent(), new ACLMessage(ACLMessage.REQUEST), this, g);
        getAgent().addBehaviour(s);
        return s;
    }

    public void incrementAuctionsFinished(Auction auction) {
        auctionsCompleted.add(auction);
        auctionsFinished++;
    }

    public void onEndSellerSearch(ArrayList<ACLMessage> sellers) {
        askedForSellers++;
        numberOfAuctions += sellers.size();
        for (ACLMessage seller : sellers) {
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
        System.out.println("-------------------------Auction started of game----------------------- : " + game.getId());
        getAgent().addBehaviour(auctionBehavior);
    }

    private void finishGames() {

        for (Game g : games) {
            Random rand = new Random();
            int result = rand.nextInt(3) + 1;
            switch (result){
                case 1:
                    g.setResult("homeWin");
                    break;
                case 2:
                    g.setResult("draw");
                    break;
                case 3:
                    g.setResult("awayWin");
                    break;
                default:
                    break;
            }
            for (Auction a : auctionsCompleted) {
                if (a.getGameId() == g.getId()) {
                    if (g.getResult().compareTo(a.getType()) == 0) {
                        Player winner = findPlayer(a.getownerId());
                        winner.winnerAsSeller(a);
                    }
                    else {
                        Player winner = findPlayer(a.getBuyerId());
                        winner.winnerAsBuyer(a);
                    }
                }
            }
        }
    }

    private Player findPlayer(String name) {
        for (Player p : Utils.players) {
            if (p.getLocalName().compareTo(name) == 0) {
                return p;
            }
        }
        return null;
    }
}
