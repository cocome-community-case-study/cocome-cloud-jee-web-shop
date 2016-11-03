package org.cocome.cloud.shop.inventory.connection.caching;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Implements an abstract cache as a key-value store.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 * 
 * @param <K> - The type of the keys used.
 * @param <V> - The type of the values used.
 */
public abstract class AbstractCache<K, V> implements ICache<K, V> {
	private static final Logger LOG = Logger.getLogger(AbstractCache.class);
	
	/**
	 * Gets the map in which the cached values should be stored in.
	 * In this way, every subclass may decide on the best type of map
	 * to use. 
	 * 
	 * @return the map with the cached values
	 */
	protected abstract Map<K, V> getCacheMap();
	
	/**
	 * Queries the backing data store for the value corresponding to 
	 * the given key. Either returns the value or {@code null} if 
	 * the key does not exist in the data store. If the returned 
	 * value was not {@code null}, this method should store the value in 
	 * the cache. 
	 * 
	 * This method is called if the value could not be found in the 
	 * cache or the cache was invalidated before. 
	 * 
	 * @param key the key to look for
	 * @return
	 * 		the corresponding value or {@code null} if no entry is found
	 */
	protected abstract V getEntryFromStore(K key);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V getEntry(K key) {
		if (key == null) {
			return null;
		}
		
		V cachedValue = getCacheMap().get(key);
		
		if (cachedValue != null) {
			return cachedValue;
		}
		
		return getCacheMap().get(key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putEntry(K key, V value) {
		if (key == null) {
			LOG.warn("Could not put value into cache, key was null!");
			return;
		}
		
		LOG.debug("Added (key, value) to cache: (" + key + ", " + value + ")");
		getCacheMap().put(key, value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<V> getAllEntries() {
		return Collections.unmodifiableCollection(getCacheMap().values());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invalidate() {
		getCacheMap().clear();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invalidate(K key) {
		getCacheMap().remove(key);
	}
}
