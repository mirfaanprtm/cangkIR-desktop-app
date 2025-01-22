package main.Models;

public class Courier {
	private String CourierID;
	private String CourierName;
	private int CourierPrice;
	
	public String getCourierID() {
		return CourierID;
	}
	public void setCourierID(String courierID) {
		CourierID = courierID;
	}
	public String getCourierName() {
		return CourierName;
	}
	public void setCourierName(String courierName) {
		CourierName = courierName;
	}
	public int getCourierPrice() {
		return CourierPrice;
	}
	public void setCourierPrice(int courierPrice) {
		CourierPrice = courierPrice;
	}
	@Override
	public String toString() {
		return CourierName;
	}
	
}
