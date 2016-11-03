package org.cocome.cloud.shop.shoppingcart;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;

/**
 * Utility class for the shopping cart.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
public class CartUtil {
	public static final Logger LOG = Logger.getLogger(CartUtil.class);
	
	private CartUtil() {
		// Utility class
	}
	
	public static long computeMapIndex(ProductWrapper product) {
		Long barcode = product.getBarcode();
		Long storeID = product.getOriginStore().getID();
		long index = (barcode << Long.highestOneBit(storeID)) + storeID;
		LOG.debug("Map index for product " + product.getBarcode() 
			+ " in store " + product.getOriginStore().getID() + " is " + index);
		return index;
	}
	
	public static List<Integer> getQuantityList(ProductWrapper product) {
		long availableAmount = product.getAmount() > 30 ? 30 : product.getAmount();
		List<Integer> quantities = new ArrayList<Integer>((int) availableAmount);
		
		for (int i = 1; i <= availableAmount; i++) {
			quantities.add(i);
		}
		return quantities;
	}
}
