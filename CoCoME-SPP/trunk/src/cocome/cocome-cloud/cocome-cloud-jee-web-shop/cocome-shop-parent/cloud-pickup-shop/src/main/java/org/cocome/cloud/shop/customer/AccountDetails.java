package org.cocome.cloud.shop.customer;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.cocome.cloud.shop.inventory.store.Store;
import org.cocome.logic.stub.StoreTO;

import java.io.Serializable;

/**
 * Holds the account information for the currently logged in customer.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 *
 */
@Named
@SessionScoped
public class AccountDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject @LoggedIn
	Customer customer;
	
	public String getFirstName() {
		return customer.getFirstName();		
	}
	
	public String getLastName() {
		return customer.getLastName();
	}
	
	public String getMailAddress() {
		return customer.getMailAddress();
	}
	
	public String getPreferredStoreName() {
		StoreTO store = customer.getPreferredStore(); 
		return store != null ? store.getName() : "";
	}
	
	public String getPreferredStoreLocation() {
		StoreTO store = customer.getPreferredStore(); 
		return store != null ? store.getLocation() : "";
	}
}
