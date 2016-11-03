package org.cocome.cloud.shop.inventory.connection;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.xml.ws.WebServiceRef;
import org.apache.log4j.Logger;
import org.cocome.cloud.logic.stub.ILoginManager;
import org.cocome.cloud.logic.stub.ILoginManagerService;
import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.shop.customer.Customer;
import org.cocome.cloud.shop.customer.CustomerRegistration;
import org.cocome.cloud.shop.customer.LoggedIn;
import org.cocome.cloud.shop.customer.UserLogin;
import org.cocome.cloud.shop.inventory.store.IStoreInformation;
import org.cocome.cloud.shop.inventory.store.Store;
import org.cocome.cloud.shop.inventory.store.StoreInformation;
import org.cocome.tradingsystem.inventory.application.store.CustomerWithStoreTO;
import org.cocome.tradingsystem.inventory.application.store.StoreTO;
import org.cocome.tradingsystem.inventory.application.usermanager.CredentialTO;
import org.cocome.tradingsystem.inventory.application.usermanager.CredentialType;
import org.cocome.tradingsystem.inventory.application.usermanager.Role;
import org.cocome.tradingsystem.inventory.application.usermanager.UserTO;

import java.security.Principal;

/**
 * Queries customers from the backend and transforms the response 
 * into a Customer instance.  
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Stateless
public class CustomerQuery {
	private static final Logger LOG = Logger.getLogger(CustomerQuery.class);
	
	@Inject
	Principal principal;
	
	@Inject
	UserLogin login;
	
	@Inject
	StoreInformation storeInformation;
	
	@WebServiceRef(ILoginManagerService.class)
	ILoginManager loginManager;
	
	@Inject
	CustomerRegistration customerReg;
	
	@RolesAllowed("Customer")
	@Produces @LoggedIn @SessionScoped
	public Customer getCurrentCustomer(@New Customer customer) {
		UserTO userTO = initUserTO(principal.getName(), login.getPassword());

		try {
			UserTO serverTO = loginManager.requestAuthToken(userTO);
			CustomerWithStoreTO customerTO = loginManager.getCustomerWithStoreTO(
					serverTO, principal.getName());
			customer.setUserTO(serverTO);
			customer.initCustomerFields(customerTO);
		} catch (NotInDatabaseException_Exception s) {
			login.logout();
			LOG.error("Customer could not be retrieved from the database.");
		}
		
		setStoreInformation(customer);
		return customer;
	}
	
	public boolean createUserWithPassword(String username, String password) {
		return loginManager.createNewUser(initUserTO(username, password));
	}
	
	public boolean registerNewCustomer() {
		CustomerWithStoreTO customer = initCustomerTO();
		return loginManager.createNewCustomer(customer);
	}

	private void setStoreInformation(Customer customer) {
		StoreTO store = customer.getPreferredStore();
		if (store != null && store.getId() != IStoreInformation.STORE_ID_NOT_SET) {
			storeInformation.setActiveStoreID(store.getId());
			storeInformation.submitStore();
		}
	}
	
	private UserTO initUserTO(String username, String password) {
		CredentialTO credential = new CredentialTO();
		credential.setType(CredentialType.PASSWORD);
		credential.setCredentialString(password);
		
		UserTO userTO = new UserTO();
		userTO.setUsername(username);
		userTO.getCredentials().add(credential);
		return userTO;
	}
	
	private CustomerWithStoreTO initCustomerTO() {
		CustomerWithStoreTO customer = new CustomerWithStoreTO();
		customer.setFirstName(customerReg.getFirstName());
		customer.setLastName(customerReg.getLastName());
		customer.setMailAddress(customerReg.getMailAddress());
		
		UserTO user = initUserTO(customer.getMailAddress(), customerReg.getPassword1());
		customer.setUsername(user.getUsername());
		customer.getCredentials().addAll(user.getCredentials());
		customer.getRoles().add(Role.CUSTOMER);
		
		Store activeStore = null;
		try {
			activeStore = storeInformation.getActiveStore(); 
		} catch (NotInDatabaseException_Exception e) {
			LOG.warn("Active store not found in database, ignoring.");
		}
		
		if (activeStore != null) {
			StoreTO store = initStoreTO(activeStore);
			customer.setPreferredStoreTO(store);
		}
		
		return customer;
	}
	
	public boolean updateCustomer(Customer customer) {
		CustomerWithStoreTO customerTO = new CustomerWithStoreTO();
		customerTO.setId(customer.getId());
		customerTO.setFirstName(customer.getFirstName());
		customerTO.setLastName(customer.getLastName());
		customerTO.setUsername(customer.getUserTO().getUsername());
		customerTO.setPreferredStoreTO(customer.getPreferredStore());
		
		for (String creditCard : customer.getCreditCardInfos()) {
			customerTO.getCreditCardInfos().add(creditCard);
		}
		
		return loginManager.updateCustomer(customerTO);
	}

	private StoreTO initStoreTO(Store activeStore) {
		StoreTO store = new StoreTO();
		store.setId(activeStore.getID());
		store.setLocation(activeStore.getLocation());
		store.setName(activeStore.getName());
		return store;
	}
}
