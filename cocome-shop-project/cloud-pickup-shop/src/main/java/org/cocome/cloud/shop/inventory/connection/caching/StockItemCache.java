package org.cocome.cloud.shop.inventory.connection.caching;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.shop.inventory.connection.IStoreQuery;
import org.cocome.cloud.shop.inventory.store.IStoreInformation;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;
/**
 * Implements a cache for stock items using a {@link LinkedHashMap} as backing store.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@ApplicationScoped
public class StockItemCache extends AbstractCache<Long, ProductWrapper> implements IStockItemCache {
	private HashMap<Long, LinkedHashMap<Long, ProductWrapper>> cachedStoreMaps = 
			new HashMap<Long, LinkedHashMap<Long, ProductWrapper>>();
	
	@Inject
	IStoreInformation storeInformation;
	
	@Inject
	IStoreQuery storeQuery;
	
	@Override
	protected ProductWrapper getEntryFromStore(Long key) {
		ProductWrapper stockItem;
		
		if (!storeInformation.isStoreSet()) {
			return null;
		}
		
		try {
			stockItem = storeQuery.getStockItemByBarcode(storeInformation.getActiveStore(), key);
		} catch (NotInDatabaseException_Exception e) {
			return null;
		}
		
		if (stockItem != null) {
			this.putEntry(stockItem.getBarcode(), stockItem);
		}

		return stockItem;
	}

	@Override
	protected Map<Long, ProductWrapper> getCacheMap() {
		if (!storeInformation.isStoreSet()) {
			return Collections.emptyMap();
		}
		
		long storeID = storeInformation.getActiveStoreID();
		LinkedHashMap<Long, ProductWrapper> cache = cachedStoreMaps.get(storeID);
		if (cache == null) {
			cache = new LinkedHashMap<Long, ProductWrapper>();
			cachedStoreMaps.put(storeID, cache);
		}
		
		return cache;
	}

}
