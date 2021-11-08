
import jade.core.AID;
import jade.core.Agent;

import jade.wrapper.*;

import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class MainAgent extends Agent {
	private static final long serialVersionUID = 1L;
	private AgentController ac = null;
	private AID[] subCoord;
	MainAgentGUI mainAgentGUI;
	String host;
	int AgentCounter=0;
	protected void setup() {
		mainAgentGUI = new MainAgentGUI(this);
		mainAgentGUI.setVisible(true);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("MainAgent");
		sd.setName("JADE-attack");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	    ReceiveMsg();
	}

	// Cancel Button 
	protected void takeDown() {
		System.out.println("Remove Agent from Container");
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		mainAgentGUI.dispose();
		System.out.println(getAID().getName()+" terminating.");
	}

	//Send Button
	@SuppressWarnings("serial")
	public void SendMessage(String action,String agentsNumber, String host2, String port, String ticker){
		addBehaviour(new OneShotBehaviour() {
        public void action() {
        	//Search for the Sub Agent
        	String subCoordinatorName=mainAgentGUI.getMiddleMan();
        	System.out.println(subCoordinatorName+" is selected, and ready for creating sub-agents");
        	if (action.contains("Create")){
        		ACLMessage msg = new ACLMessage(16);
                msg.addReceiver(new AID(subCoordinatorName, AID.ISLOCALNAME));
                msg.setLanguage("English");
                String msgToSend=action+";"+agentsNumber+";"+host2+";"+port+";"+ticker;
                msg.setContent(msgToSend);
                send(msg);
                int tmp=Integer.parseInt(agentsNumber);
                AgentCounter=AgentCounter+tmp;
                System.out.println("****I Sent Message to::>"+subCoordinatorName+"*****"+"\n"+
                                    "The Content of My Message is::>"+ msg.getContent());
        	}
        	if (action.contains("Attack")||action.contains("StopAttack"))
        	{
        		ACLMessage msg = new ACLMessage(16);
                msg.addReceiver(new AID(subCoordinatorName, AID.ISLOCALNAME));
                msg.setLanguage("English");
                String msgToSend=action+";"+agentsNumber+";"+host2+";"+port+";"+ticker;
                msg.setContent(msgToSend);
                send(msg);
                System.out.println("****I Sent Message to::>"+subCoordinatorName+"*****"+"\n"+
                                    "The Content of My Message is::>"+ msg.getContent());
        	 	}       	    	
        	}
		} );
    }
	
	//MAIN AGENT LISTENS TO RECEIVED MESSAGES
	@SuppressWarnings("serial")
	public void ReceiveMsg(){
		addBehaviour(new CyclicBehaviour() {
			    private String Message_Performative;
		        private String Message_Content;
		        private String SenderName;
		        @SuppressWarnings("static-access")
				public void action() {
		            ACLMessage msg = receive();
		            if(msg != null) {
		                Message_Performative = msg.getPerformative(msg.getPerformative());
		                Message_Content = msg.getContent();
		                SenderName = msg.getSender().getLocalName();
		                System.out.println(" ****The Architect received msg from ***"+ SenderName+ ":" + Message_Content);		               
		            }
		        }
		} );
    } 

	@SuppressWarnings("serial")
	public void updateSubAgentList() {
		addBehaviour(new OneShotBehaviour() {	 
			
			@Override
			public void action() {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("SubCoordinator");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					System.out.println("Found the following agents:");
					subCoord = new AID[result.length];
					mainAgentGUI.clearSubAgents();
					for (int i = 0; i < result.length; ++i) {
						subCoord[i] = result[i].getName();
						System.out.println(subCoord[i].getName());					
						mainAgentGUI.addSubAgents(subCoord[i].getName()); //ading the subCoordinators agents to the ComboBox 
					}					
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});
	}
	
	///HERE STARTS THE GUI
	@SuppressWarnings("serial")
	class MainAgentGUI extends JFrame {

		private JPanel contentPane;
		private JTextField txtURL, txtAgentNumber, txtTicker;
		private MainAgent mainAgent;
		@SuppressWarnings("rawtypes")
		private JComboBox cmbMiddleMan;
		private JLabel lblPreparedAgents;

		public MainAgentGUI(MainAgent mainAgent) {
			super(mainAgent.getLocalName());
	        this.mainAgent = mainAgent;
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 548, 384);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel lblNewLabel = new JLabel("Choose Middle Man");
			lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblNewLabel.setBounds(12, 13, 197, 16);
			contentPane.add(lblNewLabel);
			
			cmbMiddleMan = new JComboBox();
			cmbMiddleMan.setFont(new Font("Tahoma", Font.PLAIN, 15));
			cmbMiddleMan.setBounds(12, 29, 441, 32);
			contentPane.add(cmbMiddleMan);
			
			JButton btnRefresh = new JButton(new ImageIcon(getClass().getResource("/image/refresh_16x16x32.png")));
			btnRefresh.setBounds(455, 29, 45, 32);
			contentPane.add(btnRefresh);
			
			btnRefresh.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                //call function from listing all sub coord
	            	mainAgent.updateSubAgentList();
	            }
	        }); 
			
			JLabel lblNewLabel_1 = new JLabel("URL of the server");
			lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblNewLabel_1.setBounds(12, 90, 228, 16);
			contentPane.add(lblNewLabel_1);
			
			txtURL = new JTextField();
			txtURL.setFont(new Font("Tahoma", Font.PLAIN, 15));
			txtURL.setBounds(12, 107, 488, 32);
			contentPane.add(txtURL);
			txtURL.setColumns(10);
			
			JLabel lblNewLabel_2 = new JLabel("Number of Agents");
			lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblNewLabel_2.setBounds(12, 171, 242, 16);
			contentPane.add(lblNewLabel_2);
			
			txtAgentNumber = new JTextField();
			txtAgentNumber.setFont(new Font("Tahoma", Font.PLAIN, 15));
			txtAgentNumber.setBounds(12, 188, 242, 32);
			contentPane.add(txtAgentNumber);
			txtAgentNumber.setColumns(10);
			
			JLabel lblNewLabel_2_1 = new JLabel("Ticker Behaviour");
			lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblNewLabel_2_1.setBounds(258, 171, 242, 16);
			contentPane.add(lblNewLabel_2_1);
			
			txtTicker = new JTextField();
			txtTicker.setFont(new Font("Tahoma", Font.PLAIN, 15));
			txtTicker.setColumns(10);
			txtTicker.setBounds(258, 188, 242, 32);
			contentPane.add(txtTicker);
			
			JButton btnReady = new JButton("Ready");
			btnReady.setFont(new Font("Tahoma", Font.PLAIN, 15));
			btnReady.setBounds(101, 287, 97, 37);
			contentPane.add(btnReady);
			btnReady.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	String ticker = txtTicker.getText(); //ticker
	            	String agentsNumber = txtAgentNumber.getText(); //no agents
	            	String address = txtURL.getText(); //host port
	            	String newAddress[]=address.split(":");
	        		String host=newAddress[0];
	        		String port = newAddress[1];
	        		String action="Create";
	        		lblPreparedAgents.setText(agentsNumber + " agents are prepared for attack"); 
	        		mainAgent.SendMessage(action,agentsNumber, host, port, ticker);
	        		
	            }
	        });
			
			JButton btnAttack = new JButton("Attack");
			btnAttack.setFont(new Font("Tahoma", Font.PLAIN, 15));
			btnAttack.setBounds(210, 287, 97, 37);
			contentPane.add(btnAttack);
			btnAttack.addActionListener(new ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) { 
	            	String ticker = txtTicker.getText(); //ticker
	            	String agentsNumber = txtAgentNumber.getText(); //no agents
	            	String address = txtURL.getText(); //host port
	            	String newAddress[]=address.split(":");
	        		String host2=newAddress[0];
	        		String port = newAddress[1];
	        		String action="Attack";
	            	mainAgent.SendMessage(action, agentsNumber, host2, port, ticker);
	            }
	        });
			
			JButton btnStop = new JButton("Stop");
			btnStop.setFont(new Font("Tahoma", Font.PLAIN, 15));
			btnStop.setBounds(319, 287, 97, 37);
			contentPane.add(btnStop);
			btnStop.addActionListener(new ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	//AttackAgentsLbl1.setText("0");
	            	String ticker = txtTicker.getText(); //ticker
	            	String agentsNumber = txtAgentNumber.getText(); //no agents
	            	String address = txtURL.getText(); //host port
	            	String newAddress[]=address.split(":");
	        		String host2=newAddress[0];
	        		String port = newAddress[1];
	        		String action="StopAttack";
	            	mainAgent.SendMessage(action, agentsNumber, host2, port, ticker);
	            }
	        });
			
			lblPreparedAgents = new JLabel("No agent is prepared for attack");
			lblPreparedAgents.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblPreparedAgents.setBounds(12, 255, 488, 19);
			contentPane.add(lblPreparedAgents);
		}

	    
		public String getMiddleMan() {
			String name=cmbMiddleMan.getSelectedItem().toString();
			return name;
		}
		
		//SETTING UP THE LBL FOR INFORMATION
		public void preparedAgents(int number){
			String tmp = Integer.toString(number);
			lblPreparedAgents.setText(tmp + " agents are ready for attack");
		}
		
		public void addSubAgents(String name) {
			String tmp[]=name.split("@");
			Object a = tmp[0];
			cmbMiddleMan.addItem(a);
		}
		
		public void clearSubAgents() {
			cmbMiddleMan.removeAllItems();
		}
	}

}
