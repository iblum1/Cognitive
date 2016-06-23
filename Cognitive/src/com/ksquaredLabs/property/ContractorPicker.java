package com.ksquaredLabs.property;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.ksquaredLabs.cognitive.NPSInputs.NPSInputType;
import com.ksquaredLabs.property.Contractor.ContractorComparator;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class ContractorPicker {

	private static ArrayList<Contractor> contractors;
	private static ArrayList<Ticket> tickets;
	private Ticket ticket;
	
	public ContractorPicker() {
		contractors = null;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
	
	public Contractor pickContractor(DB db) {
		if (contractors == null) {
			DBCollection coll = db.getCollection("contractor");
			contractors = Contractor.getListFromDB(coll, null);
		}
		DBCollection coll = db.getCollection("ticket");
		tickets = Ticket.getListFromDB(coll, null);
		Client client = ticket.getClient();
		Contractor contractor = null;
		System.out.format("client %s, cost %.2f speed %.2f quality %.2f\n", client.getName(), client.getCostFactor(), client.getSpeedFactor(), client.getQualityFactor());
		if (client.getCostFactor() > client.getSpeedFactor() && client.getCostFactor() > client.getQualityFactor()) {
			// pick cheap contractor
			contractor = pickContractorByRating(NPSInputType.cost, true, ticket);
		} else if (client.getSpeedFactor() > client.getCostFactor() && client.getSpeedFactor() > client.getQualityFactor()) {
			// pick fast contractor
			contractor = pickContractorByRating(NPSInputType.speed, true, ticket);
		} else if (client.getQualityFactor() > client.getSpeedFactor() && client.getCostFactor() < client.getQualityFactor()) {
			// pick quality contractor
			contractor = pickContractorByRating(NPSInputType.quality, true, ticket);
		} else if (client.getCostFactor() < client.getSpeedFactor() && client.getCostFactor() < client.getQualityFactor()) {
			// pick expensive contractor
			contractor = pickContractorByRating(NPSInputType.cost, false, ticket);
		} else if (client.getSpeedFactor() < client.getCostFactor() && client.getSpeedFactor() < client.getQualityFactor()) {
			// pick slow contractor
			contractor = pickContractorByRating(NPSInputType.speed, false, ticket);
		} else if (client.getQualityFactor() < client.getSpeedFactor() && client.getCostFactor() > client.getQualityFactor()) {
			// pick quality contractor
			contractor = pickContractorByRating(NPSInputType.quality, false, ticket);
		} else {
			// pick average contractor
			contractor = pickAverageContractor(ticket);
		}
		if (contractor != null) {
			contractor.getMyTickets().add(ticket);
			System.out.println("Contractor added is " + contractor.getName());
			contractor.updateDb(db.getCollection("contractor"));
		} else {
			ticket.push();
			return pickContractor(db);
		}
		return contractor;
	}
	
	public Contractor pickContractorByRating(NPSInputType type,boolean good, Ticket ticket) {
		System.out.format("Input type %s, forward %s, by rating\n", type, good);
		if (contractors.size() == 0) return null;
		ContractorComparator comparator = new ContractorComparator();
		comparator.type = type;
		Collections.sort(contractors, comparator);
//		System.out.println("Sorted contractors are " + contractors);
		int contractorOrdinal = pickContractorIdByDate(good, ticket);
		if (contractorOrdinal > -1)
			return contractors.get(contractorOrdinal);
		else return null;
	}
	
	private int pickContractorIdByDate(boolean good, Ticket ticket) {
		int contractorOrdinal = 0;
		boolean found = false;
		if (!good) {
			contractorOrdinal = 0;
			while (!found && contractorOrdinal < contractors.size()-1) {
				found = contractors.get(contractorOrdinal).available(ticket);
				if (!found) {
					contractorOrdinal++;
				}
				System.out.format("Found %s %d\n", found + "", contractorOrdinal);
			}
		} else {
			contractorOrdinal = contractors.size() - 1;
			while (!found && contractorOrdinal > 0) {
				found = contractors.get(contractorOrdinal).available(ticket);
				if (!found) {
					contractorOrdinal--;
				}
			}
			System.out.format("Found %s %d\n", found + "", contractorOrdinal);
			
		}
		if (!found) return -1;
		return contractorOrdinal;
	}
	
	public Contractor pickAverageContractor(Ticket ticket) {
		System.out.println("Input Type: Average");
		if (contractors.size() == 0) return null;
		ContractorComparator comparator = new ContractorComparator();
		comparator.type = NPSInputType.average;
		double averageCost = 0.0;
		double averageSpeed = 0.0;
		double averageQuality = 0.0;
		for (Contractor contractor : contractors) {
			averageCost += contractor.getCostRating();
			averageSpeed += contractor.getSpeedRating();
			averageQuality += contractor.getQualityRating();
		}
		averageCost = averageCost / (double) contractors.size();
		averageSpeed = averageSpeed / (double) contractors.size();
		averageQuality = averageQuality / (double) contractors.size();
		comparator.averageCost = averageCost;
		comparator.averageSpeed = averageSpeed;
		comparator.averageQuality = averageQuality;
		Collections.sort(contractors,comparator);
//		System.out.println("Sorted contractors are " + contractors);
		
		
		int contractorOrdinal = pickContractorIdByDate(false, ticket);
		if (contractorOrdinal > -1)
			return contractors.get(contractorOrdinal);
		else return null;
	}
}
