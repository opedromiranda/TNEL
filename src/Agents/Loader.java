package Agents;

import App.Utils;
import Components.Auction;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


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
        Auctioneer auctioneer = new Auctioneer();

        for(Player p : Utils.players){
            AgentContainer agentContainer = getContainerController();
            AgentController agentController = agentContainer.acceptNewAgent(p.getPlayerName(), p);
            agentController.start();
        }

        AgentContainer agentContainer = getContainerController();
        AgentController agentController = agentContainer.acceptNewAgent("Auctionner", auctioneer);
        agentController.start();
    }
}
