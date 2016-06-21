package com.ksquaredLabs.property;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;

import com.ksquaredLabs.cognitive.NPSInputs.NPSInputType;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Contractor implements Comparable<Contractor> {

	private String name;
	private ObjectId index;
	private double costRating;
	private double speedRating;
	private double qualityRating;
	private ArrayList<Ticket> myTickets;

	public NPSInputType type;
	
	public Contractor () {
		
	}
	
	public Contractor(BSONObject json) {
		BasicBSONObject obj = (BasicBSONObject) json;
		name = obj.getString("name");
		if (obj.containsField("costRate")) {
			costRating = obj.getDouble("costRate");
		}
		if (obj.containsField("speedRate")) {
			speedRating = obj.getDouble("speedRate");
		}
		if (obj.containsField("qualityRate")) {
			qualityRating = obj.getDouble("qualityRate");
		}
		if (obj.containsField("tickets")) {
			BasicDBList list = (BasicDBList) obj.get("tickets"); 
			if (list != null) {
				ArrayList<BasicDBObject> thisList = (ArrayList) list;
				myTickets = new ArrayList();
				for (BasicDBObject object : thisList) {
					Ticket ticket = new Ticket(object);
					myTickets.add(ticket);
				}
			}
		}
		if (obj.containsField("_id")) {
			index = obj.getObjectId("_id");
		}
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
	
	public ArrayList<Ticket> getMyTickets() {
		return myTickets;
	}
	
	
	public boolean available(Ticket ticket) {
		if (myTickets == null) myTickets = new ArrayList<Ticket>();
		for (int i = 0; i < ticket.getDatesOfWork().length; i++) {
			Date ticketDate = ticket.getDatesOfWork()[i];
			for (Ticket checkTicket : myTickets) {
				for(int j = 0; j < checkTicket.getDatesOfWork().length; j++) {
					Date checkDate = checkTicket.getDatesOfWork()[j];
//					System.out.format("checkDate %tD, ticketDate %tD\n", checkDate, ticketDate);
					if (checkDate.equals(ticketDate)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public void scheduleTicket(Ticket ticket) {
		if (myTickets == null) myTickets = new ArrayList<Ticket>();
		myTickets.add(ticket);
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
		return "Contractor [index =" + index + ", name=" + name + ", costRating=" + costRating + ", speedRating=" + speedRating
				+ ", qualityRating=" + qualityRating + ", tickets " + myTickets + "]\n";
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contractor other = (Contractor) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void insetIntoDb(DBCollection coll) {
		BasicDBObject obj = getDBObject();
		coll.insert(obj);
	}
	
	public void getFromDb(int index, DBCollection coll) {
		BasicDBObject query = new BasicDBObject("_id", index);
		DBCursor cursor = coll.find(query);
		try {
			BasicDBObject object = (BasicDBObject) cursor.next();
			if (object.containsField("name")) {
				name = object.get("name").toString();
			}
			if (object.containsField("costRate")) {
				costRating = object.getDouble("costRate");
			}
			if (object.containsField("speedRate")) {
				speedRating = object.getDouble("speedRate");
			}
			if (object.containsField("qualityRate")) {
				qualityRating = object.getDouble("qualityRate");
			}
			if (object.containsField("tickets")) {
				BasicDBList list = (BasicDBList) object.get("tickets"); 
				myTickets = (ArrayList) list;
			}
			if (object.containsField("_id")) {
				index = object.getInt("_id");
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
//		System.out.println("query is " + query + ", current object is " + dbObject + ", new object is " + obj);
		
	}
	
	public BasicDBObject getDBObject() {
		BasicDBObject obj = new BasicDBObject("name", name)
				.append("costRate", Double.valueOf(costRating))
				.append("speedRate", Double.valueOf(speedRating))
				.append("qualityRate", Double.valueOf(qualityRating))
				.append("tickets", Ticket.toDBList(myTickets));
		return obj;
	}
	
	public void removeDb(DBCollection coll) {
		BasicDBObject query = new BasicDBObject("name", name);
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
		public double averageCost;
		public double averageSpeed;
		public double averageQuality;

		@Override
		public int compare(Contractor o1, Contractor o2) {
			switch(type) {
			case cost:
				return Double.compare(o1.costRating, o2.costRating);
			case speed:
				return Double.compare(o1.speedRating,  o2.speedRating);
			case quality:
				return Double.compare(o1.qualityRating, o2.qualityRating);
			case average:
				double average1 = Math.abs(o1.costRating - averageCost) + 
						Math.abs(o1.speedRating - averageSpeed) +
						Math.abs(o1.qualityRating - averageQuality);
				double average2 = Math.abs(averageCost - o2.costRating) + 
						Math.abs(averageSpeed - o2.speedRating) +
						Math.abs(averageQuality - o2.qualityRating);
				return Double.compare(average1, average2);
			default:;
				
			}
			return 0;
		}
		
	}
}
