package Agents.behaviours;

import Agents.Player;
import Components.Auction;
import Components.Game;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.json.JSONException;

import java.util.Vector;

/**
 * Created by pedro on 28/05/15.
 */
public class AuctionBehaviour extends ContractNetInitiator {

    FixtureBehaviour parentBehavior;
    Game game;
    Player player;
    Auction auction;

    public AuctionBehaviour(Agent a, ACLMessage cfp, FixtureBehaviour parentBehaviour, Game game, Player player) {
        super(a, cfp);
        this.parentBehavior = parentBehaviour;
        this.game = game;
        this.player = player;
        auction = player.auctions.get(game.getId());
    }

    protected Vector prepareCfps(ACLMessage cfp) {
        /*
        Vector v = new Vector();

        DFAgentDescription[] players = getPlayers(Player.TYPE);
        for (int i = 0; i < players.length; ++i) {
            cfp.addReceiver(players[i].getName());
        }

        cfp.setContent(auction.toString());

        v.add(cfp);*/
        System.out.println("começou");
        return null;
    }

    protected void handleAllResponses(Vector responses, java.util.Vector acceptances) {
        System.out.println("recebeu");
        /*if (responses.size() <= 1) {
            System.out.println("Winner : " + ((ACLMessage) responses.get(0)).getSender());
            ACLMessage msg = ((ACLMessage) responses.get(0)).createReply();
            msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            acceptances.add(msg);
            this.done();
        }
        else {
            try {
                //incrementBid();
                for(int i=0; i<responses.size(); i++) {
                    ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
                    msg.setPerformative(ACLMessage.CFP);
                    msg.setContent(auction.toString());
                    acceptances.add(msg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }*/
    }

    /*private void incrementBid() throws JSONException {
        double actualBid = auction.getDouble("odd");
        auction.put("odd", actualBid + 0.1);
    }*/

    protected void handleAllResultNotifications(java.util.Vector resultNotifications) {
        System.out.println("got " + resultNotifications.size() + " result notifs!");
    }

    public DFAgentDescription[] getPlayers(String TYPE) {

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(TYPE);
        template.addServices(sd);
        try {
            return DFService.search(this.getAgent(), template);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return null;
    }

}
