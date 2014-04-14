package myChatRoom;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

public class ServerThread extends Thread{
	
	//globals
	Socket clientSocket;
	private Scanner input;
	private PrintWriter output;
	String msg = "";
	public JFrame GUI;
	public JTextArea text;
	Server server;
	protected boolean ServerThreadContinue = true;
	public ServerThread(Socket s, Server sv) throws IOException{
		clientSocket = s;
		server = sv;
		
	}
	public void run(){
		buildThisGUI();
		try {
			input = new Scanner(clientSocket.getInputStream());
			output = new PrintWriter(clientSocket.getOutputStream());
			while(ServerThreadContinue){	
				text.append("Waiting on input.\n");
				if(input.hasNextLine()){					
					String msg = input.nextLine();
					text.append("Client said: "+msg+"\n");
					if (msg.contains("SERVERCMD: ") && msg.contains("disconnected.")) 
					{
						disconnectUser();
						msg = msg.substring(11);
						displayToAll(msg);
					}
					else if(msg.contains("SERVERCMD: SHUT DOWN")) 
					{
						//JOptionPane.showMessageDialog(null, "Received by ServerThread.");
						disconnectAllUsers();					
					}else if(msg.contains("SERVERCMD PRIVATE MESSAGE: ")) 
					{
						msg = msg.substring(27);
						String[] temp = msg.split(": ");
						//temp[0] contains list of chatters
						//temp[1] contains message to display
						temp[0] = temp[0].replace("[","");
						temp[0] = temp[0].replace("]","");
						String[] chatters = temp[0].split(", ");
						text.append("msg: "+msg+"\n");
						text.append("temp[0]: "+temp[0]+"\n");
						text.append("temp[1]: "+temp[1]+"\n");
						text.append("chatters: "+chatters+"\n");
						displayToSome(temp[1],chatters);
						
					}else{

						displayToAll(msg);
					}
					
				}                
			}
			text.append("ServerThreadContinue==false");
			//this is only reached when ServerThreadContinue==false from disconnectUser and disconnectAllUsers 
			try {
				text.append("CLIENT DISCONNECTED!\nSELF DESTRUCTING IN:");
				text.append("\n5");	sleep(1000);
				text.append("\n4");	sleep(1000);
				text.append("\n3");	sleep(1000);
				text.append("\n2");	sleep(1000);
				text.append("\n1");	sleep(1000);
			} catch (InterruptedException e) {	System.out.println("ServerThread run inner try disconnect error: "+e);}
			
			GUI.dispose();
			clientSocket.close();
			
		} catch (IOException e) {
			text.append("ServerThread run outer try error: "+e);
		}
		
	}
	//removes a single user from the ConnectionArray and the CurrentUsers array
	public void disconnectUser(){
		text.append("disconnecting...\n");
		server.CurrentUsers.remove(server.ConnectionArray.indexOf(clientSocket));
		server.ConnectionArray.remove(clientSocket);
		server.updateUserDisplay();
		ServerThreadContinue = false; 
	}
	//does not remove all users directly. This is handled by the Server class.
	//purpose of this method is to simply set ServerThreadContinue = false to
	//break out of the loop. 
	public void disconnectAllUsers(){
		text.append("disconnecting...\n");					
		ServerThreadContinue = false; 
	}
	public void displayToAll(String msg) throws IOException{
		//NOW DISPLAY MESSAGE TO ALL CLIENTS
		for(int i=0;i<server.ConnectionArray.size();i++)
		{
			text.append("Displaying message to: "+server.CurrentUsers.get(i)+"...");
			Socket tempS = server.ConnectionArray.get(i);
			PrintWriter tempOut = new PrintWriter(tempS.getOutputStream());
			tempOut.println(msg);
			tempOut.flush();
			text.append("Done\n");
			
		}
	}
	
	public void displayToSome(String str, String[] chatters) throws IOException{
		//str is the message
		//chatters is the list of chatters
		
		//change String[] chatters to ArrayList<String> without the last name (the sender)
		ArrayList<String> names = new ArrayList<String>();
		for(int i=0;i<chatters.length-1;i++){ names.add(chatters[i]); }
		
		//create string of recipient names
		String sNames = "";
		if(names.size()==1){
			sNames = names.get(0);
		}else{
			for(int i=0;i<names.size()-1;i++){
				sNames = sNames + names.get(i)+", ";
			}	
			sNames = sNames + "and " + names.get(names.size()-1);		
		}
		
		Socket senderSocket;
		PrintWriter senderOut;
		//loop through and find the sender socket
		for(int i=0;i<server.CurrentUsers.size();i++)
		{
			if(server.CurrentUsers.get(i).equals(chatters[chatters.length-1])){
				
				senderSocket = server.ConnectionArray.get(i);
				senderOut = new PrintWriter(senderSocket.getOutputStream());
				senderOut.println("YOU SENT THIS PRIVATE MESSAGE TO "+sNames+": "+str);
				senderOut.flush();
				text.append("Done\n");
			}
		}
		for(int i=0;i<server.CurrentUsers.size();i++)
		{
			for(int j=0;j<chatters.length-1;j++){				
				if(server.CurrentUsers.get(i).equals(chatters[j])){
					text.append("Displaying message to: "+chatters[j]+"...");
					Socket tempS = server.ConnectionArray.get(i);
					PrintWriter tempOut = new PrintWriter(tempS.getOutputStream());
					tempOut.println("PRIVATE MESSAGE FROM "+chatters[chatters.length-1]+" TO "+sNames+": "+str);
					tempOut.flush();
					text.append("Done\n");
				}				
			}		
		}
	}
	public void buildThisGUI(){
		server.x = server.x+40;
		server.y = server.y+40;
		String user = server.CurrentUsers.get(server.ConnectionArray.indexOf(clientSocket));
		GUI = new JFrame();
		GUI.setTitle(user+"'s Server Thread");
		GUI.setLocation(server.x,server.y);
		GUI.setResizable(true);
		GUI.setBackground(new Color(255,255,255));
		GUI.setSize(300,300);
		GUI.setLayout(null);			
		
		text = new JTextArea();
		text.setColumns(30);
		text.setFont(new Font("Tahoma",0,12));
		text.setForeground(new Color(0,0,255));
		text.setLineWrap(true);
		text.setRows(5);
		text.setEditable(false);
		
		JScrollPane conversationScrollPane = new JScrollPane();
		conversationScrollPane.setHorizontalScrollBarPolicy(	ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		conversationScrollPane.setVerticalScrollBarPolicy(		ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		conversationScrollPane.setViewportView(text);
		GUI.getContentPane().add(conversationScrollPane);
		conversationScrollPane.setBounds(10,10,265,245);
		
		GUI.setVisible(true);
	}
}
