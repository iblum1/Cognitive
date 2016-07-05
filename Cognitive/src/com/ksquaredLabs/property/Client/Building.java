package com.ksquaredLabs.property.Client;

import java.util.ArrayList;

import com.ksquaredLabs.property.Address;

public class Building {

	private Address address;
	private ArrayList<Tennant> tennants;
	private ArrayList<Floor> floors;
	private Site site;
	private Client client;
	private double squareFootage;
	private String description;
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public ArrayList<Tennant> getTennants() {
		return tennants;
	}
	
	public void setTennants(ArrayList<Tennant> tennants) {
		this.tennants = tennants;
	}
	
	public ArrayList<Floor> getFloors() {
		return floors;
	}
	
	public void setFloors(ArrayList<Floor> floors) {
		this.floors = floors;
	}
	
	public Site getSite() {
		return site;
	}
	
	public void setSite(Site site) {
		this.site = site;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public double getSquareFootage() {
		return squareFootage;
	}
	
	public void setSquareFootage(double squareFootage) {
		this.squareFootage = squareFootage;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
