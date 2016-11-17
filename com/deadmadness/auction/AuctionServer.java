package com.deadmadness.auction;

import java.util.ArrayList;
import java.io.IOException;
//import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/*************************************
 * 
 * @author Jamal Mahmoud - C13730921
 * @version 1.0
 * 
 * Deals with setting up connections and creates threads for users
 * also handles the auction state (start/stop) and allows the server user to set up items
 * 
 ************************************/

public class AuctionServer implements Runnable{
	//TODO Notify new bid placed
	//TODO Notify client of closed items
	//TODO Extra function
	
	//list of clients for thread
	private ServerThread clients[] = new ServerThread[50];
	private ServerSocket serverSocket = null;	//socket for server
	private Thread thread = null;	
	private int clientCount = 0;	//number of clients connected
	
	private int bid = 10;
	public int winner = 0;
	private AuctionItem items;
	private ArrayList<String> list = new ArrayList<String>();
	private Scanner keyboard;
	private Timer timer = null;
	private boolean state = false; //state of auction
	
	
	//constructor for server
	public AuctionServer(int port){
		try{
			//binding to physical port
			System.out.println("Binding to port..");
			serverSocket = new ServerSocket(port);
			
			//notification of connection
			System.out.println("Server up!");
			System.out.println("Address: " + serverSocket.getInetAddress());
			
			//starts the server thread
			startThread();
			startAuction();
		} catch (IOException e) {
			System.out.println("Bind failed to port " + port);
			e.printStackTrace();
		}
	}
	
	//TODO Notify Client of Item currently on sale
	public String itemOnSale() {
		return items.getItemAtIndex(items.getNum()-1);
	}
	
	public boolean hasStarted(){
		return state;
	}
	
	//TODO Notify Client of Highest bid for item
	public String getBid(){ return Integer.toString(bid); }
	
	public boolean setBid(String input, boolean newRound){
		
		int bid = Integer.parseInt(input);
		
		if(bid > this.bid && newRound == false){
			this.bid = bid; 
			return true;
		} else if(newRound){
			this.bid = bid;
			return true;
		} else {
			return false;
		}
	}
	
	//TODO Specify bid period
	
	//TODO Notify time remaining
	
	@Override
	public void run() {
		while(thread != null){
			try {
				//listen on port for incoming connections and add them to the threads list
				addThread(serverSocket.accept());
				
				//give other threads a chance to search
				Thread.yield();
			} catch (IOException e){
				e.printStackTrace();
				stopThread();
			}
		}
	}
	
	
	//checks if there is a thread and if not, create a new instance
	public void startThread() {
		if(thread == null) {
			thread = new Thread(this);
			thread.start();	//start thread
		}
	}
	
	public void stopThread() {
		thread = null;
	}
	
	
	
	
	private int getClient(int ID) {
		for(int i=0; i<clientCount; i++){
			if(clients[i].getID() == ID){
				return i;
			}
		}
		return -1;
	}
	
	public synchronized void unicast(int ID, String input) {
		
		for(int i=0;i<clientCount; i++){
			if(clients[i].getID() == ID){
				clients[i].send(input);
			}
		}
		notifyAll();
	}
	
	
	
	//used for broadcasting information to clients
	public synchronized void broadcast(String input){
		
		for(int i=0;i<clientCount; i++){
			clients[i].send(input);
		}

		notifyAll();
	}
	
	
	public synchronized void remove(int ID){
		int pos = getClient(ID);
		if(pos >= 0) {
			ServerThread toTerminate = clients[pos];
			System.out.println("Removing Client Thread: " + ID + " at " + pos);
			
			if(pos < clientCount-1) {
				for(int i = pos+1; i < clientCount; i++) {
					clients[i-1] = clients[i];
				}
			}
			clientCount--;
			
			try {
				toTerminate.close();
			} catch (IOException e) {
				System.out.println("Error closing thread: " + e);
			}
			toTerminate = null;
			System.out.println("Client " + pos + "removed");
			notifyAll();	//wakes up threads that are waiting on this one
		}
	}

	private void addThread(Socket socket) {
		System.out.println("Incoming Client...");
		if(clientCount < clients.length) {
			System.out.println("New Client connected: " + socket);
			clients[clientCount] = new ServerThread(this, socket);
			
			try {
				clients[clientCount].open();
				clients[clientCount].start();
				clientCount++;
				if(!state){
					unicast(clients[clientCount-1].getID(),"Stopped");
					unicast(clients[clientCount-1].getID(),"Welcome to the Auction! Please wait while the items are being added.");
				} else {
					unicast(clients[clientCount-1].getID(),"Started");
				}
			} catch(IOException e){
				System.out.println("Error opening thread!");
				e.printStackTrace();
			}
		} else {
			System.out.println("MAX client capacity reached(" + clients.length + ")\n");
		}
	}
	
	private ArrayList<String> addItems(){
		keyboard = new Scanner(System.in);
		String text;
		System.out.println("Enter your list of items(add 'END' to complete list): ");
		for(;;){
			text = keyboard.nextLine();
			if(!text.equals("END")){
				list.add(text);
				System.out.println("Added " + text);
			} else {
				break;
			}
		}
		return list;
	}
	
	//sets up the initial Auction
	public void startAuction(){
		winner = 0;
		items = new AuctionItem(addItems());
		state = true;
		broadcast("Started");
		broadcast("Welcome to The Auction!");
		timer = new Timer(this);
	}
	//Updates auction with new item
	public void updateAuction(){
		if (items.nextItem(winner)){
			if(items.getNum()!=0){
				winner = 0;
				setBid(Integer.toString(10), true);
				broadcast("\nNew Item for sale!");
				broadcast("Current Item: " + itemOnSale());
				broadcast("Highest Bid: " + getBid());
				timer = new Timer(this);
			} else {
				timer = null;
				setBid(Integer.toString(1000000), true);
				broadcast("\nBid is complete!\n");
				broadcast("Stopped");
				state = false;
				System.out.println("Would you like to start another bid? (y/N)");
				if(new Scanner(System.in).nextLine().equals("y")){
					startAuction();
				} else {
					System.exit(0);
				}
			}
		}
	}
	
	public void resetTime(){
		if(timer != null){
			timer.interrupt();
			timer = null;
			timer = new Timer(this);
		}
		
	}
	
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Usage: java AuctionServer port");
		} else {
			new AuctionServer(Integer.parseInt(args[0]));
		}
	}
}