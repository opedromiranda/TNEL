package Agents.behaviours;

import Agents.Player;
import Components.Auction;
import Components.Game;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;


public class AuctionBehaviour extends ContractNetInitiator {

    FixtureBehaviour parentBehavior;
    Game game;
    Player player;
    Auction auction;
    ArrayList<AID> playersInGame;

    public AuctionBehaviour(Agent a, ACLMessage cfp, FixtureBehaviour parentBehaviour, Game game, Player player, ArrayList<AID> playersInGame) {
        super(a, cfp);
        this.parentBehavior = parentBehaviour;
        this.game = game;
        this.player = player;
        auction = player.auctions.get(game.getId());
        this.playersInGame = playersInGame;
    }

    protected Vector prepareCfps(ACLMessage cfp) {
        Vector v = new Vector();

        if (playersInGame.isEmpty()) {
            DFAgentDescription[] players = getPlayers(Player.TYPE);
            for (int i = 0; i < players.length; ++i) {
                cfp.addReceiver(players[i].getName());
            }
        }
        else {
            cfp = new ACLMessage(ACLMessage.CFP);
            for (AID playerAID : playersInGame) {
                cfp.addReceiver(playerAID);
            }
        }

        try {
            cfp.setContentObject((java.io.Serializable) this.auction);
        } catch (IOException e) {
            e.printStackTrace();
        }

        v.add(cfp);
        return v;
    }

    protected void handleAllResponses(Vector responses, java.util.Vector acceptances) {
        //System.out.println("handle responses: " + responses.size());

        for(int i=0; i < responses.size(); i++) {
            ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
            if ( ((ACLMessage) responses.get(i)).getPerformative() == ACLMessage.PROPOSE ) {
                msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            }
            else {
                msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
            }
            acceptances.add(msg);
        }
    }

    protected void handleAllResultNotifications(java.util.Vector resultNotifications) {
        //System.out.println("got " + resultNotifications.size() + " result notifs!");
        AuctionBehaviour auctionBehavior;

        if (resultNotifications.size() == 0) {
            auction.nextRound();
            if (auction.getRound() >= 3) {
                parentBehavior.incrementAuctionsFinished(auction);
            }
            auctionBehavior = new AuctionBehaviour(getAgent(), new ACLMessage(ACLMessage.CFP), parentBehavior, game, player, playersInGame);
            getAgent().addBehaviour(auctionBehavior);
        }
        else if(resultNotifications.size() == 1){
            System.out.println("Bidder Found for game " + game.getId() + ": " + ((ACLMessage) resultNotifications.get(0)).getSender().getLocalName());
            auction.setBuyerId(((ACLMessage) resultNotifications.get(0)).getSender().getLocalName());
            parentBehavior.incrementAuctionsFinished(auction);
        }
        else {
            auction.incrementActualOdd();
            playersInGame.clear();
            auctionBehavior = new AuctionBehaviour(getAgent(), new ACLMessage(ACLMessage.CFP), parentBehavior, game, player, playersInGame);
            for(int i = 0; i < resultNotifications.size(); i++){
                playersInGame.add(((ACLMessage) resultNotifications.get(i)).getSender());
            }
            getAgent().addBehaviour(auctionBehavior);
        }
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
