/**
 * 
 */
package com.ksquaredLabs.cognitive;

import java.util.ArrayList;
import java.util.Date;

import com.ksquaredLabs.cognitive.NPSInputs.NPSInputType;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * @author iblum
 *
 */
public class Cognitive {
	
	private static DB dB = null;
	private static MongoClient mongoClient = null;
	
	public static void initDB() {
		try {
			mongoClient = new MongoClient("localhost", 27017);
		} catch (Exception e) {
			
		}
		dB = mongoClient.getDB("mydb");
		dB.getCollection("inputs").drop();
		dB.getCollection("outputs").drop();
		dB.getCollection("client").drop();
		dB.getCollection("contractor").drop();
	}

	public static DB getDB() {
		return dB;
	}

	
	public static double[] calculateOutput(PriorityCalculator calc, ArrayList<NPSInputs> inputs) {
		double[] output = new double[4];
		ArrayList<Date> dates = getListOfDate(inputs);
		double speedRadix = calc.calculatePriorities(getListOfInput(NPSInputType.speed, inputs),dates);
		double costRadix = calc.calculatePriorities(getListOfInput(NPSInputType.cost, inputs),dates);
		double qualityRadix = calc.calculatePriorities(getListOfInput(NPSInputType.quality, inputs),dates);
		double sum = speedRadix + costRadix + qualityRadix;
//		System.out.format("raw priorities: %.4f, %.4f, %.4f\n", speedRadix, costRadix, qualityRadix);
		output[0] = speedRadix / sum;
		output[1] = costRadix / sum;
		output[2] = qualityRadix / sum;
		output[3] = calculateNPS();

		return output;
	}
	
	
	
	private static int averageOfList(ArrayList<Integer> list) {
		int sum = 0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i).intValue();
		}
		return sum/list.size();
	}
	
	private static ArrayList<Date> getListOfDate(ArrayList<NPSInputs> inputs) {
		ArrayList<Date> output = new ArrayList<Date>();
		for (int i = 0;i < inputs.size(); i++) {
			output.add(inputs.get(i).timeStamp);
		}
		return output;
	}
	
	private static ArrayList<Integer> getListOfInput(NPSInputType type, ArrayList<NPSInputs> inputs) {
		ArrayList<Integer> output = new ArrayList<Integer>();
		for (int i = 0;i < inputs.size(); i++) {
			NPSInputs input = inputs.get(i); 
			output.add(type == NPSInputType.speed?input.speed : 
				(type == NPSInputType.cost ? input.cost : input.quality));
		}
		return output;
	}
	
	private static double calculateNPS() {
		double nps = 0.0;
		
		DBCollection coll = dB.getCollection("inputs");
		BasicDBObject queryHigh = new BasicDBObject("average", new BasicDBObject("$gt",8.9));
		BasicDBObject queryLow = new BasicDBObject("average", new BasicDBObject("$lt",7.0));
		
		long countAll = coll.count();
		long countHigh = coll.count(queryHigh);
		long countLow = coll.count(queryLow);
		
		System.out.println("all " + countAll + " high " + countHigh + " low " + countLow);
		
		nps = (double) countHigh / (double) countAll;
		nps -= (double) countLow / (double) countAll;
		nps *= 100.0;
		
		return nps;
	}
}
