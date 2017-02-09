package org.cocome.cloud.shop.inventory;

import java.util.Collection;
import java.util.LinkedHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.logic.stub.ProductOutOfStockException_Exception;
import org.cocome.cloud.logic.stub.UpdateException_Exception;
import org.cocome.cloud.shop.customer.Customer;
import org.cocome.cloud.shop.inventory.connection.CustomerQuery;
import org.cocome.cloud.shop.inventory.connection.IEnterpriseQuery;
import org.cocome.cloud.shop.inventory.connection.IStoreQuery;
import org.cocome.cloud.shop.inventory.connection.caching.IProductCache;
import org.cocome.cloud.shop.inventory.connection.caching.IStockItemCache;
import org.cocome.cloud.shop.inventory.enterprise.IEnterpriseInformation;
import org.cocome.cloud.shop.inventory.store.IStoreInformation;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;
import org.cocome.cloud.shop.shoppingcart.CartItem;
import org.cocome.cloud.shop.shoppingcart.IShoppingCart;

/**
 * Implements the connection to the backend inventory using a custom cache
 * to cache products and stock items.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@RequestScoped
public class InventoryConnector implements IInventory {
	private static final Logger LOG = Logger.getLogger(InventoryConnector.class);
	
	@Inject
	IEnterpriseInformation enterpriseInformation;
	
	@Inject
	IStoreInformation storeInformation;
	
	@Inject
	IEnterpriseQuery enterpriseQuery;
	
	@Inject
	IStoreQuery storeQuery;
	
	@Inject
	IStockItemCache itemCache;
	
	@Inject
	IProductCache productCache;
	
	@Inject
	CustomerQuery customerQuery;
	
	Collection<ProductWrapper> stockItems;
	
	@PostConstruct
	public void init() {
		if (!storeInformation.isStoreSet()) {
			return;
		}
		
		try {
			queryStockItems();
		} catch (NotInDatabaseException_Exception e) {
			LOG.error("Could not query stock items for store with ID " + storeInformation.getActiveStoreID());
		}
	}

	private void queryStockItems() throws NotInDatabaseException_Exception {
		stockItems = itemCache.getAllEntries();
		
		if (stockItems.size() == 0) {
			fillStockItemCache();
		}
	}

	private void fillStockItemCache() throws NotInDatabaseException_Exception {
		stockItems = storeQuery.queryStockItems(storeInformation.getActiveStore());
		
		for (ProductWrapper item : stockItems) {
			itemCache.putEntry(item.getBarcode(), item);
		}
	}
	
	@Override
	public Collection<ProductWrapper> getAvailableStockItems() throws NotInDatabaseException_Exception {
		if (stockItems == null) {
			queryStockItems();
		}
		return stockItems;
	}

	@Override
	public Collection<ProductWrapper> getAllProducts() throws NotInDatabaseException_Exception {
		Collection<ProductWrapper> products = productCache.getAllEntries(); 
		
		if (!storeInformation.isStoreSet()) {
			if (products.size() == 0) {
				fillProductCache();
				products = productCache.getAllEntries();
			}
			LOG.debug("Size of returned collection: " + products.size());
			return products;
		}
		
		return addStockItemInfoToProducts(products);
	}

	private void fillProductCache() throws NotInDatabaseException_Exception {
		Collection<ProductWrapper> products = enterpriseQuery.getAllProducts();
		
		for (ProductWrapper product : products) {
			LOG.debug("Adding product into cache: " + product.getName());
			productCache.putEntry(product.getBarcode(), product);
		}		
	}

	private Collection<ProductWrapper> addStockItemInfoToProducts(Collection<ProductWrapper> products) {
		LinkedHashMap<Long, ProductWrapper> productSet = 
				new LinkedHashMap<Long, ProductWrapper>((int)(products.size() / 0.75) + 1);
		
		try {
			// first add all existing stock items to the set, they will not be overwritten later
			for (ProductWrapper item : getAvailableStockItems()) {
				productSet.put(item.getBarcode(), item);
			}
		} catch (NotInDatabaseException_Exception e) {
			LOG.error("Could not update products for selected store!");
		}
		
		// add the missing products to the set
		for (ProductWrapper product : products) {
			if (!productSet.containsKey(product.getBarcode())) {
				productSet.put(product.getBarcode(), product);
			}
		}
		return productSet.values();
	}

	@Override
	public ProductWrapper getProduct(long barcode) {
		ProductWrapper product = itemCache.getEntry(barcode); 
		if (product != null) {
			return product;
		}
		return productCache.getEntry(barcode);
	}

	@Override
	public boolean accountSale(IShoppingCart cart) throws NotInDatabaseException_Exception, ProductOutOfStockException_Exception, UpdateException_Exception {
		boolean success = storeQuery.accountSale(cart);
		
		if (success) {
			for (CartItem item : cart.getItems()) {
				itemCache.invalidate(item.getProduct().getBarcode());
			}
		}
		
		return success;
	}

	@Override
	public boolean updateCustomer(Customer customer)
			throws NotInDatabaseException_Exception, UpdateException_Exception {
		return customerQuery.updateCustomer(customer);
	}

	
}
