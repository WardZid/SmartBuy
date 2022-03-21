package model;

import java.util.ArrayList;

public class Order {
	//attributes
	private int orderId;
	private int totalTime;
	private int waitTime;
	private String status;
	private ArrayList<OrderItem> myList;
	private double priority;
	//Constructors
	public Order(int orderId, int totalTime, int waitTime,String status) {
		this.orderId = orderId;
		this.totalTime = totalTime;
		this.waitTime = waitTime;
		this.status=status;
	}
	//getters and Setters
	public int getOrderId() {return orderId;}
	public void setOrderId(int orderId) {this.orderId = orderId;}
	public int getTotalTime() {return totalTime;}
	public void setTotalTime(int totalTime) {this.totalTime = totalTime;}
	public int getWaitTime() {return waitTime;}
	public void setWaitTime(int waitTime) {	this.waitTime = waitTime;}
	public ArrayList<OrderItem> getMyList() {return myList;}
	public void setMyList(ArrayList<OrderItem> myList) {this.myList = myList;}
	public double getPriority() {return priority;}
	public void setPriority(double priority) {this.priority = priority;}
	//Methods
	public boolean isWaiting() {
		//method to know if the order is ready and waiting to be started
		if(status.equals("WAITING")) return true;
		return false;
	}
	public boolean isInProgress() {
		//method to know if the order is being worked on
		if(status.equals("IN_PROGRESS")) return true;
		return false;
	}
	@Override
	public String toString() {
		//toString to present the order's data to the ShiftManager
		return "\n [Order ID= " + orderId + "] | [Minimum Prep Time=" + totalTime + "] | [Waiting For=" + ((float)waitTime/60) + "] | [Priority Level=" + priority +"]\nProducts: \n"+ myList+"\n";
	}
}
