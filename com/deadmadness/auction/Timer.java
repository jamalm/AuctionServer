package com.deadmadness.auction;


/*************************************
 * 
 * @author Jamal Mahmoud
 * @version 0.1
 * 
 ************************************/


public class Timer extends Thread{
	
	private AuctionServer server = null;
	private int pause;
	private int timeLeft;
	
	public Timer(AuctionServer server){
		this.server = server;
		pause = 10000;	//5 second pause between refreshes
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
			
			timeLeft -= 10;
			
			if(timeLeft == 0){
				if(server.getBid() != "10" && server.getBid() != "1000000"){
					server.unicast(server.winner, "You won!");
				}
				server.updateAuction();
			}
		}
	}
}
