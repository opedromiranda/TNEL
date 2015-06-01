package Agents;

import Agents.behaviours.AuctionPlayerBehavior;
import Agents.behaviours.SellerResponse;
import Components.Auction;
import Components.Belief;
import Components.Bet;
import Components.Game;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Player extends Agent {

    public static String TYPE = "PLAYER";

    private String name;
    private double balance; // actual money
    private Integer id;

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

}
