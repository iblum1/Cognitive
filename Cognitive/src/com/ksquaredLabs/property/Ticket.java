package com.ksquaredLabs.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Ticket {

	private Client client;
	private Contractor contractor;
	private Date scheduleDate;
	private int duration;
	private double costResult;
	private double speedResult;
	private double qualityResult;
	private double resultRating;
	private Date[] datesOfWork;
	
	
	public Ticket() {
		
	}
	
	public Ticket(BasicBSONObject object) {
		if (object.containsField("client")) {
			client = new Client((BSONObject) object.get("client"));
		}
//		if (object.containsField("contractor")) {
//			contractor = new Contractor((BSONObject) object.get("contractor"));
//		}
		if (object.containsField("scheduleDate")) {
			scheduleDate = object.getDate("scheduleDate");
		}
		if (object.containsField("duration")) {
			duration = object.getInt("duration");
		}
		if (object.containsField("costResult")) {
			costResult = object.getDouble("costResult");
		}
		if (object.containsField("speedResult")) {
			speedResult = object.getDouble("speedResult");
		}
		if (object.containsField("qualityResult")) {
			qualityResult = object.getDouble("qualityResult");
		}
		if (object.containsField("resultRating")) {
			resultRating = object.getDouble("resultRating");
		}
		if (object.containsField("DOW")) {
			BasicDBList list = (BasicDBList) object.get("DOW");
			Date[] dA = new Date[0];
			datesOfWork = list.toArray(dA);
		}
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
//		contractor.scheduleTicket(this);
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

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public Date[] getDatesOfWork() {
		return datesOfWork;
	}

	public void setDatesOfWork(Date[] datesOfWork) {
		this.datesOfWork = datesOfWork;
	}

	@Override
	public String toString() {
		return "Ticket [client=" + client + ", scheduleDate=" + scheduleDate
				+ ", duration=" + duration + ", costResult=" + costResult + ", speedResult=" + speedResult
				+ ", qualityResult=" + qualityResult + ", resultRating=" + resultRating + ", DatesOfWork=" + Arrays.toString(datesOfWork) + "]\n";
	}

	public void insetIntoDb(DBCollection coll) {
		BasicDBObject obj = getDBObject();
		coll.insert(obj);
	}
	
	public static Ticket scheduleTicket(int Month, int Year, Client client, DBCollection coll) {
		Ticket ticket = new Ticket();
		ticket.setClient(client);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Year);
		if (Month > 12) throw new IndexOutOfBoundsException();
		calendar.set(Calendar.MONTH, Month);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int daysQty = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		Date scheduleDate = null;
		boolean found = false;
		while (!found) {
			int randomDay = (int) (Math.random() * (double) daysQty);
			calendar.set(Calendar.DAY_OF_MONTH, randomDay + 1);
			System.out.format("date = %tD, day of month is %d\n",calendar,randomDay );
			scheduleDate = calendar.getTime();
			BasicDBObject query = new BasicDBObject("schedule",scheduleDate);
			if (Ticket.getListFromDB(coll, query).size() < 1) {
				found = true;
				ticket.setScheduleDate(scheduleDate);
			}
		}
		ticket.setDuration((int) (Math.random() * 3.0) + 3);
		Date[] dA = new Date[ticket.getDuration()];
		calendar = Calendar.getInstance();
		calendar.setTime(ticket.scheduleDate);
		for (int i = 0; i < ticket.getDuration(); i++) {
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			dA[i] = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		ticket.setDatesOfWork(dA);
		return ticket;
	}
	
	public void getFromDb(int index, DBCollection coll) {
		BasicDBObject query = new BasicDBObject("_id", index);
		DBCursor cursor = coll.find(query);
		try {
			BasicDBObject object = (BasicDBObject) cursor.next();
			if (object.containsField("client")) {
				client = new Client((BSONObject) object.get("client"));
			}
//			if (object.containsField("contractor")) {
//				contractor = new Contractor((BSONObject) object.get("contractor"));
//			}
			if (object.containsField("scheduleDate")) {
				scheduleDate = object.getDate("scheduleDate");
			}
			if (object.containsField("duration")) {
				duration = object.getInt("duration");
			}
			if (object.containsField("costResult")) {
				costResult = object.getDouble("costResult");
			}
			if (object.containsField("speedResult")) {
				speedResult = object.getDouble("speedResult");
			}
			if (object.containsField("qualityResult")) {
				qualityResult = object.getDouble("qualityResult");
			}
			if (object.containsField("resultRating")) {
				resultRating = object.getDouble("resultRating");
			}
			if (object.containsField("DOW")) {
				BasicDBList list = (BasicDBList) object.get("DOW");
				Date[] dA = new Date[0];
				datesOfWork = list.toArray(dA);
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
		BasicDBObject obj = new BasicDBObject("client", client.getDBObject())
//				.append("contractor", contractor.getDBObject())
				.append("scheduleDate", scheduleDate)
				.append("duration", duration)
				.append("speedResult", speedResult)
				.append("costResult", costResult)
				.append("qualityResult", qualityResult)
				.append("resultRating", resultRating)
				.append("DOW", getDatesOfWork());
		return obj;
	}
	
	public void removeDb(int index, DBCollection coll) {
		BasicDBObject query = new BasicDBObject("_id", index);
		coll.remove(query);
	}
	
	public static ArrayList<Ticket> getListFromDB(DBCollection coll, BasicDBObject query) {
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		DBCursor cursor = query == null?coll.find() : coll.find(query);
		try {
			while (cursor.hasNext()) {
				Ticket ticket = new Ticket((BasicBSONObject) cursor.next());
				tickets.add(ticket);
			}
		} finally {
			cursor.close();
		}
		return tickets;
	}
	
	public static BasicDBList toDBList(ArrayList<Ticket> tickets) {
		if (tickets == null) return null;
		BasicDBList list = new BasicDBList();
		for (Ticket ticket : tickets) {
			list.add(ticket.getDBObject());
		}
		return list;
	}

}
