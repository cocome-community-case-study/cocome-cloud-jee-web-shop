package org.cocome.cloud.shop.shoppingcart;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;

/**
 * Implements the shopping cart.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@SessionScoped
public class ShoppingCart implements IShoppingCart, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(ShoppingCart.class);
	
	private Map<Long, CartItem> items = new LinkedHashMap<Long, CartItem>();
	private double currentPrice = 0.0;
	private boolean computePrice = false;

	@Override
	public Collection<CartItem> getItems() {
		return items.values();
	}

	@Override
	public boolean addItem(ProductWrapper product) {
		return addItem(product, 1);
	}

	@Override
	public boolean deleteItem(ProductWrapper product) {
		return removeItem(product);
	}

	@Override
	public double getCartPrice() {
		if (computePrice) {
			currentPrice = computeCartPrice();
			computePrice = false;
		}
		return currentPrice;
	}

	private double computeCartPrice() {
		double price = 0.00;
		for (CartItem item : items.values()) {
			price += item.getCompletePrice();
		}
		return price;
	}

	@Override
	public boolean addItem(ProductWrapper product, int quantity) {		
		if (product == null) {
			LOG.warn("Product was null, could not add to cart!");
			return false;
		}
		
		LOG.debug("Adding item " + product.getName() + " to cart with quantity " + quantity);
		
		CartItem item = items.get(CartUtil.computeMapIndex(product));
		
		if (item == null) {
			if (quantity < 1) {
				/* We can't remove an item from the cart that is not there
				 * and adding an item with 0 quantity doesn't make sense. */
				LOG.warn("Item was null and quantity less than 1");
				return false;
			} else {
				item = new CartItem(product, quantity);
				items.put(CartUtil.computeMapIndex(product), item);
				computePrice = true;
				//currentPrice += item.getCompletePrice();
				return true;
			}
		}
		
		int newQuantity = item.getQuantity() + quantity;
		
		if (newQuantity <= 0) {
			// The new item is not in the cart anymore, so remove it
			removeItem(product);
		} else {
			item.setQuantity(newQuantity);
			computePrice = true;
			//currentPrice += item.getSinglePrice() * quantity;
		}
		
		return true;
	}

	private boolean removeItem(ProductWrapper product) {
		if (product == null) {
			LOG.warn("Product was null, could not add to cart!");
			return false;
		}
		
		CartItem item = items.get(CartUtil.computeMapIndex(product));
		
		if (item != null) {
			computePrice = true;
			//currentPrice -= item.getCompletePrice();
			items.remove(CartUtil.computeMapIndex(product));
		} else {
			LOG.warn("Item with the given barcode was not found in the cart: " + product.getBarcode());
		}
		
		return true;
	}

	@Override
	public boolean deleteItem(ProductWrapper product, int quantity) {
		if (product == null || quantity < 0) {
			return false;
		}
		
		// Delete the item by adding a negative quantity to the cart
		int deleteQuantity = quantity * -1;
		
		return addItem(product, deleteQuantity);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	@Override
	public boolean updateItemQuantity(ProductWrapper product, int newQuantity) {
		if (product == null) {
			return false;
		}
		
		CartItem item = items.get(CartUtil.computeMapIndex(product));
		
		if (item == null) {
			return false;
		}
		
		// If the item quantity is set directly on the item the diff will be 0
		int diff = newQuantity - item.getQuantity();	
		return addItem(product, diff);
	}

	@Override
	public void reset() {
		items = new LinkedHashMap<Long, CartItem>();
		currentPrice = 0.0;
		computePrice = false;
	}

}
