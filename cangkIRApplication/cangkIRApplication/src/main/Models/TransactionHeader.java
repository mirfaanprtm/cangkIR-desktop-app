package main.Models;


public class TransactionHeader {
	private String TransactionID;
	private String UserID;
	private String CourierID;
	private java.sql.Date TransactionDate;
	private int useDeliveryInsurance;
	public String getTransactionID() {
		return TransactionID;
	}
	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getCourierID() {
		return CourierID;
	}
	public void setCourierID(String courierID) {
		CourierID = courierID;
	}
	
	public java.sql.Date getTransactionDate() {
		return TransactionDate;
	}
	public void setTransactionDate(java.sql.Date transactionDate) {
		TransactionDate = transactionDate;
	}
	public int getUseDeliveryInsurance() {
		return useDeliveryInsurance;
	}
	public void setUseDeliveryInsurance(int useDeliveryInsurance) {
		this.useDeliveryInsurance = useDeliveryInsurance;
	}
	
	
}
