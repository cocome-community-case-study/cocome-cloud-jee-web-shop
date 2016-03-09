package org.cocome.cloud.shop.inventory;

import java.util.Collection;

import org.cocome.cloud.shop.customer.Customer;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;
import org.cocome.cloud.shop.shoppingcart.IShoppingCart;
import org.cocome.cloud.shop.shoppingcart.ShoppingCart;
import org.cocome.logic.stub.NotInDatabaseException_Exception;
import org.cocome.logic.stub.ProductOutOfStockException_Exception;
import org.cocome.logic.stub.UpdateException_Exception;

/**
 * Interface for accessing the inventory. Implementations should implement 
 * caching mechanisms to reduce the calls to the backend. 
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IInventory {
	public Collection<ProductWrapper> getAvailableStockItems() throws NotInDatabaseException_Exception;
	
	public Collection<ProductWrapper> getAllProducts();
	
	public ProductWrapper getProduct(long barcode);
	
	public boolean accountSale(IShoppingCart cart) throws NotInDatabaseException_Exception, ProductOutOfStockException_Exception, UpdateException_Exception;
	
	public boolean updateCustomer(Customer customer) throws NotInDatabaseException_Exception, UpdateException_Exception;
}
