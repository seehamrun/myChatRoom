package myChatRoom;


import java.net.*;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;

public class ClientThread extends Thread{

	//globals
	Socket socket;
	Scanner input;
	Scanner send = new Scanner(System.in);
	PrintWriter output;
	ClientThreadGUI gui;
	Server server;
	boolean keepReading;
//-----------------------------------------------------------------------------------------------
	public ClientThread (Socket s, ClientThreadGUI g)
	{
		this.socket = s;
		this.gui = g;
			
	}
//-----------------------------------------------------------------------------------------------
	public void run()
	{
		keepReading=true;
		try
		{
			input = new Scanner(socket.getInputStream());
			output = new PrintWriter(socket.getOutputStream());
			output.flush();
			while(keepReading){
				readInput();	
			}
					
		}
		catch(Exception e) { System.out.println("ClientThread run try error: "+e); }
	}
//-----------------------------------------------------------------------------------------------
	public void readInput() throws IOException
	{				
		if(input.hasNext())
		{
			String msg = input.nextLine();
			
			
			if(msg.contains("SERVERCMD: SHUT DOWN"))
			{
				//if you run this JOptionPane and the corresponding JOptionPane in ServerThread, 
				//you'll see that it is first received by the client thread and then sent to the server thread by the DISCONNECT method
			//	JOptionPane.showMessageDialog(null, "Received by ClientThreadfad.");	
				disconnect(2);		//this integer 2 indicates to the method not to close the socket - this is handled by the Server class	
			}
			
			//this is where the GUI's JList of online members gets updated
			else if(msg.contains("SERVERCMD: User list = "))
			{
				String 	temp1 = msg.substring(23);
						temp1 = temp1.replace("[","");
						temp1 = temp1.replace("]","");
						
				String[] CurrentUsers = temp1.split(", ");
				
				gui.onlineJL.setListData(CurrentUsers);	
				
			}
			else
			{
				gui.conversationTxtAr.append(msg+"\n");
			}
		}
	}
//-----------------------------------------------------------------------------------------------	
	public void disconnect(int source) throws IOException
	{
		gui.frame.dispose();
		new Thread();
		if(source == 2){
			output.println("SERVERCMD: SHUT DOWN");
			output.flush();	
			JOptionPane.showMessageDialog(null, "The server has been shut down. You have been disconnected!");
			
		}else{
			/*
			 * this output removes the socket from the Server's ConnectinArray, removes the name 
			 * of the user from the Server's CurrentUsers array, updates the list of logged on 
			 * users and then discontinues the server thread.
			 */
			output.println("SERVERCMD: "+gui.userName+": disconnected.");
			output.flush();	
			
			JOptionPane.showMessageDialog(null, "You disconnected!");			
			socket.close();
		}
		keepReading = false;
		output.close();
		input.close();
		
	}

	
//-----------------------------------------------------------------------------------------------
	public void send(String str)
	{
		output.println(gui.userName+": "+str);
		output.flush();	//message caught by ServerThread
		gui.msgTxtFld.setText("");
	}
	public void sendPrivate(List<String> chatters, String msg){
		output.println("SERVERCMD PRIVATE MESSAGE: "+chatters+": "+msg);//caught by ServerThread
		output.flush();
	}
}

