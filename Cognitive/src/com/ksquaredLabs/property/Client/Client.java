package com.ksquaredLabs.property.Client;

import java.util.ArrayList;

import org.bson.BSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Client {

	private String name;
	private double costFactor;
	private double speedFactor;
	private double qualityFactor;
	private double costRating = 7.0;
	private double speedRating = 7.0;
	private double qualityRating = 7.0;
	private double yearlySpend = 0.0;
	private int numberOfTickets = 0;
	
	public Client() {
		
	}
	
	public Client(BSONObject json) {
		name = json.get("name") + "";
		costFactor = Double.parseDouble(json.get("cost") + "");
		speedFactor = Double.parseDouble(json.get("speed") + "");
		qualityFactor = Double.parseDouble(json.get("quality") + "");
		yearlySpend = Double.parseDouble(json.get("yearlySpend") + "");
		numberOfTickets = Integer.parseInt(json.get("tickets") + "");
		if (json.get("costRate") != null) costFactor = Double.parseDouble(json.get("costRate") + "");
		if (json.get("speedRate") != null) speedFactor = Double.parseDouble(json.get("speedRate") + "");
		if (json.get("qualityRate") != null) qualityFactor = Double.parseDouble(json.get("qualityRate") + "");
		if (json.get("yearlySpend") != null) yearlySpend = Double.parseDouble(json.get("yearlySpend") + "");
		if (json.get("tickets") != null) numberOfTickets = Integer.parseInt(json.get("tickets") + "");
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getCostFactor() {
		return costFactor;
	}
	
	public void setCostFactor(double costFactor) {
		this.costFactor = costFactor;
	}
	
	public double getSpeedFactor() {
		return speedFactor;
	}
	
	public void setSpeedFactor(double speedFactor) {
		this.speedFactor = speedFactor;
	}
	
	public double getQualityFactor() {
		return qualityFactor;
	}
	
	public void setQualityFactor(double qualityFactor) {
		this.qualityFactor = qualityFactor;
	}
	
	public double getCostRating() {
		return costRating;
	}

	public void setCostRating(double costRating) {
		this.costRating = costRating;
	}

	public double getSpeedRating() {
		return speedRating;
	}

	public void setSpeedRating(double speedRating) {
		this.speedRating = speedRating;
	}

	public double getQualityRating() {
		return qualityRating;
	}

	public void setQualityRating(double qualityRating) {
		this.qualityRating = qualityRating;
	}
	
	public double getYearlySpend() {
		return yearlySpend;
	}

	public void setYearlySpend(double yearlySpend) {
		this.yearlySpend = yearlySpend;
	}

	public int getNumberOfTickets() {
		return numberOfTickets;
	}

	public void setNumberOfTickets(int numberOfTickets) {
		this.numberOfTickets = numberOfTickets;
	}

	public String toJson() {
		return "client : {\"name\" = \"" + name + "\", \"costFactor\" = " + costFactor + ", \"speedFactor\" = " + speedFactor + ", \"qualityFactor\" = " + 
				qualityFactor + ", \"costRating\" = " + costRating + ", \"speedRating\" = " + speedRating + ", \"qualityRating\" = " + qualityRating + 
				", \"yearlySpend\" = " + yearlySpend + ", \"tickets\" = " + numberOfTickets +"}"; 
	}
	
	@Override
	public String toString() {
		return String.format("Client [name=%s, costFactor=%.2f, speedFactor=%.2f, qualityFactor=%.2f, costRating=%.2f, speedRating=%.2f, qualityRating=%.2f, "
				+ "yearlySpend=%.2f, numberOfTickets=%d]", 
				name, costFactor, speedFactor, qualityFactor, costRating, speedRating, qualityRating, yearlySpend, numberOfTickets);
	}

	public double calculateRating(double cost, double speed, double quality) {
		costRating = cost * costFactor;
		speedRating = speed * speedFactor;
		qualityRating = quality * qualityFactor;
		double result = costRating + speedRating + qualityRating;
		result = result / (costFactor + speedFactor + qualityFactor) ;
//		System.out.format("input: speed %.2f, cost %.2f, quality %.2f \nfactor: speed %.2f, cost %.2f, quality %.2f;  result: %.2f\n", 
//				speed, cost, quality, speedFactor, costFactor, qualityFactor, result);
		return result;
	}
	
	public void updateYearlySpend(double cost) {
		yearlySpend += cost;
	}
	
	public void incrementTickets() {
		numberOfTickets++;
	}
	
	public void insetIntoDb(DBCollection coll) {
		BasicDBObject obj = new BasicDBObject("name", name)
				.append("cost", costFactor)
				.append("speed", speedFactor)
				.append("quality", qualityFactor)
				.append("yearlySpend", yearlySpend)
				.append("tickets", numberOfTickets);
		coll.insert(obj);
	}
	
	public void getFromDb(int index, DBCollection coll) {
		BasicDBObject query = new BasicDBObject("_id", index);
		DBCursor cursor = coll.find(query);
		try {
			DBObject object = cursor.next();
			if (object.containsField("name")) {
				name = object.get("name").toString();
			}
			if (object.containsField("cost")) {
				costFactor = Double.parseDouble(object.get("cost").toString());
			}
			if (object.containsField("speed")) {
				speedFactor = Double.parseDouble(object.get("speed").toString());
			}
			if (object.containsField("quality")) {
				qualityFactor = Double.parseDouble(object.get("quality").toString());
			}
			if (object.containsField("costRate")) {
				costRating = Double.parseDouble(object.get("costRate").toString());
			}
			if (object.containsField("speedRate")) {
				speedRating = Double.parseDouble(object.get("speedRate").toString());
			}
			if (object.containsField("qualityRate")) {
				qualityRating = Double.parseDouble(object.get("qualityRate").toString());
			}
			if (object.containsField("yearlySpend")) {
				yearlySpend = Double.parseDouble(object.get("yearlySpend").toString());
			}
			if (object.containsField("tickets")) {
				numberOfTickets = Integer.parseInt(object.get("tickets").toString());
			}
		} catch (Exception e) {
			cursor.close();
			System.err.println(e);
		}
		
	}
	
	public void updateDb(DBCollection coll) {
		BasicDBObject obj = getDBObject();
		BasicDBObject query = new BasicDBObject("name", name);
		DBObject dbObject = coll.findAndModify(query, obj);
		
	}
	
	public BasicDBObject getDBObject() {
		BasicDBObject obj = new BasicDBObject("name", name)
				.append("cost", costFactor)
				.append("speed", speedFactor)
				.append("quality", qualityFactor)
				.append("costRate", costRating)
				.append("speedRate", speedRating)
				.append("qualityRate", qualityRating)
				.append("yearlySpend", yearlySpend)
				.append("tickets", numberOfTickets);
		return obj;
	}
	
	public void removeDb(int index, DBCollection coll) {
		BasicDBObject query = new BasicDBObject("_id", index);
		coll.remove(query);
	}
	
	public static ArrayList<Client> getListFromDB(DBCollection coll, BasicDBObject query) {
		ArrayList<Client> clients = new ArrayList<Client>();
		DBCursor cursor = query == null?coll.find() : coll.find(query);
		try {
			while (cursor.hasNext()) {
				Client client = new Client(cursor.next());
				clients.add(client);
			}
		} finally {
			cursor.close();
		}
		return clients;
	}
}
