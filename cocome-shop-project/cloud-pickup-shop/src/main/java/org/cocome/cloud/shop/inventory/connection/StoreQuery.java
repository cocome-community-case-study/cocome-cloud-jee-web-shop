package org.cocome.cloud.shop.inventory.connection;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.cocome.cloud.logic.registry.client.IApplicationHelper;
import org.cocome.cloud.logic.stub.IStoreManager;
import org.cocome.cloud.logic.stub.IStoreManagerService;
import org.cocome.cloud.logic.stub.NotBoundException_Exception;
import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.logic.stub.ProductOutOfStockException_Exception;
import org.cocome.cloud.logic.stub.UpdateException_Exception;
import org.cocome.cloud.registry.service.Names;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;
import org.cocome.cloud.shop.inventory.store.Store;
import org.cocome.cloud.shop.shoppingcart.CartItem;
import org.cocome.cloud.shop.shoppingcart.IShoppingCart;
import org.cocome.tradingsystem.inventory.application.store.ProductWithStockItemTO;
import org.cocome.tradingsystem.inventory.application.store.ProductWithSupplierAndStockItemTO;
import org.cocome.tradingsystem.inventory.application.store.SaleTO;

/**
 * Implements the store query interface to retrieve store related information.
 * Uses the web service interface from CoCoMEs logic.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@RequestScoped
public class StoreQuery implements IStoreQuery {
	private static final Logger LOG = Logger.getLogger(StoreQuery.class);
	
	IStoreManager storeManager;
	
	@Inject
	long defaultStoreIndex;
	
	@Inject
	IApplicationHelper applicationHelper;
	
	private IStoreManager lookupStoreManager(long storeID) throws NotInDatabaseException_Exception {
		try {
			return applicationHelper.getComponent(
					Names.getStoreManagerRegistryName(storeID), 
					IStoreManagerService.SERVICE, 
					IStoreManagerService.class).getIStoreManagerPort();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| MalformedURLException | NoSuchMethodException | SecurityException | NotBoundException_Exception e) {
			if (storeID == defaultStoreIndex) {
			LOG.error("Got exception while retrieving store manager location: " + e.getMessage());
			e.printStackTrace();
			throw new NotInDatabaseException_Exception(e.getMessage());
			} else {
				return lookupStoreManager(defaultStoreIndex);
			}
		}
	}
	
	@Override
	public List<ProductWrapper> queryStockItems(Store store) throws NotInDatabaseException_Exception {
		long storeID = store.getID();
		storeManager = lookupStoreManager(storeID);
		List<ProductWrapper> stockItems = new LinkedList<ProductWrapper>();
		List<ProductWithSupplierAndStockItemTO> items = storeManager.getProductsWithStockItems(storeID);
		for (ProductWithSupplierAndStockItemTO item : items) {
			ProductWrapper newItem = new ProductWrapper(item, item.getStockItemTO(), store);
			stockItems.add(newItem);
		}
		return stockItems;
	}

	@Override
	public ProductWrapper getStockItemByProductID(Store store, long productID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductWrapper getStockItemByBarcode(Store store, long barcode) {
		return null;
	}

	@Override
	public boolean accountSale(IShoppingCart cart) throws NotInDatabaseException_Exception, ProductOutOfStockException_Exception, UpdateException_Exception {
		LinkedHashMap<Long, SaleTO> saleByStore = new LinkedHashMap<>((int) (cart.getItemCount() / 0.75));
		
		for (CartItem item : cart.getItems()) {
			SaleTO sale = saleByStore.get(item.getStore().getID());
			
			if (sale == null) {
				sale = new SaleTO();
				List<ProductWithStockItemTO> productTOs = new LinkedList<>();
				sale.setProductTOs(productTOs);
				saleByStore.put(item.getStore().getID(), sale);
			}
			
			sale.setDate(new Date());
			
			for (int i = 0; i < item.getQuantity(); i++) {
				ProductWithStockItemTO product = new ProductWithStockItemTO();
				product.setBarcode(item.getProduct().getBarcode());
				product.setPurchasePrice(item.getProduct().getSalesPrice());
				product.setStockItemTO(item.getProduct().getStockItemTO());
				sale.getProductTOs().add(product);
			}
			

		}
		
		for (Entry<Long, SaleTO> sale : saleByStore.entrySet()) {
			storeManager = lookupStoreManager(sale.getKey());
			storeManager.accountSale(sale.getKey(), sale.getValue());
		}
		
		return true;
	}
}
