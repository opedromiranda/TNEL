package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Vector;


public class Sender extends Agent {

    public void setup() {
        addBehaviour(new FIPAContractNetInit(this, new ACLMessage(ACLMessage.CFP)));
    }

    class FIPAContractNetInit extends ContractNetInitiator {

        public FIPAContractNetInit(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();
            System.out.println("envia");

            //cfp.addReceiver(new AID("a1", false));
            //cfp.addReceiver(new AID("a2", false));
            cfp.addReceiver(new AID("a5", false));
            cfp.setContent("this is a call...");

            v.add(cfp);

            return v;
        }

        protected void handleAllResponses(Vector responses, java.util.Vector acceptances) {

            System.out.println("got " + responses.size() + " responses!");

            for(int i=0; i<responses.size(); i++) {
                ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
                msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // OR NOT!
                acceptances.add(msg);
            }
        }

        protected void handleAllResultNotifications(java.util.Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }

    }
}
