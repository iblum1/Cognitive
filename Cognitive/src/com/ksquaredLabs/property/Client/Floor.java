package com.ksquaredLabs.property.Client;

import java.util.ArrayList;

public class Floor {

	private ArrayList<Tennant> tennants;
	private Client client;
	private Building building;
	private Site site;
	private double squareFootage;
	
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
	
	public Building getBuilding() {
		return building;
	}
	
	public void setBuilding(Building building) {
		this.building = building;
	}
	
	public Site getSite() {
		return site;
	}
	
	public void setSite(Site site) {
		this.site = site;
	}
	
	public double getSquareFootage() {
		return squareFootage;
	}
	
	public void setSquareFootage(double squareFootage) {
		this.squareFootage = squareFootage;
	}
	
}
