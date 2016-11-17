package com.deadmadness.auction;

import java.util.ArrayList;

/*************************************
 * 
 * @author Jamal Mahmoud - C13730921
 * @version 1.0
 * 
 * Deals with the items in the auction, removes sold items and loops unsold ones.
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
	private void setNum(){
		numItems = items.size();
	}
	public boolean nextItem(int win){
		//System.out.println("NUMITEMS TEST: " + numItems);
		
		if(win!=0){
			if(numItems > 0){
				removeItem(numItems-1);
				setNum();
				//System.out.println("NUMITEMS TEST2: " + numItems);
				return true;
			} else {
				return false;
			}
		} else {
			if(numItems > 0){
				swap(items.get(numItems-1));
				//System.out.println("NUMITEMS TEST push item to end");
				
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	public String getItemAtIndex(int i) {
			return items.get(i);
	}
	
	private synchronized void removeItem(int index) {
		items.remove(index);
		notify();
		
	}
	//swaps elements in the arraylist to loop unsold items
	private void swap(String item){
		String temp;
		temp = item;
		for(int i=1;i<numItems;i++){
			items.set(numItems-i, items.get(numItems-(i+1)));
		}
		items.set(0, temp);
	}
}