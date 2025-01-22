package main.Repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.Db.DatabaseConn;
import main.Models.Cart;
import main.Models.Courier;
import main.Models.Cup;
import main.View.LoginForm;

public class CartRepo implements ICartRepo {
	private LoginForm loginForm = new LoginForm();
	
	private String lastIDCup() {
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
	
	private static String lastIDUser() {
    	Connection connection = DatabaseConn.connection();
    	String query = "SELECT MAX(UserID) from msuser";
    	try {
    		PreparedStatement preparedStatement = connection.prepareStatement(query);
    		
    		ResultSet rs = preparedStatement.executeQuery();
    		
    		if(rs.next()) {
    			String newID = rs.getString(1);
    			int count = Integer.parseInt(newID.substring(2)) + 1;
                newID = "US" + String.format("%03d", count);
                return newID;
    		}
    	} catch (SQLException e) {
    		e.getMessage();
    	}
		return null;
	}

	@Override
	public Cart create(Cart cart) throws Exception {
		
		Connection connection = DatabaseConn.connection();
	    String query = "INSERT INTO cart (UserID, CupID, Quantity) VALUES (?,?,?)";
	    try {
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, cart.getUserID());
	        preparedStatement.setString(2, cart.getCupID());
	        preparedStatement.setInt(3, cart.getQuantity());
	        System.out.println("UserID Value: " + cart.getUserID());
	        System.out.println("CupID value: " + cart.getCupID());
	        System.out.println("Cart before insertion: " + cart);
	        preparedStatement.executeUpdate();
	        

	        return cart;
	    } catch (SQLException e) {
		        throw new Exception("Failed add cart : " + e.getMessage());
	    }
	}
	
    private Cart findCartItem(String cupId, String cupName) throws Exception {
        // Helper method to find a cart item in the list based on cup name
    	CartRepo cartRepo = new CartRepo();
    	List<Cart> cartItems = cartRepo.getAll();
        for (Cart cartItem : cartItems) {
            if (cartItem.getCupName().equals(cupName) && cartItem.getUserID().equals(cupId)) {
            	System.out.println("Cart : " + cartItem);
                return cartItem;
            }
        }
        return null;
    }
	
    public Cart createOrUpdate(Cart cart) throws Exception {
        Connection connection = DatabaseConn.connection();
        String query = "INSERT INTO cart (UserID, CupID, Quantity) VALUES (?,?,?) ON DUPLICATE KEY UPDATE Quantity = Quantity + VALUES(Quantity)";

        try {
        	PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cart.getUserID());
            preparedStatement.setString(2, cart.getCupID());
            preparedStatement.setInt(3, cart.getQuantity());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                return cart;
            } else {
                throw new Exception("Failed to create or update cart");
            }
        } catch (SQLException e) {
            throw new Exception("Failed to create or update cart: " + e.getMessage());
        }
    }


	@Override
	public Cart findById(String cartId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void updateQuantity(Cart cart) throws Exception {
	    Connection connection = DatabaseConn.connection();
	    String query = "UPDATE cart SET Quantity = ? WHERE CupID = ? AND UserID = ?";

	    try {
	        PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, cart.getQuantity());
	        preparedStatement.setString(2, cart.getCupID());
	        preparedStatement.setString(2, cart.getUserID());

	        int rowsAffected = preparedStatement.executeUpdate();
	        if (rowsAffected <= 0) {
	            throw new Exception("No rows updated. Quantity not changed.");
	        }
	    } catch (SQLException e) {
	        throw new Exception("Failed to update quantity: " + e.getMessage());
	    }
	}
	
	public void deleteByUserAndCup(String userId, String cupId) throws Exception {
        Connection connection = DatabaseConn.connection();

        String query = "DELETE FROM cart WHERE UserID = ? AND CupID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, cupId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Failed to delete cart: " + e.getMessage());
        }
    }


	@Override
	public List<Cart> getAll() throws Exception {
		List<Cart> carts = new ArrayList<>();
	    Connection connection = DatabaseConn.connection();

	    String query = "select msc.CupName, msc.CupPrice, c.Quantity, SUM(msc.CupPrice * c.Quantity) as Total from cart as c join mscup as msc on c.CupID = msc.CupID group by msc.CupName, msc.CupPrice, c.Quantity";
	    try {
	        PreparedStatement preparedStatement = connection.prepareStatement(query);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            Cart cart = new Cart();
	            cart.setCupName(resultSet.getString("CupName"));
	            cart.setCupPrice(resultSet.getInt("CupPrice"));
	            cart.setQuantity(resultSet.getInt("Quantity"));
	            cart.setTotal(resultSet.getInt("Total"));
	            carts.add(cart);
	        }
	    } catch (SQLException e) {
	        e.getMessage();
	    }

	    return carts;
	}
	
	public List<Cart> getAllByUser(String userID) throws Exception {
	    List<Cart> carts = new ArrayList<>();
	    Connection connection = DatabaseConn.connection();

	    String query = "select msc.CupName, msc.CupPrice, c.Quantity, SUM(msc.CupPrice * c.Quantity) as Total from cart as c join mscup as msc on c.CupID = msc.CupID where c.UserID = ? group by msc.CupName, msc.CupPrice, c.Quantity";
	    try {
	        PreparedStatement preparedStatement = connection.prepareStatement(query);

	        // Setel nilai parameter UserID
	        preparedStatement.setString(1, userID);

	        ResultSet resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            Cart cart = new Cart();
	            cart.setCupName(resultSet.getString("CupName"));
	            cart.setCupPrice(resultSet.getInt("CupPrice"));
	            cart.setQuantity(resultSet.getInt("Quantity"));
	            cart.setTotal(resultSet.getInt("Total"));
	            cart.setUserID(userID); // Setel UserID sesuai dengan parameter metode
	            carts.add(cart);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return carts;
	}

}
