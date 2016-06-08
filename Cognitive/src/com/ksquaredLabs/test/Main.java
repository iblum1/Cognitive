package com.ksquaredLabs.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.ksquaredLabs.cognitive.NPSInputs;
import com.ksquaredLabs.cognitive.PriorityCalculator;
import com.ksquaredLabs.property.Client;
import com.ksquaredLabs.property.Contractor;
import com.ksquaredLabs.property.ContractorPicker;
import com.ksquaredLabs.property.Ticket;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.ksquaredLabs.cognitive.*;

public class Main {

	private static ArrayList<NPSInputs> inputs = new ArrayList<NPSInputs>(); 

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
		
		int numberOfIterations = 0;
		
		for (Client client : clients) {
			
			
			
		
			double speedBase = client.getSpeedFactor();
			double costBase = client.getCostFactor();
			double qualityBase = client.getQualityFactor();
	
			int timer = 0;
			double NPS = 0;
			while (NPS < 50.0 && timer < 50) {
				System.out.println("---");
				Ticket ticket = new Ticket();
				ticket.setClient(client);
				ContractorPicker picker = new ContractorPicker();
				picker.setTicket(ticket);
				ticket.setContractor(picker.pickContractor(dB));
				ticket.processTicket();
//				System.out.println("Contractor picked is " + ticket.getContractor().getName());
				
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
				
				double[] radicies = Cognitive.calculateOutput(calc, inputs, client);
				
				double x = radicies[0];
				double y = radicies[1];
				double z = radicies[2];
				NPS = radicies[3];
				System.out.format("Name %s, Contractor %s: \nIteration %d: Speed/Cost/Quality base: %.2f, %.2f, %.2f.\n", 
						client.getName(), ticket.getContractor().getName(), timer, speedBase, costBase, qualityBase);
				
				outputColl.insert(toDoubleArrayObj(radicies, client));
				
				System.out.format("Inputs: speed %d; cost %d; quality %d. Average: %.2f \nRadix: speed %.2f, cost %.2f, quality %.2f. NPS %.2f\n",
						i,j,k,average,x, y, z, NPS );
				
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				timer++;
			}
			numberOfIterations += timer;
			System.out.println("Number of iterations for " + client.getName() + " equals " + numberOfIterations);
		}
		
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
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Nothing is free");
		contractor.setCostRating(4.0);
		contractor.setSpeedRating(9.0);
		contractor.setQualityRating(9.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("How Fast is fast");
		contractor.setCostRating(5.0);
		contractor.setSpeedRating(10.0);
		contractor.setQualityRating(5.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Take our time");
		contractor.setCostRating(9.0);
		contractor.setSpeedRating(4.0);
		contractor.setQualityRating(9.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Only the Best");
		contractor.setCostRating(5.0);
		contractor.setSpeedRating(5.0);
		contractor.setQualityRating(10.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Cheap and Quick");
		contractor.setCostRating(9.0);
		contractor.setSpeedRating(9.0);
		contractor.setQualityRating(4.0);
		contractor.insetIntoDb(contractorCollection);
		
		contractor = new Contractor();
		contractor.setName("Average Joe's Gym");
		contractor.setCostRating(7.0);
		contractor.setSpeedRating(7.0);
		contractor.setQualityRating(7.0);
		contractor.insetIntoDb(contractorCollection);
		
		
		
	}

}
