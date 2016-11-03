package org.cocome.cloud.shop.navigation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Implements the navigation menu for the site. Default elements in this menu  
 * are the links for the product overview and the selection of a shop.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@ApplicationScoped
public class NavigationMenu implements INavigationMenu {
	private LinkedHashMap<String, NavigationElement> elements = new LinkedHashMap<String, NavigationElement>();
	private ResourceBundle strings;
	
	@PostConstruct
	private void initResourceBundle() {
		FacesContext ctx = FacesContext.getCurrentInstance();
	    Locale locale = ctx.getViewRoot().getLocale();

		strings = ResourceBundle.getBundle(
	            "cocome.cloud.pickupshop.Strings", locale);
	}
	
	/* (non-Javadoc)
	 * @see org.cocome.cloud.shop.navigation.INavigationMenu#getElements()
	 */
	@Override
	public List<NavigationElement> getElements() {
		if (elements.isEmpty()) {
			elements.put("showStockItems", new NavigationElement("showStockItems", strings.getString("homeNavText")));
			elements.put("shopSelect", new NavigationElement("shopSelect", strings.getString("selectStoreNavText")));
		}
		return new ArrayList<NavigationElement>(elements.values());
	}
	
	/* (non-Javadoc)
	 * @see org.cocome.cloud.shop.navigation.INavigationMenu#removeElement(java.lang.String)
	 */
	@Override
	public void removeElement(String viewID) {
		elements.remove(viewID);
	}
	
	/* (non-Javadoc)
	 * @see org.cocome.cloud.shop.navigation.INavigationMenu#addElement(java.lang.String, org.cocome.cloud.shop.navigation.NavigationElement)
	 */
	@Override
	public void addElement(String viewID, NavigationElement element) {
		elements.put(viewID, element);
	}
}
