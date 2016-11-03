package org.cocome.cloud.shop.shoppingcart;

import org.cocome.cloud.shop.inventory.store.ProductWrapper;
import org.cocome.cloud.shop.inventory.store.Store;

/**
 * Represents a stock item in the shopping cart and is backed by a 
 * {@link ProductWrapper} object.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public class CartItem {
	private ProductWrapper product;
	private int quantity;
	
	public CartItem(ProductWrapper product, int quantity) {
		this.setProduct(product);
		this.setQuantity(quantity >= 0 ? quantity : 1);
	}

	public ProductWrapper getProduct() {
		return product;
	}

	public void setProduct(ProductWrapper product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity >= 0 ? quantity : 1;
	}
	
	public double getSinglePrice() {
		return product.getSalesPrice();
	}
	
	public double getCompletePrice() {
		return product.getSalesPrice() * quantity;
	}
	
	public long getAmountAvailable() {
		return product.getAmount();
	}
	
	public Store getStore() {
		return product.getOriginStore();
	}
}
