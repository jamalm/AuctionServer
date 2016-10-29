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
		pause = 5000;	//5 second pause between refreshes
		timeLeft = 60;
		
		start();
	}
	
	public void run(){
		while(timeLeft > 0){
			server.broadcast("Time", Integer.toString(timeLeft), true);
			
			try{
				Thread.sleep(pause);
			} catch (InterruptedException e){
				//if interrupted, break from loop
				break;
			}
			
			timeLeft -= 5;
			
			if(timeLeft == 0){
				server.updateAuction();
			}
		}
	}
}
