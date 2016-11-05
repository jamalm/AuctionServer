package com.deadmadness.auction;

import java.util.List;

public class UserProfile {
	private String username;
	private String password;
	private int balance;
	
	private List<String> itemsWon;
	
	public UserProfile(String user, String pass, int balance){
		username = user;
		password = pass;
		this.balance = balance;
	}
	
	public String getUser(){
		return username;
	}
	
	public boolean CheckPassword(String pass){
		if(pass.equals(password)){
			return true;
		} else {
			return false;
		}
	}
	
	public void setBalance(int balance){
		this.balance += balance;
	}
	public int getBalance(){
		return balance;
	}
}
