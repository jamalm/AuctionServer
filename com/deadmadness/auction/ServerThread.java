package com.deadmadness.auction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/*************************************
 * 
 * @author Jamal Mahmoud
 * @version 0.1
 * 
 ************************************/

public class ServerThread extends Thread{
	private AuctionServer server = null;
	private Socket socket = null;
	private int ID = -1;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	private Thread thread;
	
	public ServerThread(AuctionServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		ID = socket.getPort();
	}
	
	public int getID(){
		return ID;
	}
	
	public void send(String message) {
		try {
			out.writeUTF(message);
			out.flush();
		} catch (IOException e){
			System.out.println("\nThe message \"" + message + "\" failed to send\n");
			e.printStackTrace();
			server.remove(ID);
			thread = null;
		}
	}
	
	public void run() {
		System.out.println("Server Thread " + ID + " running.");
		thread = new Thread(this);
		
		while(true) {
			try {
				server.broadcast("Current Item", server.itemOnSale(), true);
				server.broadcast("Highest Bid", server.getBid(), true);
				
				//read input
				String bid = in.readUTF();
				
				//if bid is higher..
				if(server.setBid(bid, false)){
					//set new highest bidder
					//server.broadcast("Highest Bid", server.getBid(),true);
					System.out.println(ID + "- Updated bid: " + bid + "\n");
					server.resetTime();
					
				} else {
					server.broadcast(Integer.toString(ID), "Invalid bid", false);
				}
				/*
				if(in.readUTF().equals("QUIT")){
					server.broadcast(Integer.toString(ID), in.readUTF(), false);
				}*/
				
				Thread.yield();
			} catch(IOException e){
				server.remove(ID);
				thread = null;
			} catch(NumberFormatException e){
				System.out.println("Not a number");
			}
		}
	}
	
	public void open() throws IOException {
		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}
	
	public void close() throws IOException {
		if(socket != null){
			socket.close();
		}
		if(in != null){
			in.close();
		}
		if(out != null){
			out.close();
		}
	}
}
