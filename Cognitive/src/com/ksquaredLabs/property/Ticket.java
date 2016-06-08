package com.ksquaredLabs.property;

public class Ticket {

	private Client client;
	private Contractor contractor;
	private double costResult;
	private double speedResult;
	private double qualityResult;
	private double resultRating;
	
	
	public Ticket() {
		
	}
	
	public Ticket(Client client, Contractor contractor) {
		this.client = client;
		this.contractor = contractor;
	}
	
	public void processTicket() {
		double[] results = contractor.calculateResultOfTicket();
		speedResult = results[0];
		costResult = results[1];
		qualityResult = results[2];
		System.out.format("Ticket Results are %.2f, %.2f, %.2f\n", speedResult, costResult, qualityResult);
		resultRating = client.calculateRating(costResult, speedResult, qualityResult);
		
	}
	
	public double getResultRating() {
		return resultRating;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Contractor getContractor() {
		return contractor;
	}

	public void setContractor(Contractor contractor) {
		this.contractor = contractor;
	}

	public double getCostResult() {
		return costResult;
	}

	public double getSpeedResult() {
		return speedResult;
	}

	public double getQualityResult() {
		return qualityResult;
	}
	
	
}
