package server.view;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import server.MasterListener;
import server.RegisteredClient;

public class ServerFrame extends JFrame{
	private JPanel mainPanel;
	private JTable clientList;
	private MasterListener listener;
	
	public ServerFrame(){
		this.add(this.getMainPanel());
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("iServer");
		this.setMinimumSize(new Dimension(550,550));
		this.setPreferredSize(new Dimension(550,550));
	}
	
	public JPanel getMainPanel(){
		if(mainPanel != null) return mainPanel;
		
		mainPanel = new JPanel();
		
		String[] cols = new String[]{"Login", "IP Address"};
		
		DefaultTableModel m = new DefaultTableModel(cols, 1);
		clientList = new JTable(m);
		
		JScrollPane scrollPane = new JScrollPane(clientList);
		clientList.setFillsViewportHeight(true);
		
		mainPanel.add(scrollPane);
		
		return mainPanel;
	}

	public void setListener(MasterListener listener) {
		this.listener = listener;
	}
	
	public JTable getClientList(){
		return clientList;
	}

	public MasterListener getListener() {
		if(listener != null) return listener;
		
		listener = new MasterListener() {
			
			@Override
			public void changeClients(ArrayList<RegisteredClient> clients) {
				DefaultTableModel m = new DefaultTableModel();
				RegisteredClient[] dataVector = new RegisteredClient[clients.size()];
				clients.toArray(dataVector);
				String[][] data = new String[dataVector.length][2];//();
				
				System.out.println("Inside changeClients");
				for(int i = 0; i<dataVector.length; i++){
					data[i][0] = dataVector[i].getUserName();
					data[i][1] = dataVector[i].getIp();
					
					System.out.println(data[i][0]);
					System.out.println(data[i][1]);
					
				}
				
				String[] columnIdentifiers = new String[]{"Login", "IP Address"};
				m.setDataVector(data, columnIdentifiers);
				getClientList().setModel(m);
				pack();
			}
		};
		
		return listener;
	}
	
	
	

}
