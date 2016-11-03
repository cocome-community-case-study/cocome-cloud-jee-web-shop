package org.cocome.cloud.shop.shoppingcart;

import java.util.Collection;

import org.cocome.cloud.shop.inventory.store.ProductWrapper;

/**
 * Interface for the shopping cart.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IShoppingCart {
	public Collection<CartItem> getItems();
	public boolean addItem(ProductWrapper product);
	public boolean addItem(ProductWrapper product, int quantity);
	public boolean deleteItem(ProductWrapper product);
	public boolean deleteItem(ProductWrapper product, int quantity);
	public double getCartPrice();
	public int getItemCount();
	public boolean updateItemQuantity(ProductWrapper product, int newQuantity);
	public void reset();
}
