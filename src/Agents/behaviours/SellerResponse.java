package Agents.behaviours;

import Agents.Player;
import Components.Auction;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class SellerResponse extends AchieveREResponder {

    public SellerResponse(Player a, MessageTemplate mt) {
        super(a, mt);
    }

    protected ACLMessage handleRequest(ACLMessage request) {
        ACLMessage reply = request.createReply();
        int gameId = Integer.parseInt(request.getContent());
        Auction auction = getPlayer().auctions.get(gameId);

        if(auction != null){
            System.out.println("NOTNULL");
            reply.setPerformative(ACLMessage.AGREE);
            reply.setContent("" + gameId);
        } else {
            reply.setPerformative(ACLMessage.REFUSE);
        }

        return reply;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
        ACLMessage result = request.createReply();
        result.setPerformative(ACLMessage.INFORM);
        return result;
    }

    public Player getPlayer() {
        return (Player) this.getAgent();
    }
}
