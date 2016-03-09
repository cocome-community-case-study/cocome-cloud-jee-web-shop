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
	
	public String getCreditCard() {
		return creditCard;
	}
	
	public void setCreditCard(String creditCard) {
		LOG.debug("Credit card was set to " + creditCard);
		this.creditCard = creditCard;
	}
	
	public int getCreditCardPin() {
		return creditCardPin;
	}
	
	public void setCreditCardPin(int creditCardPin) {
		LOG.debug("Pin was set to " + creditCardPin);
		this.creditCardPin = creditCardPin;
	}
	
	public Set<String> getCreditCardInfo() {
		return customer.getCreditCardInfos();
	}
	
	public IShoppingCart getShoppingCart() {
		return cart;
	}
}
