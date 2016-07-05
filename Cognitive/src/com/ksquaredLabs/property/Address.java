package com.ksquaredLabs.property;

public class Address {

	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String zipcode;
	private GeoCoordinate lattitude;
	private GeoCoordinate longitude;
	
	public String getAddressLine1() {
		return addressLine1;
	}
	
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	
	public String getAddressLine2() {
		return addressLine2;
	}
	
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZipcode() {
		return zipcode;
	}
	
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	
	public GeoCoordinate getLattitude() {
		return lattitude;
	}
	
	public void setLattitude(GeoCoordinate lattitude) {
		this.lattitude = lattitude;
	}
	
	public GeoCoordinate getLongitude() {
		return longitude;
	}
	
	public void setLongitude(GeoCoordinate longitude) {
		this.longitude = longitude;
	}
	
}
