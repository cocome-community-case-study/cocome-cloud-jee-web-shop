package org.cocome.cloud.shop.inventory.store;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.shop.inventory.connection.IEnterpriseQuery;
import org.cocome.cloud.shop.navigation.INavigationMenu;

/**
 * Holds information about the currently active store.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@SessionScoped
public class StoreInformation implements IStoreInformation, Serializable {	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(StoreInformation.class);
	
	private long activeStoreID = IStoreInformation.STORE_ID_NOT_SET;
	private Store activeStore;
	private boolean hasChanged = false;

	@Inject
	IEnterpriseQuery enterpriseQuery;
	
	@Inject
	INavigationMenu navigationMenu;
	
	@Override
	public Store getActiveStore() throws NotInDatabaseException_Exception {
		LOG.debug("Active store is being retrieved from the database");
		if ((activeStore == null || hasChanged == true) && activeStoreID != STORE_ID_NOT_SET) {
			activeStore = enterpriseQuery.getStoreByID(activeStoreID);
		}
		return activeStore;
	}

	@Override
	public void setActiveStoreID(long storeID) {
		LOG.debug("Active store was set to id " + storeID);
		this.activeStoreID = storeID;		
	}

	@Override
	public long getActiveStoreID() {
		return activeStoreID;
	}

	@Override
	public String submitStore() {
		LOG.debug("Submit store was called");
		if (isStoreSet()) {
			navigationMenu.removeElement("shopSelect");
			hasChanged = true; 
			return "showStockItems";
		} else {
			return "error";
		}
	}

	@Override
	public boolean isStoreSet() {
		return activeStoreID != IStoreInformation.STORE_ID_NOT_SET;
	}
}
