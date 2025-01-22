package main.Repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.Db.DatabaseConn;
import main.Models.Courier;

public class CourierRepo implements ICourierRepo {

	@Override
	public List<Courier> getAll() throws Exception {
		List<Courier> couriers = new ArrayList<>();
	    Connection connection = DatabaseConn.connection();

	    String query = "SELECT * FROM mscourier";
	    try {
	        PreparedStatement preparedStatement = connection.prepareStatement(query);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            Courier courier = new Courier();
	            courier.setCourierName(resultSet.getString("CourierName"));
	            courier.setCourierPrice(resultSet.getInt("CourierPrice"));

	            couriers.add(courier);
	        }
	    } catch (SQLException e) {
	        e.getMessage();
	    }

	    return couriers;
	}
	
	public String findCourierIdByCourierName (String courierName) {
    	Connection connection = DatabaseConn.connection();
    	String query = "SELECT CourierID from mscourier WHERE CourierName = ?";
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, courierName);
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
	
}
