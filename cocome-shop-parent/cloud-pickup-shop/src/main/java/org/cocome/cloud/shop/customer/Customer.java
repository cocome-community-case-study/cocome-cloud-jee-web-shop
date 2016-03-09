package org.cocome.cloud.shop.customer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceRef;

import org.apache.log4j.Logger;
import org.cocome.cloud.shop.inventory.IInventory;
import org.cocome.logic.stub.CredentialTO;
import org.cocome.logic.stub.CredentialType;
import org.cocome.logic.stub.CustomerWithStoreTO;
import org.cocome.logic.stub.ILoginManager;
import org.cocome.logic.stub.LoginManagerService;
import org.cocome.logic.stub.NotInDatabaseException_Exception;
import org.cocome.logic.stub.StoreTO;
import org.cocome.logic.stub.UpdateException_Exception;
import org.cocome.logic.stub.UserTO;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holds the Customer information.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Model
public class Customer implements Serializable {
	private static final Logger LOG = Logger.getLogger(Customer.class);
	private static final long serialVersionUID = 1L;
	
	private long id;
	
	private String firstName;
	
	private String lastName;
	
	private String mailAddress;
	
	private StoreTO preferredStore;
	
	private UserTO userTO;
	
	private Set<String> creditCardInfos = new LinkedHashSet<String>();
	
	@Inject
	IInventory inventory;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public void initCustomerFields(CustomerWithStoreTO customer) {
		setId(customer.getId());
		firstName = customer.getFirstName();
		lastName = customer.getLastName();
		mailAddress = customer.getMailAddress();
		setPreferredStore(customer.getPreferredStoreTO());
		setCreditCardInfos(new LinkedHashSet<>(customer.getCreditCardInfos()));
	}

	public StoreTO getPreferredStore() {
		return preferredStore;
	}

	public void setPreferredStore(StoreTO preferredStore) {
		this.preferredStore = preferredStore;
	}

	public UserTO getUserTO() {
		return userTO;
	}

	public void setUserTO(UserTO userTO) {
		this.userTO = userTO;
	}

	public Set<String> getCreditCardInfos() {
		return creditCardInfos;
	}

	public void setCreditCardInfos(Set<String> creditCardInfos) {
		this.creditCardInfos = creditCardInfos;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public boolean update() throws NotInDatabaseException_Exception, UpdateException_Exception {
		return inventory.updateCustomer(this);
	}
}
