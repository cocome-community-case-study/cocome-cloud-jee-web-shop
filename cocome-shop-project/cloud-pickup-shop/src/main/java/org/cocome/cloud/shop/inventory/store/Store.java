package org.cocome.cloud.shop.inventory.store;

/**
 * Holds informtaion about a Store.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public class Store {
	private long id;
	private String name;
	private String location;
	
	public Store(long id, String location, String name) {
		this.id = id;
		this.name = name;
		this.location = location;
	}

	public long getID() {
		return id;
	}
	
	public void setID(long storeID) {
		this.id = storeID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String storeName) {
		this.name = storeName;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String storeLocation) {
		this.location = storeLocation;
	}
	
}
