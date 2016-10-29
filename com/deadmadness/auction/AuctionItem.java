package com.deadmadness.auction;

import java.util.ArrayList;

/*************************************
 * 
 * @author Jamal Mahmoud
 * @version 0.1
 * 
 ************************************/

public class AuctionItem {
	private ArrayList<String> items = new ArrayList<String>();
	private int numItems;
	private final int MIN = 5;
	
	public AuctionItem(ArrayList<String> items) {
		if(items.size() < MIN){
			System.out.println("Not enough items for auction!");
			System.exit(1);
		} else {
			numItems = items.size();
			this.items = items;
		}
		
	}
	
	public int getNum() {
		return numItems;
	}
	public void itemSold(){
		if(numItems !=0){
			numItems--;
		}
	}
	
	public String getItemAtIndex(int i) {
		return items.get(i);
	}
	
	public synchronized int removeItem() {
		numItems--;
		notify();
		
		return numItems;
	}
}