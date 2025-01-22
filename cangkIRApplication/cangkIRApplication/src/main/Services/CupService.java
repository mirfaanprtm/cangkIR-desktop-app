package main.Services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import main.Db.DatabaseConn;

public class CupService {
	
	private static String incrementID() {
    	Connection connection = DatabaseConn.connection();
    	String totalData = "SELECT COUNT(*) from mscup";
    	try {
    		PreparedStatement preparedStatement = connection.prepareStatement(totalData);
    		
    		ResultSet rs = preparedStatement.executeQuery();
    		if(rs.next()) {
    			return "CU0" + (rs.getInt(1) + 1) ;
    		}
    	} catch (SQLException e) {
    		e.getMessage();
    	}
		return null;
	}
	
	public static void addCup(String cupId, String cupName, int cupPrice) {
        Connection connection = DatabaseConn.connection(); 
        String query = "INSERT INTO mscup (CupID, CupName, CupPrice) VALUES (?,?,?)";
        try {
        	PreparedStatement preparedStatement = connection.prepareStatement(query);
        	preparedStatement.setString(1, incrementID());
        	preparedStatement.setString(2, cupName);
            preparedStatement.setInt(3, cupPrice);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }       
    }

    public static void updateCupPrice(String cupId, String cupName, int cupPrice) {
    	Connection connection = DatabaseConn.connection(); 
        String query = "UPDATE mscup SET CupPrice = ? WHERE CupID = ?";
        try {
        	PreparedStatement preparedStatement = connection.prepareStatement(query);
        	preparedStatement.setString(1, incrementID());
        	preparedStatement.setString(2, cupName);
            preparedStatement.setInt(3, cupPrice);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }  
    	
//        try (Connection connection = DatabaseConn.connection()) {
//            String query = "UPDATE cups SET cup_price = ? WHERE cup_name = ?";
//            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//                preparedStatement.setInt(1, newPrice);
//                preparedStatement.setString(2, cupName);
//                preparedStatement.executeUpdate();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
