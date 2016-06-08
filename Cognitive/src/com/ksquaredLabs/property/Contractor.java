package com.ksquaredLabs.property;

import java.util.ArrayList;
import java.util.Comparator;

import org.bson.BSONObject;

import com.ksquaredLabs.cognitive.NPSInputs.NPSInputType;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Contractor implements Comparable<Contractor> {

	private String name;
	private double costRating;
	private double speedRating;
	private double qualityRating;

	public NPSInputType type;
	
	public Contractor () {
		
	}
	
	public Contractor(BSONObject json) {
		name = json.get("name") + "";
		costRating = Double.parseDouble(json.get("cost") + "");
		speedRating = Double.parseDouble(json.get("speed") + "");
		qualityRating = Double.parseDouble(json.get("quality") + "");
	}
	
	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
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


	public double[] calculateResultOfTicket() {
		double[] results = new double[3];
		results[0] = Util.normalProbability(speedRating);
		results[1] = Util.normalProbability(costRating);
		results[2] = Util.normalProbability(qualityRating);
		return results;
	}

	@Override
	public String toString() {
		return "Contractor [name=" + name + ", costRating=" + costRating + ", speedRating=" + speedRating
				+ ", qualityRating=" + qualityRating + "]";
	}

	public void insetIntoDb(DBCollection coll) {
		BasicDBObject obj = new BasicDBObject("name", name)
				.append("cost", costRating)
				.append("speed", speedRating)
				.append("quality", qualityRating);
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
				costRating = Double.parseDouble(object.get("cost").toString());
			}
			if (object.containsField("speed")) {
				speedRating = Double.parseDouble(object.get("speed").toString());
			}
			if (object.containsField("quality")) {
				qualityRating = Double.parseDouble(object.get("quality").toString());
			}
		} catch (Exception e) {
			cursor.close();
			System.err.println(e);
		}
		
	}
	
	public void updateDb(int index, DBCollection coll) {
		BasicDBObject obj = new BasicDBObject("name", name)
				.append("cost", costRating)
				.append("speed", speedRating)
				.append("quality", qualityRating);
		BasicDBObject query = new BasicDBObject("_id", index);
		coll.update(query, obj);
		
	}
	
	public void removeDb(int index, DBCollection coll) {
		BasicDBObject query = new BasicDBObject("_id", index);
		coll.remove(query);
	}
	
	public static ArrayList<Contractor> getListFromDB(DBCollection coll, BasicDBObject query) {
		ArrayList<Contractor> contractors = new ArrayList<Contractor>();
		DBCursor cursor = query == null?coll.find() : coll.find(query);
		try {
			while (cursor.hasNext()) {
				Contractor contractor = new Contractor(cursor.next());
				contractors.add(contractor);
			}
		} finally {
			cursor.close();
		}
		return contractors;
	}
	
	@Override
	public int compareTo(Contractor o) {
		switch(type) {
		case cost:
			return Double.compare(o.costRating, this.costRating);
		case speed:
			return Double.compare(o.speedRating,  this.speedRating);
		case quality:
			return Double.compare(o.qualityRating, this.qualityRating);
		default:;
			
		}
		return 0;
	}
	
	public static class ContractorComparator implements Comparator<Contractor> {
		
		public NPSInputType type;

		@Override
		public int compare(Contractor o1, Contractor o2) {
			switch(type) {
			case cost:
				return Double.compare(o1.costRating, o2.costRating);
			case speed:
				return Double.compare(o1.speedRating,  o2.speedRating);
			case quality:
				return Double.compare(o1.qualityRating, o2.qualityRating);
			default:;
				
			}
			return 0;
		}
		
	}
}
