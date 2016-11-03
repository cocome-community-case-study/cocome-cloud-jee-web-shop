package org.cocome.cloud.shop.navigation;

import java.util.Collection;

/**
 * Interface representing the navigation menu on the site. 
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface INavigationMenu {

	Collection<NavigationElement> getElements();

	void removeElement(String viewID);

	void addElement(String viewID, NavigationElement element);

}