package Agents;

import Agents.behaviours.FixtureBehaviour;
import App.Utils;
import Components.Auction;
import Components.Belief;
import Components.Calendar;
import Components.Game;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;


public class Auctioneer extends Agent {

    public static String TYPE = "AUCTIONEER";

    private Calendar calendar;

    protected void setup()
    {
        System.out.println("Auctioneer agent " + getLocalName() + " started.");
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(TYPE);
        sd.setName(getLocalName());
        register(sd);
        this.calendar = Utils.calendar;
        SequentialBehaviour gameDays = play();
        addBehaviour(gameDays);
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

    private SequentialBehaviour play() {

        Scanner sc = new Scanner(System.in);
        sc.hasNextLine();

        SequentialBehaviour gameDays = new Sequential(this);

        for (ArrayList<Game> games : calendar.days) {
            gameDays.addSubBehaviour(new FixtureBehaviour(this, games));
        }

        return gameDays;
    }

    public DFAgentDescription[] getPlayers(String TYPE) {

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(TYPE);
        template.addServices(sd);
        try {
            return DFService.search(Auctioneer.this, template);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return null;
    }

    public class Sequential extends SequentialBehaviour{

        public Sequential(Agent a) {
            super(a);
        }

        @Override
        public int onEnd() {
            printAllUsers();
            return super.onEnd();
        }

        private void printAllUsers() {
            for(Player p : Utils.players){
                System.out.println("PLAYER " + p.getLocalName() + " with " + p.getBalance() + "€");
            }

        }
    }

}