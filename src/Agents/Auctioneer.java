package Agents;

import Agents.behaviours.FixtureBehaviour;
import App.Utils;
import Components.Auction;
import Components.Belief;
import Components.Calendar;
import Components.Game;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;


public class Auctioneer extends Agent {

    public static String TYPE = "AUCTIONEER";

    private Calendar calendar;

    protected void setup()
    {
        System.out.println("Auctioneer agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(TYPE);
        sd.setName(getLocalName());
        register(sd);

        this.calendar = Utils.calendar;



        SequentialBehaviour gameDays = play();
        addBehaviour(gameDays);
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

    private SequentialBehaviour play() {

        SequentialBehaviour gameDays = new SequentialBehaviour();

        for (ArrayList<Game> games : calendar.days) {
            gameDays.addSubBehaviour(new FixtureBehaviour(this, games));
        }

        return gameDays;
    }

    public DFAgentDescription[] getPlayers(String TYPE) {

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(TYPE);
        template.addServices(sd);
        try {
            return DFService.search(Auctioneer.this, template);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return null;
    }

    class GameContractNet extends ContractNetInitiator {

        Game game;

        public GameContractNet(Agent a, ACLMessage msg, Game game) {
            super(a, msg);
            this.game = game;
        }

        protected Vector prepareCfps(ACLMessage cfp) {
            System.out.println("GAME: " + game.getId());
            Vector v = new Vector();
            int gameId = game.getId();

            DFAgentDescription[] players = getPlayers(Player.TYPE);
            for (int i = 0; i < players.length; ++i) {
                cfp.addReceiver(players[i].getName());
            }

            JSONObject requestForGame = new JSONObject();
            try {
                requestForGame.put("game_id", gameId);
                cfp.setContent(requestForGame.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            v.add(cfp);
            return v;
        }

        protected void handleAllResponses(Vector responses, java.util.Vector acceptances) {

            System.out.println("got " + responses.size() + " responses!");

            for(int i=0; i<responses.size(); i++) {
                ACLMessage msg = ((ACLMessage) responses.get(i));
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    addBehaviour(new AuctionGameContractNet(Auctioneer.this, new ACLMessage(ACLMessage.CFP), this, msg.getContent()));
                }
            }
        }

        protected void handleAllResultNotifications(java.util.Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }


    }

    class AuctionGameContractNet extends ContractNetInitiator {
        ContractNetInitiator parentBehavior;
        JSONObject auction;

        public AuctionGameContractNet(Agent a, ACLMessage msg, ContractNetInitiator parentBehavior, String auction) {
            super(a, msg);
            this.parentBehavior = parentBehavior;
            try {
                this.auction = new JSONObject(auction);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();

            DFAgentDescription[] players = getPlayers(Player.TYPE);
            for (int i = 0; i < players.length; ++i) {
                cfp.addReceiver(players[i].getName());
            }

            cfp.setContent(auction.toString());

            v.add(cfp);

            return v;
        }

        protected void handleAllResponses(Vector responses, java.util.Vector acceptances) {
            System.out.println("recebeu");
            if (responses.size() <= 1) {
                System.out.println("Winner : " + ((ACLMessage) responses.get(0)).getSender());
                ACLMessage msg = ((ACLMessage) responses.get(0)).createReply();
                msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                acceptances.add(msg);
                this.done();
            }
            else {
                try {
                    incrementBid();
                    for(int i=0; i<responses.size(); i++) {
                        ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
                        msg.setPerformative(ACLMessage.CFP);
                        msg.setContent(auction.toString());
                        acceptances.add(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        private void incrementBid() throws JSONException {
            double actualBid = auction.getDouble("odd");
            auction.put("odd", actualBid + 0.1);
        }

        protected void handleAllResultNotifications(java.util.Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }
    }

}