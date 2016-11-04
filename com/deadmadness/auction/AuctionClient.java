package com.deadmadness.auction;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/*************************************
 * 
 * @author Jamal Mahmoud
 * @version 0.1
 * 
 ************************************/


public class AuctionClient implements Runnable{
	private Socket socket = null;
	private Thread thread = null;
	private BufferedReader input = null;
	private DataOutputStream output = null;
	private ClientThread client = null;
	
	public AuctionClient(String serverName, int serverPort) {
		System.out.println("Connecting to server..");
		
		try {
			socket = new Socket(serverName ,serverPort);
			System.out.println("Connected!\n port: " + socket);
			start();
		} catch (UnknownHostException e) {
			System.out.println("unknown host..");
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (thread != null) {
			try {
				String message = input.readLine();
				/*if(isValid(message)){
					output.writeUTF(message);
					output.flush();
				} else {
					System.out.println("\n USAGE: ");
				}*/
				output.writeUTF(message);
				output.flush();
				
			} catch(IOException e){
				System.out.println("Sending Error");
				stop();
			}
		}
		
	}
	
	public void start() throws IOException{
		input = new BufferedReader(new InputStreamReader(System.in));
		output = new DataOutputStream(socket.getOutputStream());
		
		if(thread == null){
			client = new ClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
		
	}
	
	public void stop() {
		try {
			if(input != null){
				input.close();
			}
			if(output != null){
				output.close();
			}
			if(socket != null){
				socket.close();
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		
		client.close();
		thread = null;
	}
	/*
	private boolean isValid(String message){
		for(int i=0;i<message.length();i++){
			if(!Character.isDigit(message.charAt(i))){
				return false;
			}
		}
		return true;
	}*/
	
	public void handle(String message) throws IOException {
		if(message.equals("QUIT")) {
			System.out.println("Thank you for visiting! Press Enter to Exit..");
			stop();
		} else {
			System.out.println(message);
		}
	}
	
	public static void main(String args[]){
		AuctionClient client = null;
		
		if(args.length !=2){
			System.out.println("Usage: Java AuctionClient host port");
		} else {
			client = new AuctionClient(args[0], Integer.parseInt(args[1]));
		}
	}
}
