package org.cocome.cloud.shop.inventory.connection;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.enterprise.context.RequestScoped;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.WebServiceRef;

import org.apache.log4j.Logger;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;
import org.cocome.cloud.shop.inventory.store.Store;
import org.cocome.cloud.shop.shoppingcart.CartItem;
import org.cocome.cloud.shop.shoppingcart.IShoppingCart;
import org.cocome.cloud.shop.shoppingcart.ShoppingCart;
import org.cocome.logic.stub.IStoreManager;
import org.cocome.logic.stub.NotInDatabaseException_Exception;
import org.cocome.logic.stub.ProductOutOfStockException_Exception;
import org.cocome.logic.stub.ProductTO;
import org.cocome.logic.stub.ProductWithStockItemTO;
import org.cocome.logic.stub.ProductWithSupplierAndStockItemTO;
import org.cocome.logic.stub.SaleTO;
import org.cocome.logic.stub.SaleTO.ProductTOs;
import org.cocome.logic.stub.StoreManagerService;
import org.cocome.logic.stub.UpdateException_Exception;

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
	
	@WebServiceRef(StoreManagerService.class)
	IStoreManager storeManager;
	
	@Override
	public List<ProductWrapper> queryStockItems(Store store) throws NotInDatabaseException_Exception {
		long storeID = store.getID();
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
				ProductTOs productTOs = new ProductTOs();
				sale.setProductTOs(productTOs);
				saleByStore.put(item.getStore().getID(), sale);
			}
			
			try {
				sale.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
			} catch (DatatypeConfigurationException e) {
				LOG.error("Could not get date for sale!");
				return false;
			}
			
			for (int i = 0; i < item.getQuantity(); i++) {
				ProductWithStockItemTO product = new ProductWithStockItemTO();
				product.setBarcode(item.getProduct().getBarcode());
				product.setPurchasePrice(item.getProduct().getSalesPrice());
				product.setStockItemTO(item.getProduct().getStockItemTO());
				sale.getProductTOs().getProductTO().add(product);
			}
			

		}
		
		for (Entry<Long, SaleTO> sale : saleByStore.entrySet()) {
			storeManager.accountSale(sale.getKey(), sale.getValue());
		}
		
		return true;
	}
}
