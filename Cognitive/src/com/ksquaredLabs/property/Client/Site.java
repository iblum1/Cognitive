package com.ksquaredLabs.property.Client;

import java.util.ArrayList;

import com.ksquaredLabs.property.Address;

public class Site {

	private Address address;
	private ArrayList<Building> buildings;
	private ArrayList<Tennant> tennants;
	private Client client;
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public ArrayList<Building> getBuildings() {
		return buildings;
	}
	
	public void setBuildings(ArrayList<Building> buildings) {
		this.buildings = buildings;
	}
	
	public ArrayList<Tennant> getTennants() {
		return tennants;
	}
	
	public void setTennants(ArrayList<Tennant> tennants) {
		this.tennants = tennants;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
}
