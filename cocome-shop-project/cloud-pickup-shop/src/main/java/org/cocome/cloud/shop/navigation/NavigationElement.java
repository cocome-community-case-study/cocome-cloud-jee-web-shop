package org.cocome.cloud.shop.navigation;

/**
 * Represents an element inside an {@link INavigationMenu}.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public class NavigationElement {
	private String navOutcome;
	private String displayText;
	
	public NavigationElement(String navOutcome, String displayText) {
		this.navOutcome = navOutcome;
		this.displayText = displayText;
	}
	
	public String getNavOutcome() {
		return navOutcome;
	}
	
	public void setNavOutcome(String navOutcome) {
		this.navOutcome = navOutcome;
	}
	
	public String getDisplayText() {
		return displayText;
	}
	
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
}
