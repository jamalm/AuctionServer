package com.deadmadness.auction;

public class ItemHandler extends Thread {
	private AuctionItem item;
	
	public ItemHandler(AuctionItem item){
		this.item = item;
	}
	
	public void run(){ 
		do {
			
			Thread.yield();
		}while(true);
	}
}
