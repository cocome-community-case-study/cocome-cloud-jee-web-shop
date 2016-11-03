package org.cocome.cloud.shop.inventory.connection.caching;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.shop.inventory.connection.IEnterpriseQuery;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;

/**
 * Implements a product cache using a {@link LinkedHashMap} as data store.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@ApplicationScoped
public class ProductCache extends AbstractCache<Long, ProductWrapper> implements IProductCache {
	private static final Logger LOG = Logger.getLogger(ProductCache.class);
	
	private LinkedHashMap<Long, ProductWrapper> cacheMap;
	
	@Inject
	IEnterpriseQuery enterpriseQuery;
	
	@Override
	protected ProductWrapper getEntryFromStore(Long key) {
		ProductWrapper product;
		try {
			product = enterpriseQuery.getProductByBarcode(key);
			this.putEntry(product.getBarcode(), product);
		} catch (NotInDatabaseException_Exception e) {
			LOG.warn("The following product ID could not be found in the database: " + key);
			product = null;
		}
		
		return product;
	}

	@Override
	protected Map<Long, ProductWrapper> getCacheMap() {
		if (cacheMap == null) {
			cacheMap = new LinkedHashMap<Long, ProductWrapper>();
		}
		
		return cacheMap;
	}

}
