package Agents.behaviours;


import Agents.Player;
import Components.Auction;
import Components.Belief;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

import java.util.Random;

public class AuctionPlayerBehavior extends ContractNetResponder {

    public AuctionPlayerBehavior(Player a, MessageTemplate mt) {
        super(a, mt);
    }

    protected ACLMessage handleCfp(ACLMessage cfp) {
        Auction auction = null;
        ACLMessage reply = cfp.createReply();
        try {
            auction = (Auction) cfp.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        boolean choice = makeBid(auction);

        if (choice)
            reply.setPerformative(ACLMessage.PROPOSE);
        else
            reply.setPerformative(ACLMessage.REFUSE);

        return reply;
    }

    private boolean makeBid(Auction auction) {
        int gameId = auction.getGameId();
        double odd = auction.getActualOdd();
        double betValue = auction.getBetValue();
        String type = auction.getType();
        Belief beliefForGame = getPlayer().getBeliefForGame(gameId);

        if (beliefForGame != null && getPlayer().getBalance() > (odd * betValue)) {
            double beliefDegree = beliefForGame.getBeliefDegree();
            Random r = new Random();
            int probability = r.nextInt(99) + 1;

            if (odd <= beliefForGame.getOdd(type))
                return probability < beliefDegree;
            else
                return probability > beliefDegree;
        }

        return false;

    }

    protected void handleOutOfSequence(ACLMessage msg){
        System.out.println("MENSAGEM FORA SEQUENCIA: " + msg.getContent());
    }

    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        //System.out.println(myAgent.getLocalName() + " got a reject...");
    }

    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        //System.out.println(myAgent.getLocalName() + " got an accept!");
        ACLMessage result = accept.createReply();
        result.setPerformative(ACLMessage.INFORM);

        return result;
    }

    public Player getPlayer() {
        return (Player) this.getAgent();
    }
}
