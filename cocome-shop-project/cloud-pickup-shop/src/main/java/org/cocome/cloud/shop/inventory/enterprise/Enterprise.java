package org.cocome.cloud.shop.inventory.enterprise;

/**
 * Holds information on an enterprise.
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public class Enterprise {
	private long id;
	private String name;
	
	public Enterprise() {
		id = 0;
		name = "N/A";
	}
	
	public Enterprise(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}
