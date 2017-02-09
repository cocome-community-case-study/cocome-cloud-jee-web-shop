package org.cocome.cloud.shop.inventory.enterprise;

import java.util.Collection;

import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.shop.inventory.store.Store;

/**
 * Interface for information regarding the currently active enterprise.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IEnterpriseInformation {
	public Collection<Enterprise> getEnterprises() throws NotInDatabaseException_Exception;
	public Collection<Store> getStores() throws NotInDatabaseException_Exception;
	
	public long getActiveEnterpriseID();
	public void setActiveEnterpriseID(long enterpriseID);
	public Enterprise getActiveEnterprise() throws NotInDatabaseException_Exception;
	public String submitActiveEnterprise();
	public boolean isEnterpriseSubmitted();
	public void setEnterpriseSubmitted(boolean submitted);
	public boolean isEnterpriseSet();
}
