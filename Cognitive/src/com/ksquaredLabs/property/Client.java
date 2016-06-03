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
	
	public Client(BSONObject json) {
		name = json.get("name") + "";
		costFactor = Double.parseDouble(json.get("cost") + "");
		speedFactor = Double.parseDouble(json.get("speed") + "");
		qualityFactor = Double.parseDouble(json.get("quality") + "");
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
	
	@Override
	public String toString() {
		return "Client [name=" + name + ", costFactor=" + costFactor + ", speedFactor=" + speedFactor
				+ ", qualityFactor=" + qualityFactor + "]";
	}
	
	
	public double calculateRating(double cost, double speed, double quality) {
		double result = cost * costFactor + speed * speedFactor + quality * qualityFactor;
		result = result / (costFactor + speedFactor + qualityFactor) ;
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
		} catch (Exception e) {
			cursor.close();
			System.err.println(e);
		}
		
	}
	
	public void updateDb(int index, DBCollection coll) {
		BasicDBObject obj = new BasicDBObject("name", name)
				.append("cost", costFactor)
				.append("speed", speedFactor)
				.append("quality", qualityFactor);
		BasicDBObject query = new BasicDBObject("_id", index);
		coll.update(query, obj);
		
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
