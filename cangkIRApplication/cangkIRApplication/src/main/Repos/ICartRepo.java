package main.Repos;

import java.util.List;

import main.Models.Cart;

public interface ICartRepo {
	Cart create(Cart cart) throws Exception;
	Cart findById(String cartId) throws Exception;
	List<Cart> getAll() throws Exception;
}
