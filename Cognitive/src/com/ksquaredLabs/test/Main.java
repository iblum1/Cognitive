package com.ksquaredLabs.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.ksquaredLabs.cognitive.NPSInputs;
import com.ksquaredLabs.cognitive.PriorityCalculator;
import com.ksquaredLabs.property.Client;
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
		
		DBCollection inputColl = dB.getCollection("inputs");
		DBCollection outputColl = dB.getCollection("outputs");
		BasicDBObject query = new BasicDBObject("name",args[0]);
		ArrayList<Client> clients = Client.getListFromDB(dB.getCollection("client"), query);
		
		int numberOfIterations = 0;
		
		for (Client client : clients) {
			
			numberOfIterations++;
			
			
		
			double speedBase = client.getSpeedFactor();
			double costBase = client.getCostFactor();
			double qualityBase = client.getQualityFactor();
	
			int timer = 0;
			double NPS = 0;
			while (NPS < 50.0) {
				Ticket ticket = new Ticket();
				ticket.setClient(client);
				ContractorPicker picker = new ContractorPicker();
				picker.setTicket(ticket);
				ticket.setContractor(picker.pickContractor(dB));
				ticket.processTicket();
				
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
				
				
				inputs.add(inputData);
				
				inputColl.insert(inputData.toDBObject());
				
				double[] radicies = Cognitive.calculateOutput(calc, inputs);
				
				double x = radicies[0];
				double y = radicies[1];
				double z = radicies[2];
				NPS = radicies[3];
				System.out.format("Iteration %d: Speed/Cost/Quality base: %.2f, %.2f, %.2f.\n", 
						timer, speedBase, costBase, qualityBase);
				
				outputColl.insert(toDoubleArrayObj(radicies));
				
				System.out.format("Speed/Cost/Quality Inputs: %d; %d; %d. Average: %.2f Radix: %.2f, %.2f, %.2f. NPS %.2f\n",
						i,j,k,average,x, y, z, NPS );
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				timer++;
			}
			System.out.println("Number of iterations for " + client.getName() + " ");
		}
		
	}

	private static BasicDBObject toDoubleArrayObj(double[] radicies) {
		BasicDBObject obj = new BasicDBObject();
		for (int i = 0;i < radicies.length; i++) {
			obj.append("" + i, Double.toString(radicies[i]));
		}
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

}
