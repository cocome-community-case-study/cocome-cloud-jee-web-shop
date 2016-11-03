package org.cocome.cloud.shop.inventory.connection;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.xml.ws.WebServiceRef;

import org.apache.log4j.Logger;
import org.cocome.cloud.logic.stub.IEnterpriseManager;
import org.cocome.cloud.logic.stub.IEnterpriseManagerService;
import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.shop.inventory.enterprise.Enterprise;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;
import org.cocome.cloud.shop.inventory.store.Store;
import org.cocome.tradingsystem.inventory.application.store.EnterpriseTO;
import org.cocome.tradingsystem.inventory.application.store.ProductTO;
import org.cocome.tradingsystem.inventory.application.store.StoreWithEnterpriseTO;

/**
 * Implements the enterprise query interface to retrieve information 
 * from the backend about available enterprises and stores. 
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@ApplicationScoped
public class EnterpriseQuery implements IEnterpriseQuery {
	private static final Logger LOG = Logger.getLogger(EnterpriseQuery.class);
	
	private Map<Long, Enterprise> enterprises;
	private Map<Long, Collection<Store>> storeCollections;
	private Map<Long, Store> stores;
	
	@WebServiceRef(IEnterpriseManagerService.class)
	IEnterpriseManager enterpriseManager;
	
	/* (non-Javadoc)
	 * @see org.cocome.cloud.shop.inventory.connection.IEnterpriseQuery#getEnterprises()
	 */
	@Override
	public Collection<Enterprise> getEnterprises() {
		if(enterprises != null) {
			return enterprises.values();
		}
		
		updateEnterpriseInformation();
		return enterprises.values();
	}
	
	/* (non-Javadoc)
	 * @see org.cocome.cloud.shop.inventory.connection.IEnterpriseQuery#getStores(long)
	 */
	@Override
	public Collection<Store> getStores(long enterpriseID) throws NotInDatabaseException_Exception {
		if(storeCollections == null) {
			updateStoreInformation();
		}
		
		Collection<Store> storeCollection = storeCollections.get(enterpriseID);
		return storeCollection != null ? storeCollection : new LinkedList<Store>();
	}

	@Override
	public void updateEnterpriseInformation() {
		this.enterprises = new HashMap<Long, Enterprise>();
		
		for (EnterpriseTO enterprise : enterpriseManager.getEnterprises()) {
			enterprises.put(enterprise.getId(), new Enterprise(enterprise.getId(), enterprise.getName()));
		}
	}

	@Override
	public void updateStoreInformation() throws NotInDatabaseException_Exception {
		if (this.enterprises == null) {
			updateEnterpriseInformation();
		}
		
		// This collection has a fixed size depending on the number of enterprises, 
		// so we can set the initial capacity to this to avoid overhead 
		storeCollections = new HashMap<Long, Collection<Store>>((int)(enterprises.size() / 0.75) + 1);
		
		this.stores = new HashMap<Long, Store>();
		
		for (Enterprise ent : enterprises.values()) {
			LinkedList<Store> stores = new LinkedList<Store>();

			for (StoreWithEnterpriseTO store : enterpriseManager.queryStoresByEnterpriseID(ent.getId())) {
				Store tempStore = new Store(store.getId(), store.getLocation(), store.getName());
				this.stores.put(tempStore.getID(), tempStore);
				stores.add(tempStore);
			}
			this.storeCollections.put(ent.getId(), stores);
		}
	}

	@Override
	public Enterprise getEnterpriseByID(long enterpriseID) {
		if (enterprises == null) {
			updateEnterpriseInformation();
		}
		
		return enterprises.get(enterpriseID);
	}

	@Override
	public Store getStoreByID(long storeID) throws NotInDatabaseException_Exception {
		LOG.debug("Retrieving store with id " + storeID);
		
		if (stores == null) {
			updateStoreInformation();
		}
		Store store = stores.get(storeID);
		LOG.debug("Got store " + store.getName());
		return store;
	}
	
	public List<ProductWrapper> getAllProducts() {
		// TODO should definitely be cached somehow, especially when there are lots of products
		List<ProductWrapper> products = new LinkedList<ProductWrapper>();
		for (ProductTO product : enterpriseManager.getAllProducts()) {
			ProductWrapper wrapper = new ProductWrapper(product);
			products.add(wrapper);
		}
		return products;
	}

	@Override
	public ProductWrapper getProductByID(long productID) throws NotInDatabaseException_Exception {
		ProductWrapper product = new ProductWrapper(enterpriseManager.getProductByID(productID));
		return product;
	}

	@Override
	public ProductWrapper getProductByBarcode(long barcode) throws NotInDatabaseException_Exception {
		ProductWrapper product = new ProductWrapper(enterpriseManager.getProductByBarcode(barcode));
		return product;
	}
}
