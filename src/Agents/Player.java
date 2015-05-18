package Agents;

import Components.Bet;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.ArrayList;

public class Player extends Agent {

    public static String TYPE = "PLAYER";

    private String name;
    private double balance; // actual money
    private Integer id;


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

    public String getPlayerName() {
        return name;
    }
}
