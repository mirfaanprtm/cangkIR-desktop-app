package main.Models;

public class Cart {
	
	private String UserID;
	private String CupID;
	private String CupName;
	private int CupPrice;
	private int Quantity;
	private int Total;
	private Courier courier;
	
	
	
	public Cart(String userID, String cupID, int quantity) {
		super();
		UserID = userID;
		CupID = cupID;
		Quantity = quantity;
	}

	public Cart() {
		// TODO Auto-generated constructor stub
	}
	
	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getCupID() {
		return CupID;
	}
	public void setCupID(String cupID) {
		CupID = cupID;
	}
	public Courier getCourier() {
		return courier;
	}
	public void setCourier(Courier courier) {
		this.courier = courier;
	}
	public String getCupName() {
		return CupName;
	}
	public void setCupName(String cupName) {
		CupName = cupName;
	}
	public int getCupPrice() {
		return CupPrice;
	}
	public void setCupPrice(int cupPrice) {
		CupPrice = cupPrice;
	}
	public int getQuantity() {
		return Quantity;
	}
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	
	public int getTotal() {
		return Total;
	}
	public void setTotal(int total) {
		Total = total;
	}
	@Override
	public String toString() {
		return "Cart [CupName=" + CupName + ", CupPrice=" + CupPrice + ", Quantity=" + Quantity + ", Total=" + Total
				+ "]";
	}
	
}
