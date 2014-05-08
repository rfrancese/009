package com.neo.ecopowermapsv1;

public class Location {
	String address;
	String formattedAddress;
	String latitude;
	String longitude;
	String provider;
	String description;
	String jacks;
	String price;
	int numStations;
	
	public Location(String address, String price, String latitude, String longitude) {
		this.address   = address;
		this.price     = price;
		this.latitude  = latitude;
		this.longitude = longitude;
	}
	
	public Location(String formattedAddress, String provider, String jacks, String latitude, String longitude, int numStations, String description) {
		this.formattedAddress = formattedAddress;
		this.provider    = provider;
		this.jacks       = jacks;
		this.latitude    = latitude;
		this.longitude   = longitude;
		this.numStations = numStations;
		this.description = description;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public String getFormattedAddress() {
		return this.formattedAddress;
	}
	
	public String getLatitude() {
		return this.latitude;
	}
	
	public String getLongitude() {
		return this.longitude;
	}
	
	public String getProvider() {
		return this.provider;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getJacks() {
		return this.jacks;
	}
	
	public String getPrice() {
		return this.price;
	}
	
	public int getNumStations() {
		return this.numStations;
	}
}
