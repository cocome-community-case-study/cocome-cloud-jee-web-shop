package org.cocome.cloud.shop.customer;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.shop.inventory.connection.CustomerQuery;
import org.cocome.cloud.shop.inventory.store.Store;
import java.io.Serializable;

/**
 * Handles the information for registering a new customer. 
 * Also submits the information to the backend query on completion.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@SessionScoped
public class CustomerRegistration implements Serializable {
	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	private String password1;
	private String password2;
	private String mailAddress;
	private Store preferredStore;
	
	@Inject
	CustomerQuery customerQuery;

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

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public Store getPreferredStore() {
		return preferredStore;
	}

	public void setPreferredStore(Store preferredStore) {
		this.preferredStore = preferredStore;
	}
	
	public String submit() throws NotInDatabaseException_Exception {
		// TODO check user input
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (!password1.equals(password2)) {
			// TODO use resource strings for messages
			context.addMessage(null, new FacesMessage("The passwords didn't match!"));
			return "";
		} else if (!createCustomer()) {
			context.addMessage(null, new FacesMessage("There was an error while registering!"));
			return "error";
		} else {
			return "registerSuccess";
		}
	}

	private boolean createCustomer() throws NotInDatabaseException_Exception {
		return customerQuery.registerNewCustomer();
	}
}
