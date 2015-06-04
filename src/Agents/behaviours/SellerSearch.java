package Agents.behaviours;

import Agents.Player;
import Components.Game;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.ArrayList;
import java.util.Vector;

public class SellerSearch  extends AchieveREInitiator {
    Game game;
    FixtureBehaviour parentBehaviour;
    ArrayList<ACLMessage> sellers = new ArrayList<ACLMessage>();

    public SellerSearch(Agent a, ACLMessage msg, FixtureBehaviour parentBehaviour, Game game) {
        super(a, msg);
        this.game = game;
        this.parentBehaviour = parentBehaviour;
    }

    protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
        System.out.println("Searching for sellers for game " + game.getId());

        Vector<ACLMessage> v = new Vector<ACLMessage>();

        ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
        m.setContent(game.getId() + "");

        DFAgentDescription[] players = getPlayers(Player.TYPE);
        for (int i = 0; i < players.length; ++i) {
            m.addReceiver(players[i].getName());
        }

        v.add(m);
        return v;
    }

    @Override
    protected void handleAgree(ACLMessage agree) {
        sellers.add(agree);
        super.handleAgree(agree);
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
        super.handleRefuse(refuse);
    }

    @Override
    protected void handleAllResponses(Vector responses) {
        super.handleAllResponses(responses);
        parentBehaviour.onEndSellerSearch(sellers);
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
