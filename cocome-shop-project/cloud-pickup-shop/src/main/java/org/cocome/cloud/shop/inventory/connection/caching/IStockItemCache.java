package org.cocome.cloud.shop.inventory.connection.caching;

import org.cocome.cloud.shop.inventory.store.ProductWrapper;

/**
 * Interface for a cache that holds stock item instances.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IStockItemCache extends ICache<Long, ProductWrapper> {

}
