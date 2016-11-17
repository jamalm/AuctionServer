package com.deadmadness.auction;


/*************************************
 * 
 * @author Jamal Mahmoud - C13730921
 * @version 1.0
 * 
 * Creates a timer for the server to timeout auction items
 *  
 ************************************/


public class Timer extends Thread{
	
	private AuctionServer server = null;
	private int pause;
	private int timeLeft;
	
	public Timer(AuctionServer server){
		this.server = server;
		pause = 1000;	//5 second pause between refreshes
		timeLeft = 60;
		
		start();
	}
	
	public void run(){
		while(timeLeft > 0){
			server.broadcast("Time Remaining: " + Integer.toString(timeLeft));
			
			try{
				Thread.sleep(pause);
			} catch (InterruptedException e){
				//if interrupted, break from loop
				break;
			}
			
			timeLeft -= 60;
			
			if(timeLeft == 0){
				if(server.winner != 0){
					server.broadcast(server.winner + " won " + server.itemOnSale() +"!\n");
					server.unicast(server.winner, "YOU HAVE WON A LOVELY " + server.itemOnSale()+"!\n");
				}
				server.updateAuction();
			}
		}
	}
}
