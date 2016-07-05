package com.ksquaredLabs.property;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.ksquaredLabs.property.Client.Client;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class Ticket implements Comparable<Ticket> {

	private Client client;
	private Contractor contractor;
	private String contractorName;
	private Date scheduleDate;
	private int duration;
	private double costResult;
	private double speedResult;
	private double qualityResult;
	private double resultRating;
	private TicketType ticketType;
	
	
	private ArrayList<Date> datesOfWork;
	
	private int daysPushed = 0;
	
	private int hoursExtended = 0;
	
	private int recurrence = 30;
	
	private boolean processed = false;

	private double cost = 0.0;

	public Ticket() {
		
	}

	public Ticket(BasicBSONObject object) {
		if (object.containsField("client")) {
			client = new Client((BSONObject) object.get("client"));
		}
		if (object.containsField("contractor")) {
			contractorName = object.getString("contractor");
		}
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
			datesOfWork = (ArrayList) list;
		}
		if (object.containsField("daysPushed")) {
			daysPushed = object.getInt("daysPushed");
		}
		if (object.containsField("hoursExtended")) {
			hoursExtended = object.getInt("hoursExtended");
		}
		if (object.containsField("recurrence")) {
			recurrence = object.getInt("recurrence");
		}
		if (object.containsField("processed")) {
			processed = object.getBoolean("processed");
		}
		if (object.containsField("cost")) {
			cost = object.getDouble("cost");
		}
		if (object.containsField("type")) {
			ticketType = TicketType.getTicketType(object.getString("type"));
		}
	}

	public Ticket(Client client, Contractor contractor) {
		this.client = client;
		this.contractor = contractor;
		this.contractorName = contractor.getName();
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
	
	public static Ticket nextUnProcessedTicket(ArrayList<Ticket> tickets) {
		for (Ticket ticket : tickets) {
			if (!ticket.isProcessed()) {
				return ticket;
			}
		}
		return null;
	}
	
	public static Ticket scheduleTicket(Date date, Client client, DBCollection coll, TicketType type, boolean random) {
		Ticket ticket = new Ticket();
		ticket.setClient(client);
		ticket.setTicketType(type);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date scheduleDate = null;
		boolean found = false;
		int daysQty = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		while (!found) {
			if (random) {
				int randomDay = (int) (Math.random() * (double) daysQty);
				calendar.set(Calendar.DAY_OF_MONTH, randomDay + 1);
				System.out.format("date = %tD, day of month is %d\n",calendar,randomDay );
			}
			scheduleDate = calendar.getTime();
			BasicDBObject query = new BasicDBObject("schedule",scheduleDate);
			if (Ticket.getListFromDB(coll, query).size() < 1) {
				found = true;
				ticket.setScheduleDate(scheduleDate);
			} else if (!random) {
				calendar.add(Calendar.DATE, 1);
			}
		}
		switch (ticket.getTicketType()) {
			case project:
				ticket.setDuration((int) (Math.random() * 40.0) + 24);
				break;
			case maintenance:
				ticket.setDuration((int) (Math.random() * 8.0) + 24);
				break;
			case repair:
				ticket.setDuration((int) (Math.random() * 8.0) + 8);
				break;
			default:
				ticket.setDuration((int) (Math.random() * 24.0) + 24);

		}
		ArrayList<Date> dA = new ArrayList<Date> ();
		calendar = Calendar.getInstance();
		calendar.setTime(ticket.scheduleDate);
		for (int i = 0; i < ticket.getDuration() / 8; i++) {
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			dA.add(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		ticket.setDatesOfWork(dA);
		return ticket;
	}
	
	public static Ticket scheduleTicket(int Month, int Year, Client client, DBCollection coll, TicketType type, boolean random) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Year);
		if (Month > 12) throw new IndexOutOfBoundsException();
		calendar.set(Calendar.MONTH, Month);
		return scheduleTicket(calendar.getTime(), client, coll, type, random);
	}
	
	public static boolean ticketsProcessed(ArrayList<Ticket> tickets) {
		for (Ticket ticket : tickets) {
			if (!ticket.isProcessed()) {
				return false;
			}
		}
		return true;
	}
	
	public static BasicDBList toDBList(ArrayList<Ticket> tickets) {
		if (tickets == null) return null;
		BasicDBList list = new BasicDBList();
		for (Ticket ticket : tickets) {
			list.add(ticket.getDBObject());
		}
		return list;
	}


	@Override
	public int compareTo(Ticket o) {
		return getScheduleDate().compareTo(o.getScheduleDate());
	}

	public int extend(int hours) {
		duration += hours;
		for (int i = 0; i < hours / 8; i++) {
			Date d = datesOfWork.get(datesOfWork.size() -1);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.add(Calendar.DAY_OF_MONTH, 1);
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				c.add(Calendar.DAY_OF_MONTH, 1);
			}
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				c.add(Calendar.DAY_OF_MONTH, 1);
			}
			datesOfWork.add(c.getTime());
		}
		System.out.println("Extended by " + hours);
		hoursExtended = hours;
		return duration;
	}

	public Client getClient() {
		return client;
	}

	public Contractor getContractor() {
		return contractor;
	}

	public String getContractorName() {
		return contractorName;
	}

	public double getCost() {
		return cost;
	}

	public double getCostResult() {
		return costResult;
	}

	public ArrayList<Date> getDatesOfWork() {
		return datesOfWork;
	}

	public int getHoursExtended() {
		return hoursExtended;
	}
	
	public int getDaysPushed() {
		return daysPushed;
	}

	public BasicDBObject getDBObject() {
		BasicDBObject obj = new BasicDBObject("client", client.getDBObject())
				.append("contractor", contractorName)
//				.append("contractor", contractor.getDBObject())
				.append("scheduleDate", scheduleDate)
				.append("duration", duration)
				.append("speedResult", speedResult)
				.append("costResult", costResult)
				.append("qualityResult", qualityResult)
				.append("resultRating", resultRating)
				.append("DOW", getDatesOfWork())
				.append("daysPushed", daysPushed)
				.append("hoursExtended", hoursExtended)
				.append("recurrence", recurrence)
				.append("processed", processed)
				.append("cost", cost)
				.append("type", ticketType.toString());
		return obj;
	}
	
	public int getDuration() {
		return duration;
	}

	public void getFromDb(int index, DBCollection coll) {
		BasicDBObject query = new BasicDBObject("_id", index);
		DBCursor cursor = coll.find(query);
		try {
			BasicDBObject object = (BasicDBObject) cursor.next();
			if (object.containsField("client")) {
				client = new Client((BSONObject) object.get("client"));
			}
			if (object.containsField("contractor")) {
				contractorName = object.getString("contractor");
			}
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
				datesOfWork = (ArrayList) list;
			}
			if (object.containsField("daysPushed")) {
				daysPushed = object.getInt("daysPushed");
			}
			if (object.containsField("hoursExtended")) {
				hoursExtended = object.getInt("hoursExtended");
			}
			if (object.containsField("recurrence")) {
				recurrence = object.getInt("recurrence");
			}
			if (object.containsField("processed")) {
				processed = object.getBoolean("processed");
			}
			if (object.containsField("cost")) {
				cost = object.getDouble("cost");
			}
			if (object.containsField("type")) {
				ticketType = TicketType.getTicketType(object.getString("type"));
			}
		} catch (Exception e) {
			cursor.close();
			System.err.println(e);
		}
		
	}
	
	public void insetIntoDb(DBCollection coll) {
		BasicDBObject obj = getDBObject();
		coll.insert(obj);
	}

	public void processTicket() {
		double[] results = contractor.calculateResultOfTicket();
		speedResult = results[0];
		costResult = results[1];
		qualityResult = results[2];
		System.out.format("Ticket Results are %.2f, %.2f, %.2f\n", speedResult, costResult, qualityResult);
		if (speedResult < 7.0) {
			extend(16);
		} else if (speedResult < 9.0) {
			extend(8);
		}
		if (qualityResult < 7.0) {
			recurrence -= 10;
		} else if (qualityResult < 9.0) {
			recurrence -= 5;
		}
		cost = contractor.getCostPer() * duration;
		resultRating = client.calculateRating(costResult, speedResult, qualityResult);
		processed = true;
	}

	public int push() {
		Calendar c = Calendar.getInstance();
		c.setTime(datesOfWork.get(0));
		c.add(Calendar.DAY_OF_MONTH, 1);
		ArrayList<Date> dA = new ArrayList<Date> ();
		for (int i = 0; i < getDuration(); i++) {
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				c.add(Calendar.DAY_OF_MONTH, 1);
			}
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				c.add(Calendar.DAY_OF_MONTH, 1);
			}
			dA.add(c.getTime());
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		setDatesOfWork(dA);
		daysPushed++;
		System.out.println("Pushed by " + daysPushed);
		return daysPushed;
	}
	
	public void removeDb(int index, DBCollection coll) {
		BasicDBObject query = new BasicDBObject("_id", index);
		coll.remove(query);
	}
	
	public double getQualityResult() {
		return qualityResult;
	}

	public double getResultRating() {
		return resultRating;
	}

	public double getSpeedResult() {
		return speedResult;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setContractor(Contractor contractor) {
		this.contractor = contractor;
		this.contractorName = contractor.getName();
	}
	
	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setDatesOfWork(ArrayList<Date> datesOfWork) {
		this.datesOfWork = datesOfWork;
	}
	
	public void setDaysExtended(int daysExtended) {
		this.hoursExtended = daysExtended;
	}
	
	public void setDaysPushed(int daysPushed) {
		this.daysPushed = daysPushed;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	public int getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(int recurrence) {
		this.recurrence = recurrence;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	
	public TicketType getTicketType() {
		return ticketType;
	}

	public void setTicketType(TicketType ticketType) {
		this.ticketType = ticketType;
	}

	@Override
	public String toString() {
		return String.format("Ticket [client=%s, scheduleDate=%tD, contractor=%s, duration=%d, costResult=%.2f, speedResult=%.2f, qualityResult=%.2f, "
				+ "resultRating=%.2f, datesOfWork=%s, daysPushed=%d, hoursExtended=%d, recurrence=%d, processed=%s, ticketType=%s, cost=%.2f]\n", 
				client.toString(), scheduleDate, contractorName, duration, costResult, speedResult,
				qualityResult, resultRating, datesOfWork.toString(), daysPushed, hoursExtended, recurrence, processed, ticketType.toString(), cost);
	}
	
	public void updateDb(int index, DBCollection coll) {
		BasicDBObject obj = getDBObject();
		BasicDBObject query = new BasicDBObject("_id", index);
		coll.update(query, obj);
		
	}

	public static enum TicketType {
		project, maintenance, repair;
		
		public static TicketType getTicketType(String type) {
			if (type == null) return null;
			if (type.equals("project")) return TicketType.project;
			if (type.equals("maintenance")) return TicketType.maintenance;
			if (type.equals("repair")) return TicketType.repair;
			return null;
		}
	}
}
