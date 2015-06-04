package Agents;

import Agents.behaviours.AuctionPlayerBehavior;
import Agents.behaviours.SellerResponse;
import Components.Auction;
import Components.Belief;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class Player extends Agent {

    public static String TYPE = "PLAYER";

    private String name;
    private double balance; // actual money
    private Integer id;
    private HashMap<Integer, Double> responsability;
    private int strategy;
    boolean blocked;

    /**
     * HashMap that contains the player beliefs for each given match
     * They keys of the match are the id's of each match
     */
    private HashMap<Integer, Belief> beliefs = new HashMap<Integer, Belief>();

    /**
     * HashMap that contains the player auctions for each given match
     * They keys of the match are the id's of each match
     */
    public HashMap<Integer, Auction> auctions = new HashMap<Integer, Auction>();

    public Player(Integer id, String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.id = id;
        responsability = new HashMap<Integer, Double>();
        //strategy = 1;
        if(id == 1) {
            strategy = 1;
        }
        else {
            Random rand = new Random();
            int value = rand.nextInt(2);
            if(value == 0)
                strategy = 2;
            else
                strategy = 3;
        }
        blocked = false;
        System.out.println("Player :" + name + " with strategy " + strategy);
    }

    protected void setup()
    {
        System.out.println("Player agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(TYPE);
        sd.setName(getLocalName());
        register(sd);
        addBehaviour(new AuctionPlayerBehavior(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
        addBehaviour(new SellerResponse(this,  MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
    }

    void register(ServiceDescription sd)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
            doDelete();
        }
    }

    public double getBalance() {
        return balance;
    }

    public void setBeliefs(ArrayList<Belief> beliefs) {
        for(Belief b : beliefs) {
            Integer gameId = b.getGameId();
            this.beliefs.put(gameId, b);
        }
    }

    public void setAuctions(ArrayList<Auction> auctions) {
        for(Auction a : auctions) {
            Integer gameId = a.getGameId();
            this.auctions.put(gameId, a);
        }
    }

    public Belief getBeliefForGame(Integer gameId) {
        return beliefs.get(gameId);
    }

    public String getPlayerName() {
        return name;
    }

    public Auction getAuctionForGame(Integer gameId) {
        return auctions.get(gameId);
    }

    public void winnerAsBuyer(Auction auction){
        exitAuction(auction);
        balance += auction.getBetValue();
    }

    public void winnerAsSeller(Auction auction){
        balance += auction.getBetValue() * auction.getActualOdd();
    }


    public void exitAuction(Auction auction) {
        int auctionId = auction.getAuctionId();
        if (responsability.containsKey(auctionId)) {
            double value = responsability.get(auctionId);
            responsability.remove(auctionId);
            balance += value;
        }
    }

    public boolean decrementBalance(double value){
        if(balance >= value) {
            balance -= value;
            return true;
        }
        return false;
    }

    public boolean isAvailable(Auction auction){
        int auctionId = auction.getAuctionId();
        double actualOdd = auction.getActualOdd();
        double betValue = auction.getBetValue();

        double responsabilityValue = actualOdd * betValue;
        if (blocked) {
            System.out.println("blocked");
            return false;
        }
        if (responsability.containsKey(auctionId)) {
            if (balance > (responsabilityValue - responsability.get(auctionId))) {
                //blocked = true;
                return true;
            }
        }
        else {
            if (balance >= responsabilityValue) {
                //blocked = true;
                return true;
            }
        }

        return false;
    }

    public boolean makeAuction(Auction auction, Belief beliefForGame) {
        double actualOdd = auction.getActualOdd();
        double initialOdd = auction.getOdd();
        double responsabilityValue = actualOdd * auction.getBetValue();
        double playerOddBelief = beliefForGame.getOdd(auction.getType());
        double playerBeliefDegree = beliefForGame.getBeliefDegree();
        boolean res = false;

        switch (strategy) {
            case 1:
                if (applyExponentialStrategy(actualOdd, playerOddBelief, playerBeliefDegree, initialOdd)) {
                    refreshBalance(auction.getAuctionId(), responsabilityValue);
                    res = true;
                    blocked = false;
                }
                break;
            case 2:
                if (applyProbabilityStrategy(actualOdd, playerOddBelief, playerBeliefDegree)) {
                    refreshBalance(auction.getAuctionId(), responsabilityValue);
                    res = true;
                    blocked = false;
                }
                break;
            case 3:
                if (applyProbability_balanceStrategy(actualOdd, playerOddBelief, playerBeliefDegree, responsabilityValue)) {
                    refreshBalance(auction.getAuctionId(), responsabilityValue);
                    res = true;
                    blocked = false;
                }
                break;
            default:
                blocked = false;
                res = false;
                break;
        }
        return res;
    }

    private void refreshBalance(Integer auctionId, double responsabilityValue) {
        if (responsability.containsKey(auctionId)) {
            balance -= ( responsabilityValue - responsability.get(auctionId) );
        }
        else {
            balance -= responsabilityValue;
        }
        responsability.put(auctionId, responsabilityValue);
    }

    private boolean applyProbability_balanceStrategy(double actualOdd, double playerOddBelief, double playerBeliefDegree, double responsabilityValue) {
        Random r = new Random();
        int probability = r.nextInt(99) + 1;
        double riskRatio = responsabilityValue / balance;
        double newPlayerBeliefDegree = playerBeliefDegree - riskRatio * 100;

        if (actualOdd <= playerOddBelief)
            return probability <= newPlayerBeliefDegree;
        else
            return probability >= newPlayerBeliefDegree;
    }

    private boolean applyProbabilityStrategy(double actualOdd, double playerOddBelief, double playerBeliefDegree) {
        Random r = new Random();
        int probability = r.nextInt(99) + 1;

        if (actualOdd <= playerOddBelief)
            return probability <= playerBeliefDegree;
        else
            return probability >= playerBeliefDegree;
    }

    private boolean applyExponentialStrategy(double actualOdd, double playerOddBelief, double playerBeliefDegree, double initialOdd) {
        if(actualOdd <= playerOddBelief) {
            return true;
        }

        else {
            Random r = new Random();
            int probability = r.nextInt(99) + 1;
            double increments = (actualOdd - initialOdd);
            double playerExponentialBeliefDegree = Math.pow((playerBeliefDegree/100), increments) * 100;
            return probability >= playerExponentialBeliefDegree;
        }
    }

    public Integer getId() {
        return id;
    }

    public int getStrategy() {
        return strategy;
    }
}
