package main.Models;

public class Cup {
	
	private String CupID;
	private String CupName;
	private int CupPrice;
	
	public Cup(String cupName, int cupPrice) {
		super();
		CupName = cupName;
		CupPrice = cupPrice;
	}
	
	

	public String getCupID() {
		return CupID;
	}



	public void setCupID(String cupID) {
		CupID = cupID;
	}



	public Cup() {
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

	
}
