package com.ksquaredLabs.property;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

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
	private ArrayList<Ticket> myTickets;

	public NPSInputType type;
	
	public Contractor () {
		
	}
	
	public Contractor(BSONObject json) {
		BasicBSONObject obj = (BasicBSONObject) json;
		name = obj.getString("name");
		if (obj.containsField("cost")) {
			costRating = obj.getDouble("cost");
		}
		if (obj.containsField("speed")) {
			speedRating = obj.getDouble("speed");
		}
		if (obj.containsField("quality")) {
			qualityRating = obj.getDouble("quality");
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
		for (Ticket checkTicket : myTickets) {
			Calendar c = Calendar.getInstance();
			c.setTime(checkTicket.getScheduleDate());
			c.add(Calendar.DATE, -1);
			Date checkStart = c.getTime();
			c = Calendar.getInstance();
			c.setTime(checkStart);
			c.add(Calendar.DATE, checkTicket.getDuration()+1);
			Date checkEnd = c.getTime();
			Date ticketStart = ticket.getScheduleDate();
			c = Calendar.getInstance();
			c.setTime(ticketStart);
			c.add(Calendar.DATE, ticket.getDuration());
			Date ticketEnd = c.getTime();
			System.out.format("Contractor name %s Dates Start %tD < %tD < %tD, %s End %tD < %tD < %tD, %s\n",
					name,
					checkStart, ticketStart, checkEnd, (ticketStart.after(checkStart) && ticketStart.before(checkEnd)) + "", 
					checkStart, ticketEnd, checkEnd, (ticketEnd.after(checkStart) && ticketEnd.before(checkEnd)) + "");
			if ((ticketStart.after(checkStart) && ticketStart.before(checkEnd)) || 
					(ticketEnd.after(checkStart) && ticketEnd.before(checkEnd))) {
				return false;
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
		return "Contractor [name=" + name + ", costRating=" + costRating + ", speedRating=" + speedRating
				+ ", qualityRating=" + qualityRating + "]\n";
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
	
	public BasicDBObject getDBObject() {
		BasicDBObject obj = new BasicDBObject("name", name)
				.append("costRate", costRating)
				.append("speedRate", speedRating)
				.append("qualityRate", qualityRating);
		return obj;
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
