package com.deadmadness.auction;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread{
	
	private Socket socket = null;
	private AuctionClient client = null;
	private DataInputStream input = null;

	public ClientThread(AuctionClient auctionClient, Socket socket) {
		client = auctionClient;
		this.socket = socket;
		open();
		start();
	}

	public void open() {
		try {
			input = new DataInputStream(socket.getInputStream());
		} catch (IOException e){
			System.out.println("Error retrieving input stream");
			client.stop();
			e.printStackTrace();
		}
	}
	public void close() {
		try {
			if(input != null){
				input.close();
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		while(true && client != null){
			try {
				client.handle(input.readUTF());
			} catch(IOException e){
				client = null;
				e.printStackTrace();
			}
		}
	}

}
