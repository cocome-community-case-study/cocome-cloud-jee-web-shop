package org.cocome.cloud.shop.inventory.store;

import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;

/**
 * Interface to retrieve information about the currently active store.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IStoreInformation {
	public static final long STORE_ID_NOT_SET = Long.MIN_VALUE;
	
	public void setActiveStoreID(long storeID);
	public long getActiveStoreID();
	public Store getActiveStore() throws NotInDatabaseException_Exception;
	public String submitStore();
	
	public boolean isStoreSet();
}
