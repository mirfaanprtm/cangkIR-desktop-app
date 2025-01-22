package main.Repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import main.Db.DatabaseConn;
import main.Models.Cup;
import main.Models.User;

public class CupRepo implements ICupRepos {
	
	private String incrementID() {
    	Connection connection = DatabaseConn.connection();
    	String totalData = "SELECT COUNT(*) from mscup";
    	try {
    		PreparedStatement preparedStatement = connection.prepareStatement(totalData);
    		
    		ResultSet rs = preparedStatement.executeQuery();
    		
    		if(rs.next()) {
    			
    			int count = rs.getInt(1);
                String newID = "CU" + String.format("%03d", count + 1);
                try {
                	Cup cup = findById(newID);
				} catch (Exception e) {
					e.printStackTrace();
				}
                return newID;
    		}
    	} catch (SQLException e) {
    		e.getMessage();
    	}
		return null;
	}
	
	private String lastID() {
    	Connection connection = DatabaseConn.connection();
    	String query = "SELECT MAX(CupID) from mscup";
    	try {
    		PreparedStatement preparedStatement = connection.prepareStatement(query);
    		
    		ResultSet rs = preparedStatement.executeQuery();
    		
    		if(rs.next()) {
    			String newID = rs.getString(1);
    			int count = Integer.parseInt(newID.substring(2)) + 1;
                newID = "CU" + String.format("%03d", count);
                return newID;
    		}
    	} catch (SQLException e) {
    		e.getMessage();
    	}
		return null;
	}
	
	public String findCupIdByCupName (String cupname) {
    	Connection connection = DatabaseConn.connection();
    	String query = "SELECT CupID from mscup WHERE CupName = ?";
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, cupname);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				String rows = rs.getString(1);
				return rows;
			}
		} catch (SQLException e) {
			e.getMessage();
		}
    	return null;
    }


	@Override
	public Cup create(Cup cup) throws Exception {
		Connection connection = DatabaseConn.connection();
	    String query = "INSERT INTO mscup (CupID, CupName, CupPrice) VALUES (?,?,?)";
	    try {
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, lastID());
	        preparedStatement.setString(2, cup.getCupName());
	        preparedStatement.setInt(3, cup.getCupPrice());

	        preparedStatement.executeUpdate();
	        return cup;
	    } catch (SQLException e) {
	        throw new Exception("Failed add cup : " + e.getMessage());
	    }
	}

	@Override
	public List<Cup> getAll() throws Exception {
		List<Cup> cups = new ArrayList<>();
	    Connection connection = DatabaseConn.connection();

	    String query = "SELECT * FROM mscup";
	    try {
	        PreparedStatement preparedStatement = connection.prepareStatement(query);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            Cup cup = new Cup();
	            cup.setCupName(resultSet.getString("CupName"));
	            cup.setCupPrice(resultSet.getInt("CupPrice"));

	            cups.add(cup);
	        }
	    } catch (SQLException e) {
	        e.getMessage();
	    }

	    return cups;
	}

	@Override
	public Cup update(Cup cup) throws Exception {
	    Connection connection = DatabaseConn.connection();
	    String query = "UPDATE mscup SET CupPrice = ? WHERE CupName = ?";

	    try {
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, cup.getCupPrice());
	        preparedStatement.setString(2, cup.getCupName());

	        int rowsAffected = preparedStatement.executeUpdate();
	        if (rowsAffected > 0) {
	            return cup;
	        } else {
	            throw new Exception("Failed to update cup");
	        }
	    } catch (SQLException e) {
	        throw new Exception("Failed to update cup : " + e.getMessage());
	    }
	}


	@Override
	public void delete(String cupName) throws Exception {
	    Connection connection = DatabaseConn.connection();
	    String query = "DELETE FROM mscup WHERE CupName = ?";

	    try {
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, cupName);
	        int rowsAffected = preparedStatement.executeUpdate();
	        return;
	    } catch (SQLException e) {
	        throw new Exception("Failed to delete : " + e.getMessage());
	    }
	}

	@Override
	public List<Cup> findByName(String cupName) throws Exception {
		Connection connection = DatabaseConn.connection();
	    String query = "SELECT * FROM mscup WHERE CupName = ?";

	    try {
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, cupName);
	        preparedStatement.executeQuery();
	        
	    } catch (SQLException e) {
	        throw new Exception(e.getMessage());
	    }
		return null;
	}

	@Override
	public Cup findById(String cupId) throws Exception {
		Connection connection = DatabaseConn.connection();
	    String query = "SELECT * FROM mscup WHERE CupId = ?";
	    Cup cup = new Cup();
	    try  {
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, cupId);
	        ResultSet rs = preparedStatement.executeQuery();
	        if(rs.first()) {
	        	cup.setCupName(rs.getString(2));
	        	cup.setCupPrice(rs.getInt(3));
	        }
	       
	        return cup;
	        
	    } catch (SQLException e) {
	        return null;
	    }
	}
}
