package org.cocome.cloud.shop.inventory.enterprise;

import java.util.Collection;

import org.cocome.cloud.shop.inventory.store.Store;
import org.cocome.logic.stub.NotInDatabaseException_Exception;

/**
 * Interface for information regarding the currently active enterprise.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IEnterpriseInformation {
	public Collection<Enterprise> getEnterprises();
	public Collection<Store> getStores() throws NotInDatabaseException_Exception;
	
	public long getActiveEnterpriseID();
	public void setActiveEnterpriseID(long enterpriseID);
	public Enterprise getActiveEnterprise();
	public String submitActiveEnterprise();
	public boolean isEnterpriseSubmitted();
	public void setEnterpriseSubmitted(boolean submitted);
	public boolean isEnterpriseSet();
}
