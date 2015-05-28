package Agents;

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
        addBehaviour(new FIPAContractNetResp(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
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

    class FIPAContractNetResp extends ContractNetResponder {

        public FIPAContractNetResp(Agent a, MessageTemplate mt) {
            super(a, mt);
        }


        protected ACLMessage handleCfp(ACLMessage cfp) {
            System.out.println("Message: " + cfp.getContent());
            ACLMessage reply = new ACLMessage();
            JSONObject request;
            try {
                request = new JSONObject(cfp.getContent());
                if (!request.has("type")) {
                    reply = makeAuctionProposal(cfp);
                }
                else {
                    reply = makeBid(cfp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return reply;
        }

        private ACLMessage makeBid(ACLMessage cfp) throws JSONException {
            ACLMessage reply = cfp.createReply();
            JSONObject proposal = new JSONObject(cfp.getContent());
            double odd = proposal.getDouble("odd");
            String type = proposal.getString("type");
            double betValue = proposal.getDouble("bet_value");
            int gameAuctionId = proposal.getInt("game_id");
            Belief belief = Player.this.beliefs.get(gameAuctionId);

            if(belief != null && Player.this.getBalance() > (odd * betValue)){
                Random r = new Random();
                int probability = r.nextInt(99) + 1;
                if (odd > belief.getOdd(type)) {
                    if (probability > 90) { // change to his belief
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent("yes");
                    }
                    else {
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("no");
                    }
                }
                else {
                    if (probability < 90) { // change to his belief
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent("yes");
                    }
                    else {
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("no");
                    }
                }
            }
            else {
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("no");
            }

            return reply;
        }

        private ACLMessage makeAuctionProposal(ACLMessage cfp) throws JSONException {
            JSONObject proposal = new JSONObject(cfp.getContent());
            ACLMessage reply = cfp.createReply();
            int gameAuctionId = proposal.getInt("game_id");
            Auction auction = Player.this.auctions.get(gameAuctionId);

            if(auction != null){
                proposal.put("type", auction.getType());
                proposal.put("odd", auction.getOdd());
                proposal.put("bet_value", auction.getBetValue());

                reply.setPerformative(ACLMessage.PROPOSE);
                String proposalMessage = proposal.toString();
                reply.setContent(proposalMessage);
            }
            else {
                reply.setPerformative(ACLMessage.REFUSE);
            }

            return reply;
        }

        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            System.out.println(myAgent.getLocalName() + " got a reject...");
        }

        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            System.out.println(myAgent.getLocalName() + " got an accept!");
            ACLMessage result = accept.createReply();
            result.setPerformative(ACLMessage.INFORM);
            result.setContent("this is the result");

            return result;
        }
    }

    public int getId() {
        return id;
    }
}
