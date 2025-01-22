package main.Repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.Db.DatabaseConn;
import main.Models.TransactionDetail;
import main.Models.TransactionHeader;

public class TransactionRepo {
	
	public String lastID() {
    	Connection connection = DatabaseConn.connection();
    	String query = "SELECT MAX(TransactionID) from transactionheader";
    	try {
    		PreparedStatement preparedStatement = connection.prepareStatement(query);
    		
    		ResultSet rs = preparedStatement.executeQuery();
    		
    		if(rs.next()) {
    			String newID = rs.getString(1);
    			int count = Integer.parseInt(newID.substring(2)) + 1;
                newID = "TR" + String.format("%03d", count);
                return newID;
    		}
    	} catch (SQLException e) {
    		e.getMessage();
    	}
		return null;
	}
	
	public TransactionHeader create(TransactionHeader th) throws Exception {
		Connection connection = DatabaseConn.connection();
	    String query = "INSERT INTO transactionheader (TransactionID, UserID, CourierID, TransactionDate, UseDeliveryInsurance) VALUES (?,?,?,?,?)";
	    try {
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, lastID());
	        preparedStatement.setString(2, th.getUserID());
	        preparedStatement.setString(3, th.getCourierID());
	        preparedStatement.setDate(4, th.getTransactionDate());
	        preparedStatement.setInt(5, th.getUseDeliveryInsurance());
	        preparedStatement.executeUpdate();
	        return th;
	    } catch (SQLException e) {
	        throw new Exception("Failed add transaction header : " + e.getMessage());
	    }
	}
	
	public TransactionDetail create(TransactionDetail td) throws Exception {
		Connection connection = DatabaseConn.connection();
	    String query = "INSERT INTO transactiondetail (TransactionID, CupID, Quantity) VALUES (?,?,?)";
	    try {
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, td.getTransactionID());
	        preparedStatement.setString(2, td.getCupID());
	        preparedStatement.setInt(3, td.getQuantity());
	        preparedStatement.executeUpdate();
	        return td;
	    } catch (SQLException e) {
	        throw new Exception("Failed add transaction detail : " + e.getMessage());
	    }
	}
}
