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


public class Auctioneer extends Agent {

    public static String TYPE = "AUCTIONEER";

    protected void setup()
    {
        System.out.println("Auctioneer agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(TYPE);
        sd.setName(getLocalName());
        register(sd);
        Utils.getElements();
        Utils.loadCalendar();
        try {
            callForPlayers();
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

    private void callForPlayers() throws StaleProxyException {

        AgentContainer agentContainer;
        AgentController agentController;

        for(Player p : Utils.players){
            agentContainer = getContainerController();
            agentController = agentContainer.acceptNewAgent(p.getPlayerName(), p);
            agentController.start();
        }
    }

}