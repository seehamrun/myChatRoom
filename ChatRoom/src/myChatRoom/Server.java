package myChatRoom;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

public class Server{

	//globals	
	public ArrayList<Socket> ConnectionArray;	//this will hold all the sockets that are connected through this port	
	public ArrayList<String> CurrentUsers;		//this will contain all the user names

	public JFrame serverGUI;
	public JTextArea text;
	protected boolean serverContinue;
	private JButton endServer;
	public int x;
	public int y;
	private String user;
//----------------------------------------------------------------------------
	public static void main(String[] args){
		
		try{
			new Server();
		}catch (Exception e){
			System.out.println("Error: Cannot instantiate extension of ServerSocket.");
		}
		
	}
//----------------------------------------------------------------------------
	public Server(){
		ConnectionArray = new ArrayList<Socket>();	//this will hold all the sockets that are connected through this port
		CurrentUsers = new ArrayList<String>();		//this will contain all the user names
		
		serverContinue = true;
		x=0;
		y=0;
		buildServerGui();
		final int port = 10020;	//hard coded the port number - also hard coded on client side so the client doesn't have to enter the port numbern to see the chatroom
		try{
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(serverContinue){
				serverSocket.setSoTimeout(5000);
				text.append("Waiting for clients...\n");
				try{
					//accept socket
					Socket clientSocket = serverSocket.accept();
					//add socket to connection array
					ConnectionArray.add(clientSocket);
					//first thing the socket does is output the username - add to list of user names
					addUserName(clientSocket);
//					String user = CurrentUsers.get(ConnectionArray.indexOf(clientSocket));
					text.append("Client "+user+" connected from: "+clientSocket.getLocalAddress().getHostName()+"\n");
					
					//create a server thread 
					ServerThread st = new ServerThread(clientSocket,this);
					Thread t = new Thread(st);
					t.start();
					text.append("New Communication Thread Started.\n");
				}catch(SocketTimeoutException ste)
				{
					text.append("Timeout\t");
				}
				
			}			
		}catch(Exception ex){
			text.append("Error: Something is not right.\n");
			text.append("Error: "+ex+"\n");
			try{	Thread.sleep(3000);	}
			catch(Exception e){	}
             System.exit(1); 
		}
	}
//----------------------------------------------------------------------------		
	public void buildServerGui(){
		serverGUI = new JFrame();
		serverGUI.setTitle("Main Server");
		serverGUI.setResizable(true);
		serverGUI.setBackground(new Color(255,255,255));
		serverGUI.setSize(350,360);
		serverGUI.setLayout(null);			
		
		text = new JTextArea();
		text.setColumns(30);
		text.setFont(new Font("Tahoma",0,12));
		text.setForeground(new Color(0,0,255));
		text.setLineWrap(true);
		text.setRows(5);
		text.setEditable(false);
		
		JScrollPane conversationScrollPane = new JScrollPane();
		conversationScrollPane.setHorizontalScrollBarPolicy(	ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		conversationScrollPane.setVerticalScrollBarPolicy(		ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		conversationScrollPane.setViewportView(text);
		serverGUI.getContentPane().add(conversationScrollPane);
		conversationScrollPane.setBounds(10,10,315,260);
		
		endServer = new JButton("Close Server.");
		endServer.setFont(new Font("Tahoma",0,11));
		closeServer close = new closeServer();
		endServer.addActionListener(close);
		endServer.setBounds(200, 280, 100, 30);
		serverGUI.add(endServer);
		serverGUI.setVisible(true);
	}

	private class closeServer implements ActionListener{
		public void actionPerformed(ActionEvent e){
			serverContinue = false;
			int size = ConnectionArray.size();
			for(int i=0;i<size;i++){
				try {
					text.append("Shutting down "+CurrentUsers.get(0)+"\n");
					
					Socket tempS = ConnectionArray.get(0);
					PrintWriter tempOut = new PrintWriter(tempS.getOutputStream());
					tempOut.println("SERVERCMD: SHUT DOWN");	//message is received by ClientThread which sends it back to ServerThread
					tempOut.flush();
					CurrentUsers.remove(0);
					ConnectionArray.remove(0);
					updateUserDisplay();
					
				} catch (IOException e1) {
					text.append("Error: Could not close "+CurrentUsers.get(0)+"\n");
				}				
				
			}
			serverGUI.dispose();
		}
	}
	public void addUserName(Socket s){
		//as soon as the socket is accepted, ClientThreadGUI outputs the username in the Connect() method
		try{
			Scanner input = new Scanner(s.getInputStream());
			String userName = input.nextLine();
			user= userName;
			CurrentUsers.add(userName); //add new user to list of users
			updateUserDisplay();			
		}catch(Exception ex){
			text.append("Error: addUserName - "+ex);
		}			
	}
//----------------------------------------------------------------------------	
	public void updateUserDisplay(){
		//updating list of users visible to all other users
		for(int i=0; i<ConnectionArray.size(); i++)
		{
			Socket tempS = ConnectionArray.get(i);	
			PrintWriter output;
			try {
				output = new PrintWriter(tempS.getOutputStream());
				output.println("SERVERCMD: User list = "+ CurrentUsers);	//this is a command to add users to the list rather than echo this out to the chat
				output.flush();
				//this output is caught by the ClientThread class in the readInput() method
			} catch (IOException e) { text.append("Error: "+e+"\n");}
		}
	}

}
