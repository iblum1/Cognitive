package com.ksquaredLabs.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;


import com.ksquaredLabs.cognitive.NPSInputs;
import com.ksquaredLabs.cognitive.PriorityCalculator;
import com.ksquaredLabs.property.Contractor;
import com.ksquaredLabs.property.ContractorPicker;
import com.ksquaredLabs.property.Ticket;
import com.ksquaredLabs.property.Client.Client;
import com.ksquaredLabs.property.Ticket.TicketType;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.ksquaredLabs.cognitive.*;

public class Main {

	private static ArrayList<NPSInputs> inputs = new ArrayList<NPSInputs>(); 
	
	private static ArrayList<Ticket> noContractor = new ArrayList<Ticket>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		PriorityCalculator calc = new PriorityCalculator();
		
		Cognitive.initDB();
		
		DB dB = Cognitive.getDB();
		
		if (!dataExists(dB)) {
			populateData(dB);
		}
		
		DBCollection inputColl = dB.getCollection("inputs");
		DBCollection outputColl = dB.getCollection("outputs");
		BasicDBObject query = args.length > 0 ? new BasicDBObject("name",args[0]) : null;
		ArrayList<Client> clients = Client.getListFromDB(dB.getCollection("client"), query);
		ArrayList<Ticket> tickets = new ArrayList<Ticket> ();
		
		int numberOfIterations = 0;
		ContractorPicker picker = new ContractorPicker();
		
