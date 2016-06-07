package com.ksquaredLabs.property;

import java.util.ArrayList;
import java.util.Collections;

import com.ksquaredLabs.cognitive.NPSInputs.NPSInputType;
import com.ksquaredLabs.property.Contractor.ContractorComparator;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class ContractorPicker {

	private Ticket ticket;

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
	
	public Contractor pickContractor(DB db) {
		Client client = ticket.getClient();
		if (client.getCostFactor() > client.getSpeedFactor() && client.getCostFactor() > client.getQualityFactor()) {
			// pick cheap contractor
			return pickContractorByRating(NPSInputType.cost, true, db);
		} else if (client.getSpeedFactor() > client.getCostFactor() && client.getSpeedFactor() > client.getQualityFactor()) {
			// pick fast contractor
			return pickContractorByRating(NPSInputType.speed, true, db);
		} else if (client.getQualityFactor() > client.getSpeedFactor() && client.getCostFactor() < client.getQualityFactor()) {
			// pick quality contractor
			return pickContractorByRating(NPSInputType.quality, true, db);
		} if (client.getCostFactor() < client.getSpeedFactor() && client.getCostFactor() < client.getQualityFactor()) {
			// pick expensive contractor
			return pickContractorByRating(NPSInputType.cost, false, db);
		} else if (client.getSpeedFactor() < client.getCostFactor() && client.getSpeedFactor() < client.getQualityFactor()) {
			// pick slow contractor
			return pickContractorByRating(NPSInputType.speed, false, db);
		} else if (client.getQualityFactor() < client.getSpeedFactor() && client.getCostFactor() > client.getQualityFactor()) {
			// pick quality contractor
			return pickContractorByRating(NPSInputType.quality, false, db);
		} else {
			// pick average contractor
			return pickAverageContractor(db);
		}
		
	}
	
	public Contractor pickContractorByRating(NPSInputType type,boolean good, DB db) {
		DBCollection coll = db.getCollection("contractor");
		ArrayList<Contractor> contractors = Contractor.getListFromDB(coll, null);
		if (contractors.size() == 0) return null;
		ContractorComparator comparator = new ContractorComparator();
		comparator.type = type;
		Collections.sort(contractors, comparator);
//		System.out.println("Ordered by: " + type + " Contractors: " + contractors);
		if (!good) {
			return contractors.get(0);
		} else {
			return contractors.get(contractors.size()-1);
		}
	}
	
	public Contractor pickAverageContractor(DB db) {
		DBCollection coll = db.getCollection("contractor");
		ArrayList<Contractor> contractors = Contractor.getListFromDB(coll, null);
		if (contractors.size() == 0) return null;
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
		
		Contractor averageContractor = null;
		double distancefromAverageValue = 1000.0;
		for (Contractor contractor : contractors) {
			double thisDistancfromAverageValue = Math.abs(averageCost - contractor.getCostRating()) + 
					Math.abs(averageSpeed - contractor.getSpeedRating()) +
					Math.abs(averageQuality - contractor.getQualityRating());
			if (thisDistancfromAverageValue < distancefromAverageValue) {
				averageContractor = contractor;
				distancefromAverageValue = thisDistancfromAverageValue;
			}
		}
		
		return averageContractor;
	}
}
