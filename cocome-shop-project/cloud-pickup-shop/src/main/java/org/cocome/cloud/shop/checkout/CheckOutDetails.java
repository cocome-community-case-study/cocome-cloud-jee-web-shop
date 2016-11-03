package org.cocome.cloud.shop.checkout;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.cocome.cloud.shop.customer.Customer;
import org.cocome.cloud.shop.customer.LoggedIn;
import org.cocome.cloud.shop.shoppingcart.IShoppingCart;

import java.io.Serializable;
import java.util.Set;
/**
 * Holds information on the credit card and pin for the checkout process.
 * A customer has to be logged in before using this, otherwise there will be an injection error.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@ConversationScoped
public class CheckOutDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(CheckOutDetails.class);
	
	@Inject @LoggedIn
	Customer customer;
	
	@Inject
	IShoppingCart cart;
	
	private String creditCard;
	private int creditCardPin;
	
	/**
	 * Retrieves the currently stored credit card string for a checkout.
	 * May return null if no credit card is set.
	 * 
	 * @return the credit card string
	 */
	public String getCreditCard() {
		return creditCard;
	}
	
	/**
	 * Sets the stored credit card string for this checkout.
	 * 
	 * @param creditCard - the credit card used for this checkout
	 */
	public void setCreditCard(String creditCard) {
		LOG.debug("Credit card was set to " + creditCard);
		this.creditCard = creditCard;
	}
	
	/**
	 * Retrieves the pin to validate the credit card for this checkout process.
	 * 
	 * @return the credit card pin
	 */
	public int getCreditCardPin() {
		return creditCardPin;
	}
	
	/**
	 * Sets the credit card pin to validate the selected credit card 
	 * for this checkout process.
	 * 
	 * @param creditCardPin - the pin for the selected card
	 */
	public void setCreditCardPin(int creditCardPin) {
		LOG.debug("Pin was set to " + creditCardPin);
		this.creditCardPin = creditCardPin;
	}
	
	/**
	 * Retrieves all available credit cards for this costumer to 
	 * choose from. This set can be empty if no credit cards were 
	 * added by the customer. 
	 * 
	 * @return - the set of credit cards the customer added before
	 */
	public Set<String> getCreditCardInfo() {
		return customer.getCreditCardInfos();
	}
	
	/**
	 * Retrieves the current shopping cart.
	 * 
	 * @return - the shopping cart
	 */
	public IShoppingCart getShoppingCart() {
		return cart;
	}
}
