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
    private String type; // seller or buyer
    private double initialMoney;
    private double balance; // actual money
    ArrayList<Bet> gameBets;


    public Player(String name, String type, double initialMoney, ArrayList<Bet> gameBets) {
        this.name = name;
        this.type = type;
        this.initialMoney = initialMoney;
        this.balance = initialMoney;
        this.gameBets = gameBets;
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
