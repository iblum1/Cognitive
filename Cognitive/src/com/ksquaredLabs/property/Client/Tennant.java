package com.ksquaredLabs.property.Client;

import java.util.ArrayList;

import com.ksquaredLabs.property.Address;
import com.ksquaredLabs.property.Ticket;

public class Tennant {

	private Client client;
	private Floor floor;
	private Site site;
	private Building building;
	private ArrayList<Ticket> tickets;
	private Address address;
	
	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}
	
	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}
	
	/**
	 * @return the floor
	 */
	public Floor getFloor() {
		return floor;
	}
	
	/**
	 * @param floor the floor to set
	 */
	public void setFloor(Floor floor) {
		this.floor = floor;
	}
	
	/**
	 * @return the site
	 */
	public Site getSite() {
		return site;
	}
	
	/**
	 * @param site the site to set
	 */
	public void setSite(Site site) {
		this.site = site;
	}
	
	/**
	 * @return the building
	 */
	public Building getBuilding() {
		return building;
	}
	
	/**
	 * @param building the building to set
	 */
	public void setBuilding(Building building) {
		this.building = building;
	}
	
	/**
	 * @return the tickets
	 */
	public ArrayList<Ticket> getTickets() {
		return tickets;
	}
	
	/**
	 * @param tickets the tickets to set
	 */
	public void setTickets(ArrayList<Ticket> tickets) {
		this.tickets = tickets;
	}
	
	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}
	
	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}
	
}
