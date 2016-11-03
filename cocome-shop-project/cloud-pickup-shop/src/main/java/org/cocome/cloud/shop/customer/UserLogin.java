package org.cocome.cloud.shop.customer;

import java.io.Serializable;
import java.security.Principal;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.cocome.cloud.shop.navigation.NavigationElement;
import org.cocome.cloud.shop.navigation.NavigationMenu;

/**
 * Handles the login process for a user and holds the necessary information. 
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@SessionScoped
public class UserLogin implements Serializable {
	private static final long serialVersionUID = 5259814409696562712L;
	
	private String name;
	private String password;
	
	@Inject
	NavigationMenu navMenu;
	
	@Inject @LoggedIn
	Instance<Customer> customerInstance;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		if (request != null) {
			Principal userPrincipal = request.getUserPrincipal();
			if (userPrincipal != null) {
				return String.format("%s", userPrincipal.getName());
			}
		}
		return "You are not currently logged in";
	}

	public boolean isLoggedIn() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		if (request != null) {
			Principal userPrincipal = request.getUserPrincipal();
			return (userPrincipal != null);
		}
		return false;
	}

	public String login() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		try {
			request.login(this.name, this.password);
		} catch (ServletException e) {
			context.addMessage(null, new FacesMessage("Login failed."));
			return "error";
		}
		
		addMyAccountButton();
		return "myAccount";
	}

	private void addMyAccountButton() {
		FacesContext ctx = FacesContext.getCurrentInstance();
	    Locale locale = ctx.getViewRoot().getLocale();

		ResourceBundle strings = ResourceBundle.getBundle(
	            "cocome.cloud.pickupshop.Strings", locale);
		
		navMenu.addElement("myAccount", new NavigationElement("myAccount", strings.getString("myAccountButtonText")));
	}

	public String logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		try {
			request.logout();
			navMenu.removeElement("myAccount");
			context.addMessage(null, new FacesMessage("Logout successful."));
			return "showStockItems";
		} catch (ServletException e) {
			context.addMessage(null, new FacesMessage("Logout failed."));
		}
		
		return "showStockItems";
	}
}
