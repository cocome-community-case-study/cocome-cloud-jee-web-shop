package org.cocome.cloud.shop.customer;

import java.io.Serializable;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.logic.stub.UpdateException_Exception;
import org.cocome.cloud.shop.inventory.IInventory;

/**
 * Implements a wizard to guide the user through the creation of a new credit 
 * card entry.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@ConversationScoped
public class AddCreditCardWizard implements Serializable {
	private static final Logger LOG = Logger.getLogger(AddCreditCardWizard.class);
	private static final long serialVersionUID = 1L;

	@Inject
	Conversation conversation;
	
	@Inject @LoggedIn
	Customer customer;
	
	@Inject
	IInventory inventory;
	
	private String creditCardInfo;
	
	private String startView;
	
	private boolean startedConversation = false;
	
	/**
	 * @return
	 */
	public String start() {
		if (conversation.isTransient()) {
			startedConversation = true;
			conversation.begin();
		}
		startView = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		return "addCreditCard";
	}
	
	/**
	 * @return
	 */
	public String end() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (creditCardInfo != null && !creditCardInfo.isEmpty()) {
			customer.getCreditCardInfos().add(creditCardInfo);
			
			try {
				inventory.updateCustomer(customer);
			} catch (NotInDatabaseException_Exception | UpdateException_Exception e) {
				LOG.error("Got exception while updating credit card for " 
							+ customer.getMailAddress() + ": " + e);
			}
			
			context.addMessage(null, new FacesMessage("Credit card added successfully!"));
			if (!conversation.isTransient() && startedConversation) {
				startedConversation = false;
				conversation.end();
			}
		} else {
			context.addMessage(null, new FacesMessage("Could not add the credit card."));
			return "";
		}
		return startView;
	}

	/**
	 * @return
	 */
	public String getCreditCardInfo() {
		return creditCardInfo;
	}

	/**
	 * @param creditCardInfo
	 */
	public void setCreditCardInfo(String creditCardInfo) {
		this.creditCardInfo = creditCardInfo;
	}
}
