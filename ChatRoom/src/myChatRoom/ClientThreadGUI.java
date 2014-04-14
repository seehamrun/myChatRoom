package myChatRoom;

import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.awt.event.*;

public class ClientThreadGUI
{
	//globals
	private  ClientThread client;
	public String userName = "";
	private int privateChatID;
	//GUI Globals - Main Window
	protected  	JFrame 	frame;
	private  	JGradientButton aboutBtn;
	protected  	JGradientButton connectBtn;
	protected  	JGradientButton disconnectBtn;
	protected  	JGradientButton sendBtn;
	private  	JLabel 	msgLbl;
	private  	JLabel 	loggedInAsLbl;
	private  	JLabel 	loggedInAsBoxLbl;
	private  	JLabel 	onlineLbl;
	public 		JTextField msgTxtFld;
	public 		JTextArea conversationTxtAr;
	private  	JScrollPane conversationScrollPane;
	private  	JScrollPane onlineScrollPane;
	protected 		JList 	onlineJL;
	
	//GUI globals - LogIn Window
	public  	JFrame 	LogInWindow;
	public  	JTextField userNameFld;
	private  	JButton enterBtn;
	private  	JLabel 	enterUserNameLbl;
	private  	JPanel 	login_panel = new JPanel();
	
//-----------------------------------------------------------------------------------------------
	public static void main(String args[])
	{
		new ClientThreadGUI();
	}
//-----------------------------------------------------------------------------------------------
	//constructor
	public ClientThreadGUI(){
		privateChatID=-1;	//when a client requests to open a private chat, this ID wil be incremented and thus unique to each private chatroom
		BuildMainWindow();
		Initialize();
	}
//-----------------------------------------------------------------------------------------------
	public void BuildMainWindow()
	{
		frame = new JFrame();
		frame.setTitle(userName+"'s Chat Box");
		frame.setSize(450,500);
		frame.setLocation(220,180);
		frame.setResizable(false);
		Configure();	//puts all the objects in position in the chatroom
		MainWindow_Action();	//attaches event handlers to all the objects
		frame.setVisible(true);
	}
//-----------------------------------------------------------------------------------------------
	public  void Initialize()
	{
		sendBtn.setEnabled(false);
		disconnectBtn.setEnabled(false);
		connectBtn.setEnabled(true);
	}
//-----------------------------------------------------------------------------------------------
	public void Connect()
	{
		try
		{
			final int port = 10020;
			final String host = InetAddress.getLocalHost().getHostName();	//all chats opened on the same computer - change to open on other computers
			Socket s = new Socket(host,port);
			System.out.println("You connected to: "+host);
			
			client = new ClientThread(s, this);

			//Send Name to add to "OnLine" list
			PrintWriter output = new PrintWriter(s.getOutputStream());
			output.println(userName); //when the client socket is accepted, it immediately prints the user name which is caught by the Server class and added to the list of user names
			output.flush();
			
			Thread thread = new Thread(client);
			thread.start();			
			
		}//61
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			JOptionPane.showMessageDialog(null, "Server not responding.");
			System.exit(0);
		}
	}
	
//-----------------------------------------------------------------------------------------------
	//called when connect button is clicked
	public  void BuildLogInWindow()
	{
		LogInWindow = new JFrame();
		LogInWindow = new JFrame();

		LogInWindow.setTitle("What's your name?");
		LogInWindow.setSize(400, 100);
		LogInWindow.setLocation(250, 200);
		LogInWindow.setResizable(false);
		
		enterUserNameLbl = new JLabel("Enter username: ");
		userNameFld = new JTextField(20);
		enterBtn = new JButton("ENTER");
		
		login_panel = new JPanel();
		login_panel.add(enterUserNameLbl);
		login_panel.add(userNameFld);
		login_panel.add(enterBtn);
		LogInWindow.add(login_panel);
		
		attachLoginListeners();	//attaches listeners to enterBtn and userNameBoxTxtFld
		LogInWindow.setVisible(true);
	}
	
//-----------------------------------------------------------------------------------------------
	public  void attachLoginListeners()
	{
		enterBtn.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{ 
						getUserName();
						
					}
				}
			);
		userNameFld.addKeyListener(
			new KeyListener(){
				public void keyPressed(KeyEvent e){
					if(e.getKeyCode() == KeyEvent.VK_ENTER){
						getUserName();
					}
				}
				public void keyReleased(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}
			}
		);
	}
//-----------------------------------------------------------------------------------------------
	public void getUserName(){
		if(!userNameFld.getText().equals(""))
		{
			userName = userNameFld.getText().trim();
			loggedInAsBoxLbl.setText(userName);
			frame.setTitle(userName+"'s Chat Box");
			LogInWindow.setVisible(false);
			sendBtn.setEnabled(true);
			disconnectBtn.setEnabled(true);
			connectBtn.setEnabled(false);
			Connect();	
		}
		else
		{ JOptionPane.showMessageDialog(null, "Please enter a name!"); }
	}
	
