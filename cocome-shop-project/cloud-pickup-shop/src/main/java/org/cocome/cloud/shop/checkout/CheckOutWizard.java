package org.cocome.cloud.shop.checkout;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.cocome.cloud.external.DebitResult;
import org.cocome.cloud.external.IBankLocal;
import org.cocome.cloud.external.TransactionID;
import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.logic.stub.ProductOutOfStockException_Exception;
import org.cocome.cloud.logic.stub.UpdateException_Exception;
import org.cocome.cloud.shop.customer.UserLogin;
import org.cocome.cloud.shop.inventory.IInventory;
import org.cocome.cloud.shop.inventory.connection.IStoreQuery;
import org.cocome.cloud.shop.shoppingcart.IShoppingCart;

import java.io.Serializable;

/**
 * Implements the wizard to guide the customer through the checkout process. 
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
// TODO implement a generic wizard class and inherit from that
@Named
@ConversationScoped
public class CheckOutWizard implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	Conversation conversation;
	
	@Inject
	Instance<CheckOutDetails> detailsInstance;
	
	CheckOutDetails details;
	
	@Inject
	IInventory inventory;
	
	@Inject
	IBankLocal bank;
	
	@Inject
	IShoppingCart cart;
	
	@Inject
	UserLogin user;
	
	/**
	 * Begins the checkout process by creating a new conversation context 
	 * and redirecting the customer to the checkout site. 
	 * 
	 * @return - the view id string to present to the customer
	 */
	public String start() {
		if (!user.isLoggedIn()) {
			return "login";
		}
		
		if (conversation.isTransient()) {
			conversation.begin();
		}
		return "checkout";
	}
	
	/**
	 * Checks if a credit card was selected and decides which  
	 * view should be presented to the customer as the next step.
	 * If no credit card was selected the customer is notified 
	 * and is redirected to the card selection view. Otherwise 
	 * the customer is presented with the view to enter a pin.  
	 * 
	 * @return - the view id string to present to the customer
	 */
	public String enterPin() {
		if (!user.isLoggedIn()) {
			return "login";
		}
		
		if (details == null) {
			details = detailsInstance.get();
		}
		
		String creditCard = details.getCreditCard(); 
		if (creditCard != null && !creditCard.isEmpty()) {
			return "enterCreditCardPin";
		} else {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Please choose a credit card to proceed."));
			return "checkout";
		}
	}
	
	/**
	 * Checks if a credit card and a pin was entered and decides
	 * which view should be presented to the customer as the next step.
	 * If all information is present, the customer is presented with 
	 * an overview page to review the order.  
	 * 
	 * @return - the view id string to present to the customer
	 */
	public String overviewPage() {
		if (!user.isLoggedIn()) {
			return "login";
		}
		
		if (details == null) {
			details = detailsInstance.get();
		}
		
		String creditCard = details.getCreditCard();
		int pin = details.getCreditCardPin();
		if (creditCard == null || creditCard.isEmpty()) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Please choose a credit card to proceed."));
			return "checkout";
		} else if (pin < 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Please enter a valid pin."));
			return "enterCreditCardPin";
		} else {
			return "checkoutOverview";
		}
	}
	
	/**
	 * Cancels the current checkout process and redirects the customer
	 * to the products view.
	 * 
	 * @return - the view id string to present to the customer
	 */
	public String cancel() {
		return end();
	}

	/**
	 * Calls the {@link IBankLocal} interface to debit the credit card.
	 * If the debit process fails the customer is presented with a 
	 * message about the error and may retry or cancel the process.
	 * If the debit process is successful the items in the shopping 
	 * cart are being processed and the sale is completed.
	 * The shopping card is reset and the customer gets presented with
	 * the products view. 
	 * 
	 * @return - the view id string to present to the customer
	 */
	public String checkOutCart() {
		if (!user.isLoggedIn()) {
			return "login";
		}
		
		details = detailsInstance.get();
		FacesContext context = FacesContext.getCurrentInstance();
		
		TransactionID transaction = bank.validateCard(details.getCreditCard(), details.getCreditCardPin());
		if (transaction == null) {
			context.addMessage(null, new FacesMessage("Could not verify card data, please try again!"));
			return "";
		}
		
		DebitResult result = bank.debitCard(transaction);
		switch (result) {
			case OK:
				if (accountSale()) {
					context.addMessage(null, new FacesMessage("Checkout completed successfully!"));
					cart.reset();
					return end();
				} else {
					context.addMessage(null, new FacesMessage("Could not complete checkout!"));
				}
				break;
			case INSUFFICIENT_BALANCE:
				context.addMessage(null, new FacesMessage("Not enough money on the account!"));
				break;
			case INVALID_TRANSACTION_ID:
				context.addMessage(null, new FacesMessage("Invalid transaction ID, please try again!"));
				break;
			default:
				context.addMessage(null, new FacesMessage("Unknown transaction result!"));
				break;
		}
		return "";
	}
	
	private boolean accountSale() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			return inventory.accountSale(cart);
		} catch (NotInDatabaseException_Exception | ProductOutOfStockException_Exception
				| UpdateException_Exception e) {
			context.addMessage(null, new FacesMessage("An error occured: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Ends this checkout process and the corresponding conversation context.
	 * 
	 * @return - the view id string to present to the customer
	 */
	public String end() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
		return "showStockItems";
	}
}