		for (Client client : clients) {
		
			for (int timer = 0; timer < 1; timer++) {
				Ticket ticket = Ticket.scheduleTicket(0, 2016, client, dB.getCollection("ticket"), TicketType.project, true);
				tickets.add(ticket);
			}
//			for (int i = 0; i < 5; i++) {
//				int randomMonth = (int) (Math.random() * 11.0);
//				Ticket ticket = Ticket.scheduleTicket(randomMonth, 2016, client, dB.getCollection("ticket"), TicketType.repair, true);
//				tickets.add(ticket);
//			}
		}
		System.out.println(tickets);
		boolean done = false;
		while (!done) {
			Collections.sort(tickets);
			Ticket ticket = Ticket.nextUnProcessedTicket(tickets);
			Client client = ticket.getClient();
			double speedBase = client.getSpeedFactor();
			double costBase = client.getCostFactor();
			double qualityBase = client.getQualityFactor();
	
			double NPS = 0;

			picker.setTicket(ticket);
			System.out.println("---");
			System.out.println("Ticket is " + ticket);
			Contractor contractor = picker.pickContractor(dB);
			if (contractor == null) {
				System.out.println("no Contractor Available");
				noContractor.add(ticket);
			} else {
				ticket.setContractor(contractor);
				ticket.processTicket();
				ticket.getClient().updateYearlySpend(ticket.getCost());
				ticket.getClient().incrementTickets();
				ticket.getClient().updateDb(dB.getCollection("client"));
				ticket.insetIntoDb(dB.getCollection("ticket"));
				System.out.format("Date is %tD, Length is %d days\n", ticket.getScheduleDate(), ticket.getDuration());
				
				int i = (int) (ticket.getSpeedResult());
				int j = (int) (ticket.getCostResult());
				int k = (int) (ticket.getQualityResult());
				double average = ticket.getResultRating();
				
				NPSInputs inputData = new NPSInputs();
				inputData.speed = i;
				inputData.cost = j;
				inputData.quality = k;
				inputData.average = average;
				inputData.timeStamp = new Date();
				inputData.client = client;
				
				
				inputs.add(inputData);
				
				inputColl.insert(inputData.toDBObject());
				if (ticket.getTicketType() != TicketType.repair) {
					Calendar reschedule = Calendar.getInstance();
					reschedule.setTime(ticket.getScheduleDate());
					reschedule.add(Calendar.DATE,ticket.getRecurrence());
					if (reschedule.get(Calendar.YEAR) < 2017) {
						Ticket rescheduledTicket = Ticket.scheduleTicket(reschedule.getTime(), client, dB.getCollection("ticket"), TicketType.maintenance, false);
						System.out.format("Ticket added on %tD is %s",rescheduledTicket.getScheduleDate(),rescheduledTicket.toString());
						tickets.add(rescheduledTicket);
					}
				}
				if (ticket.getQualityResult() < 5) {
					Calendar scheduleRepair = Calendar.getInstance();
					scheduleRepair.setTime(ticket.getScheduleDate());
					scheduleRepair.add(Calendar.DATE, (int) ticket.getQualityResult());
					if (scheduleRepair.get(Calendar.YEAR) < 2017) {
						Ticket scheduledRepairTicket = Ticket.scheduleTicket(scheduleRepair.getTime(), client, dB.getCollection("ticket"), TicketType.repair, false);
						System.out.format("Ticket added on %tD is %s",scheduledRepairTicket.getScheduleDate(),scheduledRepairTicket.toString());
						tickets.add(scheduledRepairTicket);
					}
				}
				
				double[] radicies = Cognitive.calculateOutput(calc, inputs, client);
				
				double x = radicies[0];
				double y = radicies[1];
				double z = radicies[2];
				NPS = radicies[3];
				System.out.format("Name %s, Contractor %s: \nIteration %d: Speed/Cost/Quality base: %.2f, %.2f, %.2f.\n", 
						client.getName(), ticket.getContractor().getName(), numberOfIterations, speedBase, costBase, qualityBase);
				
				outputColl.insert(toDoubleArrayObj(radicies, client));
				
				System.out.format("Inputs: speed %d; cost %d; quality %d. Average: %.2f \nRadix: speed %.2f, cost %.2f, quality %.2f. NPS %.2f\n",
						i,j,k,average,x, y, z, NPS );
				
			}
			numberOfIterations++;
			System.out.println(numberOfIterations + ") number of tickets is now " + tickets.size());

			if (numberOfIterations > 1000) 
				System.exit(0);
			done = Ticket.ticketsProcessed(tickets);
		}
		ArrayList<Ticket> list = Ticket.getListFromDB(dB.getCollection("ticket"), null);
		Collections.sort(list);
//		System.out.println(list);
		HashMap<Integer, String> data = new HashMap<Integer, String>();
		HashMap<String, HashMap<Integer,String>> matrix = new HashMap();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_YEAR,1);
		for (int i = 0; i < 12; i++) {
			displayResult(list, null, c, data, String.format("%tB ", c));
			c.add(Calendar.MONTH, 1);
			data = new HashMap<Integer, String>();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		ArrayList<Contractor> contractors = Contractor.getListFromDB(dB.getCollection("contractor"), null);
//		for (Contractor contractor : contractors) {
//			HashMap<Integer, String> cdata = new HashMap<Integer, String>();
//			c = Calendar.getInstance();
//			list = contractor.getMyTickets();
//			if (list == null) break;
//			for (Ticket ticket : list) {
//				for (Date d : ticket.getDatesOfWork()) {
//					c.setTime(d);
//					int day1 = c.get(Calendar.DAY_OF_MONTH);
//					if (c.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
//						cdata.put(day1, ticket.getClient().getName().substring(0, 3));
//					}
//				}
//			}
//			c = getToday();
//			CalendarIconExample.showCalendar(cdata, c, contractor.getName() + " " + list.size());
//		}
		
		
	}

	private static BasicDBObject toDoubleArrayObj(double[] radicies, Client client) {
		BasicDBObject obj = new BasicDBObject();
		for (int i = 0;i < radicies.length; i++) {
			obj.append("" + i, Double.toString(radicies[i]));
		}
		obj.append("client", client.getDBObject());
		return obj;
	}
	
	private static double normalProbability(double mu) {
		double y = -1;
		while (y < 0 || y > 11.0) {
			y = (new Random().nextGaussian() * (12.0 - mu)) + mu;
		}
		return y;
//		return Math.exp(exponent) / output;
	}
	
	private static boolean dataExists(DB dB) {
		DBCollection clientCollection = dB.getCollection("client");
		if (clientCollection.getCount() == 0) {
			return false;
		}
		DBCollection contractorCollection = dB.getCollection("contractor");
		if (contractorCollection.getCount() == 0) {
			return false;
		}
		return true;
	}
	
	private static void populateData(DB dB) {
		
		DBCollection clientCollection = dB.getCollection("client");
		clientCollection.drop();
		Client client = new Client();
		client.setName("Poor Polly");
		client.setCostFactor(10.0);
		client.setSpeedFactor(2.0);
		client.setQualityFactor(2.0);
		client.insetIntoDb(clientCollection);
		
		client = new Client();
		client.setName("Richie Rich");
		client.setCostFactor(2.0);
		client.setQualityFactor(10.0);
		client.setSpeedFactor(10.0);
		client.insetIntoDb(clientCollection);
		
		client = new Client();
		client.setName("Speedy Gonzalez");
		client.setCostFactor(2.0);
		client.setQualityFactor(2.0);
		client.setSpeedFactor(10.0);
		client.insetIntoDb(clientCollection);

		client = new Client();
		client.setName("Sally Slow");
		client.setCostFactor(10.0);
		client.setQualityFactor(10.0);
		client.setSpeedFactor(2.0);
		client.insetIntoDb(clientCollection);

		client = new Client();
		client.setName("Sloppy Sam");
		client.setCostFactor(10.0);
		client.setQualityFactor(2.0);
		client.setSpeedFactor(10.0);
		client.insetIntoDb(clientCollection);

		client = new Client();
		client.setName("Picky Pam");
		client.setCostFactor(2.0);
		client.setQualityFactor(10.0);
		client.setSpeedFactor(2.0);
		client.insetIntoDb(clientCollection);

		client = new Client();
		client.setName("Joe Average");
		client.setCostFactor(7.0);
		client.setQualityFactor(7.0);
		client.setSpeedFactor(7.0);
		client.insetIntoDb(clientCollection);

		DBCollection contractorCollection = dB.getCollection("contractor");
		Contractor contractor = new Contractor();
		contractor.setName("Cheap is our Middle Name");
		contractor.setCostRating(10.0);
		contractor.setSpeedRating(5.0);
		contractor.setQualityRating(5.0);
		contractor.setCostPer(10.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Nothing is free");
		contractor.setCostRating(4.0);
		contractor.setSpeedRating(9.0);
		contractor.setQualityRating(9.0);
		contractor.setCostPer(15.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("How Fast is fast");
		contractor.setCostRating(5.0);
		contractor.setSpeedRating(10.0);
		contractor.setQualityRating(5.0);
		contractor.setCostPer(15.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Take our time");
		contractor.setCostRating(9.0);
		contractor.setSpeedRating(4.0);
		contractor.setQualityRating(9.0);
		contractor.setCostPer(11.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Only the Best");
		contractor.setCostRating(5.0);
		contractor.setSpeedRating(5.0);
		contractor.setQualityRating(10.0);
		contractor.setCostPer(15.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Cheap and Quick");
		contractor.setCostRating(9.0);
		contractor.setSpeedRating(9.0);
		contractor.setQualityRating(4.0);
		contractor.setCostPer(10.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Average Joe's Gym");
		contractor.setCostRating(7.0);
		contractor.setSpeedRating(7.0);
		contractor.setQualityRating(7.0);
		contractor.setCostPer(12.5);
		contractor.insetIntoDb(contractorCollection);
		
		
		
	}
	
	private static Calendar getToday() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY,8);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}
	
	private static void displayResult(ArrayList<Ticket> list, HashMap<String, HashMap<Integer, String>> matrix, Calendar displayMonth, HashMap<Integer, String> data, String title) {
		Calendar ticketMonth = Calendar.getInstance();
		
		for (Ticket ticket : list) {
			for (Date date : ticket.getDatesOfWork()) {
				ticketMonth.setTime(date);
				int day1 = ticketMonth.get(Calendar.DAY_OF_MONTH);
				if (ticketMonth.get(Calendar.MONTH) == displayMonth.get(Calendar.MONTH) && ticketMonth.get(Calendar.YEAR) == displayMonth.get(Calendar.YEAR)) {
					if (!data.containsKey(day1)) {
						data.put(day1, " 1 ");
					} else {
						int currentNumber = Integer.parseInt(data.get(day1).toString().trim());
		
						data.put(day1, " " + (currentNumber + 1) + " ");
					}
				}
			}
		}
		ticketMonth = Calendar.getInstance();
		ticketMonth.set(Calendar.MONTH, displayMonth.get(Calendar.MONTH));
		ticketMonth.set(Calendar.DAY_OF_MONTH, 1);
		CalendarIconExample.showCalendar(data, ticketMonth, title + list.size());

	}

}
