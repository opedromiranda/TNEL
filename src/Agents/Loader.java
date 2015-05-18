package Agents;

import App.Utils;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;

/**
 * Created by pedro on 18/05/15.
 */
public class Loader extends Agent {

    protected void setup() {
        System.out.println("Loader agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType("LOADER");
        sd.setName(getLocalName());
        register(sd);

        try {
            loadPlayers();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
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

    public void loadPlayers() throws StaleProxyException {
        ArrayList<Player> players = Utils.loadPlayers();

        for(Player p : players){
            AgentContainer agentContainer = getContainerController();
            AgentController agentController = agentContainer.acceptNewAgent(p.getPlayerName(), p);
            agentController.start();
        }
    }
}
