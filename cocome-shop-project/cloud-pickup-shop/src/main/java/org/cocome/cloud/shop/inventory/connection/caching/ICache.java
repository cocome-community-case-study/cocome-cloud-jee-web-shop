package org.cocome.cloud.shop.inventory.connection.caching;

import java.util.Collection;
/**
 * Interface for a basic key-value cache.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 * @param <K> - The type of the keys.
 * @param <V> - The type of the values.
 */
public interface ICache<K, V> {
	/**
	 * Checks if there is an entry with this key and returns it when found.
	 * Otherwise, the cache will try to retrieve the entry from the backing data store.
	 * If the entry is not found there either, {@code null} will be returned.
	 * 
	 * @param key the key to retrieve
	 * @return
	 * 	the value of the entry or {@code null} if no entry can be found 
	 */
	public V getEntry(K key);
	
	/**
	 * Adds this entry to the cache. Use this method to fill the cache.
	 * 
	 * @param key the key of the entry
	 * @param value the value of the entry
	 */
	public void putEntry(K key, V value);
	
	/**
	 * Returns all entries currently present in this cache.
	 * The returned collection is unmodifiable.
	 * 
	 * @return all entries
	 */
	public Collection<V> getAllEntries();
	
	/**
	 * Invalidates all entries in the cache. A subsequent get on the cache will then lead 
	 * to a query of the backing data store.
	 */
	public void invalidate();
	
	/**
	 * Invalidates the entry with the given key. A subsequent get with this key will
	 * lead to a query of the backing data store to update the cached value.
	 * 
	 * @param key the key to invalidate
	 */
	public void invalidate(K key);
}