//-----------------------------------------------------------------------------------------------
	public  void Configure()
	{
		frame.setSize(500,350);;
		frame.setLayout(null);		
		
		
		loggedInAsLbl = new JLabel();
		loggedInAsLbl.setFont(new Font("Tahoma", Font.BOLD,12));
		loggedInAsLbl.setText("Currently Logged In As");
		frame.getContentPane().add(loggedInAsLbl);
		loggedInAsLbl.setBounds(10,10,150,15);
		
		loggedInAsBoxLbl = new JLabel();
		loggedInAsBoxLbl.setHorizontalAlignment(SwingConstants.CENTER);
		loggedInAsBoxLbl.setFont(new Font("Tahoma",0,12));
		loggedInAsBoxLbl.setForeground(new Color(255,0,0));
		loggedInAsBoxLbl.setBackground(Color.WHITE);
		loggedInAsBoxLbl.setBorder(
				BorderFactory.createLineBorder(new Color(0,0,0)));
		frame.getContentPane().add(loggedInAsBoxLbl);
		loggedInAsBoxLbl.setBounds(10,25,150,20);
		
		connectBtn = new JGradientButton("CONNECT");
		connectBtn.setToolTipText("");
		frame.add(connectBtn);
		connectBtn.setBounds(175, 15, 90,30);
		
		disconnectBtn = new JGradientButton("DISCONNECT");
		frame.add(disconnectBtn);
		disconnectBtn.setBounds(280, 15, 110, 30);
		
		aboutBtn = new JGradientButton("ABOUT");
		frame.add(aboutBtn);
		aboutBtn.setBounds(405, 15, 75, 30);
		
		onlineLbl = new JLabel();
		onlineLbl.setHorizontalAlignment(SwingConstants.CENTER);
		onlineLbl.setText("Currently Online");
		onlineLbl.setToolTipText("");
		frame.getContentPane().add(onlineLbl);
		onlineLbl.setBounds(10,55,100,15);
		
		onlineJL = new JList();
		onlineJL.setForeground(new Color(0,0,255));
		
		onlineScrollPane = new JScrollPane();
		onlineScrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		onlineScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		onlineScrollPane.setViewportView(onlineJL);
		frame.getContentPane().add(onlineScrollPane);
		onlineScrollPane.setBounds(10,75,130,200);
		
		conversationTxtAr = new JTextArea();
		conversationTxtAr.setColumns(20);
		conversationTxtAr.setFont(new Font("Tahoma",0,12));
		conversationTxtAr.setForeground(new Color(0,0,255));
		conversationTxtAr.setLineWrap(true);
		conversationTxtAr.setRows(5);
		conversationTxtAr.setEditable(false);
		
		conversationScrollPane = new JScrollPane();
		conversationScrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		conversationScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		conversationScrollPane.setViewportView(conversationTxtAr);
		frame.getContentPane().add(conversationScrollPane);
		conversationScrollPane.setBounds(150,60,330,215);
		
		msgLbl = new JLabel();
		msgLbl.setText("Message:");
		frame.getContentPane().add(msgLbl);
		msgLbl.setBounds(10, 290, 60, 20);
		
		msgTxtFld = new JTextField();
		msgTxtFld.setBackground(new Color(255,255,255));
		msgTxtFld.requestFocus();
		frame.getContentPane().add(msgTxtFld);
		msgTxtFld.setBounds(70,285,340,30);
		
		sendBtn = new JGradientButton("SEND");
		frame.add(sendBtn);
		sendBtn.setBounds(415,285,65,30);
		
		
	}
