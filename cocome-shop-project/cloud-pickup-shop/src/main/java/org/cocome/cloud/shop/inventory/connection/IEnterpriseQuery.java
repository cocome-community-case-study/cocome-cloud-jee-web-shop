package org.cocome.cloud.shop.inventory.connection;

import java.util.Collection;
import java.util.List;

import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.shop.inventory.enterprise.Enterprise;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;
import org.cocome.cloud.shop.inventory.store.Store;

/**
 * Interface for the retrieval of enterprise and store related information from the backend.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IEnterpriseQuery {

	public Collection<Enterprise> getEnterprises() throws NotInDatabaseException_Exception;

	public Collection<Store> getStores(long enterpriseID) throws NotInDatabaseException_Exception;

	public void updateEnterpriseInformation() throws NotInDatabaseException_Exception;
	
	public void updateStoreInformation() throws NotInDatabaseException_Exception;
	
	public Enterprise getEnterpriseByID(long enterpriseID) throws NotInDatabaseException_Exception;
	
	public Store getStoreByID(long storeID) throws NotInDatabaseException_Exception;
	
	public List<ProductWrapper> getAllProducts() throws NotInDatabaseException_Exception;
	
	public ProductWrapper getProductByID(long productID) throws NotInDatabaseException_Exception;
	
	public ProductWrapper getProductByBarcode(long barcode) throws NotInDatabaseException_Exception;
}