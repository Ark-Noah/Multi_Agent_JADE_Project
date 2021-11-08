
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JOptionPane;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgentSmith extends Agent {

	Socket server = null;
	String destinationHost = "";
	int destinationPort = 0;
	long tickInterval = 1000;
	boolean doAttack = false;
	SendRequest sr;

	protected void setup() {

		Object[] args = getArguments();
		tickInterval = Integer.parseInt((String) args[0]);
		destinationHost = (String) args[1];
		destinationPort = Integer.parseInt((String) args[2]);
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Smith");
		sd.setName(getName());
		sd.addOntologies("SmithAgent");
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
			doDelete();
		}
		addBehaviour(new ReceiveMessage());
	}

	public class SendRequest extends TickerBehaviour {
		long currentTick = 0;

		public SendRequest(Agent a, long period) {
			super(a, period);
			currentTick = period;
		}

		protected void onTick() {
			try {
				if (currentTick != tickInterval) {
					reset(tickInterval);
				}
				if (!doAttack) {
					done();
				}
				System.out.println("I am opening socket to " + destinationHost + ":" + destinationPort);
				server = new Socket(destinationHost, destinationPort);

				OutputStream output = server.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);

				InputStreamReader in = new InputStreamReader(server.getInputStream());
				BufferedReader bf = new BufferedReader(in);
				String str;

				// Make the Fibbonaci Request with the number 30
				int number = 30;
				writer.println(number);

				// Receive Message
				str = bf.readLine();
				System.out.println("Client printing : " + str);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.out.println("connection established");
		}
	}

	public class ReceiveMessage extends CyclicBehaviour {

		private String msgContent;
		private String senderName;

		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				msgContent = msg.getContent();
				senderName = msg.getSender().getLocalName();

				System.out.println(this.myAgent + " received a Message from" + senderName + ": " + msgContent);
				String str = msg.getContent();
				System.out.println(str);
				if (str.contains("Attack")) {
					SendRequest sr = new SendRequest(this.myAgent, 1000);
					addBehaviour(sr);
				}
				if (str.contains("StopAttack")) {
					System.out.println("attack terminated");
					sr.stop();
					try {
						DFService.deregister(myAgent);
					} catch (FIPAException e) {
						e.printStackTrace();
					}
				}
			} else {
				block();
			}
		}
	}

}
