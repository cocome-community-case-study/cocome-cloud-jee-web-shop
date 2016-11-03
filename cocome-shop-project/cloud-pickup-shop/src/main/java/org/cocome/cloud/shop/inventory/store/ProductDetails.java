package org.cocome.cloud.shop.inventory.store;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Holds information about a currently selected product.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@SessionScoped
public class ProductDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ProductWrapper product;

	public void setProduct(ProductWrapper product) {
		this.product = product;
	}
	
	public ProductWrapper getProduct() {
		return product;
	}
}
