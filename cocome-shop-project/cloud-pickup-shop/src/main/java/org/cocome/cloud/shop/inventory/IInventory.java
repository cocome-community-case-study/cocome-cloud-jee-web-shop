package org.cocome.cloud.shop.inventory;

import java.util.Collection;

import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.logic.stub.ProductOutOfStockException_Exception;
import org.cocome.cloud.logic.stub.UpdateException_Exception;
import org.cocome.cloud.shop.customer.Customer;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;
import org.cocome.cloud.shop.shoppingcart.IShoppingCart;

/**
 * Interface for accessing the inventory. Implementations should implement 
 * caching mechanisms to reduce the calls to the backend. 
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IInventory {
	public Collection<ProductWrapper> getAvailableStockItems() throws NotInDatabaseException_Exception;
	
	public Collection<ProductWrapper> getAllProducts() throws NotInDatabaseException_Exception;
	
	public ProductWrapper getProduct(long barcode);
	
	public boolean accountSale(IShoppingCart cart) throws NotInDatabaseException_Exception, ProductOutOfStockException_Exception, UpdateException_Exception;
	
	public boolean updateCustomer(Customer customer) throws NotInDatabaseException_Exception, UpdateException_Exception;
}
