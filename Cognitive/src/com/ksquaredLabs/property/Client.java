package com.ksquaredLabs.property;

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
	
	public Client() {
		
	}
	
	public Client(BSONObject json) {
		name = json.get("name") + "";
		costFactor = Double.parseDouble(json.get("cost") + "");
		speedFactor = Double.parseDouble(json.get("speed") + "");
		qualityFactor = Double.parseDouble(json.get("quality") + "");
		if (json.get("costRate") != null) costFactor = Double.parseDouble(json.get("costRate") + "");
		if (json.get("speedRate") != null) speedFactor = Double.parseDouble(json.get("speedRate") + "");
		if (json.get("qualityRate") != null) qualityFactor = Double.parseDouble(json.get("qualityRate") + "");
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

	
	public String toJson() {
		return "client : {\"name\" = \"" + name + "\", \"costFactor\" = " + costFactor + ", \"speedFactor\" = " + speedFactor + ", \"qualityFactor\" = " + 
				qualityFactor + ", \"costRating\" = " + costRating + ", \"speedRating\" = " + speedRating + ", \"qualityRating\" = " + qualityRating + "}"; 
	}
	
	@Override
	public String toString() {
		return "Client [name=" + name + ", costFactor=" + costFactor + ", speedFactor=" + speedFactor
				+ ", qualityFactor=" + qualityFactor + ", costRating=" + costRating + ", speedRating=" + speedRating
				+ ", qualityRating=" + qualityRating + "]";
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
	
	public void insetIntoDb(DBCollection coll) {
		BasicDBObject obj = new BasicDBObject("name", name)
				.append("cost", costFactor)
				.append("speed", speedFactor)
				.append("quality", qualityFactor);
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
		} catch (Exception e) {
			cursor.close();
			System.err.println(e);
		}
		
	}
	
	public void updateDb(int index, DBCollection coll) {
		BasicDBObject obj = getDBObject();
		BasicDBObject query = new BasicDBObject("_id", index);
		coll.update(query, obj);
		
	}
	
	public BasicDBObject getDBObject() {
		BasicDBObject obj = new BasicDBObject("name", name)
				.append("cost", costFactor)
				.append("speed", speedFactor)
				.append("quality", qualityFactor)
				.append("costRate", costRating)
				.append("speedRate", speedRating)
				.append("qualityRate", qualityRating);
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