//-----------------------------------------------------------------------------------------------
	public void MainWindow_Action()
	{
		
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
               try {
            	   if(client!=null)
            		   client.disconnect(1);//the 1 indicates that we are only closing this one application and not a full system shutdown
            		   
               } catch (IOException e) {
            	   conversationTxtAr.append("Error: "+e+"\n");
               }	
            }
        });
        
		sendBtn.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{ SEND(); }
			}
		);
		
		msgTxtFld.addKeyListener(
			new KeyListener(){
				public void keyPressed(KeyEvent e){
					if(e.getKeyCode() == KeyEvent.VK_ENTER){
						SEND();
					}
				}
				public void keyReleased(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}
			}
		);
		disconnectBtn.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{ 
					try {
						client.disconnect(1);	//this 1 is to indicate it's not a system wide shutdown - just a single user shutdown - can be any number other than 2
					} catch (IOException e1) {
						conversationTxtAr.append("Error: Could not disconnect.\n");
					} 
				}
			}
		);
		
		connectBtn.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{ BuildLogInWindow(); }
			}
		);
		
		aboutBtn.addActionListener(	
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{ 
						String Programmers = "<div style= 'text-decoration:underline font-weight:bold'>Programmers:</div><br>"+
				 				"Bridget Basan<br>Jeet Patel<br>Emily Austin<br>Siham Hussein<br>Mark Hallenbeck<br><br>";
						String Class = "<div style = 'text-decoration: underline font-weight: bold'>Class:</div><br>"+
				 				"CS 342: Software Design<br>"+"Univesity of Illinois at Chicago<br>"+"Professor Patrick Troy - "
				 						+ "Spring 2014<br><br>";
						String instructions = "<div style= 'text-decoration:underline font-weight:bold'>Instructions:</div><br>To chat privately, highlight and right click on the users in the Currently Online users list. "
								+"You can send private messages to multiple users by holding down the shift key.) "
									+ "Type your message in the window that pops up.<br><br>";
						String msg = "<html><body style='width: 300px'>"+instructions+Programmers+Class;
						JOptionPane.showMessageDialog(null,msg,"Chat Program",JOptionPane.INFORMATION_MESSAGE);
					}
				}
			);
		onlineJL.addMouseListener(new MouseAdapter(){
			
			JOptionPane pane = new JOptionPane();
			public void mouseClicked(MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)){

					JList list = (JList)evt.getSource();
		            List<String> privateChatters = list.getSelectedValuesList();
//		            String[] strarray = privateChatters.toArray(new String[0]);
		            int size = privateChatters.size();
		            for(int i=0;i<size;i++){
		            	if(privateChatters.get(i).equals(userName)){
		            		privateChatters.remove(i);
		            		size--;
		            	}
		            }
		             privateChatters.add(userName); 	//add initiator to the list so that s/he also sees the message
		            
		            String chatters = "";
		            for(int i=0;i<(privateChatters.size()-1);i++){
		            	chatters= chatters+ privateChatters.get(i)+", ";
		            }
		            chatters= chatters+ "and "+privateChatters.get(privateChatters.size()-1);
		            
		            JFrame privateMsg = new JFrame("Private message to "+chatters);
		            privateMsg.setLayout(null);
		            privateMsg.setSize(405, 75);
		            privateMsg.setResizable(false);
		            privateMsg.setLocation(230,190);

		            JTextField msg = new JTextField();
		            privateMsg.add(msg);    
		            
		            msg.requestFocus();
		            msg.setBounds(10,10,290,30);
		            
		            JGradientButton b = new JGradientButton("SEND");
		            privateMsg.add(b);
		            b.setBounds(310,10,80,30);
		            
		            b.addActionListener(new sendPrivate(privateChatters,msg,privateMsg));		        		
		            msg.addKeyListener(new sendPrivateEnter(privateChatters,msg,privateMsg));
		            
		            privateMsg.setVisible(true);
				}		        
		    }			
		});
	}
	private class sendPrivate implements ActionListener{
		List<String> chatters;
		JTextField msg1;
		JFrame privateMsg;
		public void actionPerformed(ActionEvent e){
			System.out.println("chatters: "+chatters);
			sendPrivateMessage(chatters,msg1,privateMsg);
		}
		public sendPrivate(List<String> people,JTextField msg,JFrame frame){
			privateMsg = frame;
			chatters = people;
			msg1=msg;
		}
	}
	private class sendPrivateEnter implements KeyListener{
		List<String> chatters;
		JTextField msg1;
		JFrame privateMsg;
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				System.out.println("chatters: "+chatters);
				sendPrivateMessage(chatters,msg1,privateMsg);
			}
			
		}
		public sendPrivateEnter(List<String> people,JTextField msg,JFrame frame){
			privateMsg = frame;
			chatters = people;
			msg1=msg;
		}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}
	

//-----------------------------------------------------------------------------------------------
		public void SEND()
		{
			if(!msgTxtFld.getText().equals(""))
			{
				String s = msgTxtFld.getText();
				client.send(s);	//caught by ClientThread and send to ServerThread
				msgTxtFld.requestFocus();	
			}else{
				msgTxtFld.requestFocus();
			}
		}

//-----------------------------------------------------------------------------------------------
		public void sendPrivateMessage(List<String> chatters,JTextField msg,JFrame frame){
			if(!msg.getText().equals(""))
			{
//				conversationTxtAr.append("chatters: "+chatters);
				String s = ""+ msg.getText();
				client.sendPrivate(chatters,s);	//caught by ClientThread and send to ServerThread
				msg.requestFocus();	
				frame.dispose();
			}else{
				msgTxtFld.requestFocus();
			}
		}
}

