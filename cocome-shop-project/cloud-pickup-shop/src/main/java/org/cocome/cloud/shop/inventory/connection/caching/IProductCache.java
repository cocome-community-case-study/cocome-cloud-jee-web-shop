package org.cocome.cloud.shop.inventory.connection.caching;

import org.cocome.cloud.shop.inventory.store.ProductWrapper;

/**
 * Interface for a cache that holds product instances.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IProductCache extends ICache<Long, ProductWrapper> {

}
