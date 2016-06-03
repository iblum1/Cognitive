package com.ksquaredLabs.cognitive;

import java.util.ArrayList;
import java.util.Date;

public class PriorityCalculator {

	private static double priorityMean = 4.0;
	private static double prioritySigma = 2.0;
	private static double dateMean = 3000.0;
	private static double dateSigma = 2000.0;
	
	public double calculatePriorities(ArrayList<Integer> listOfValues, ArrayList<Date> listOfDates) {
//		int input = averageOfList(list);
		int input = weightedAverageOfList(listOfValues, getDateWeights(listOfDates));
//		System.out.println("input: " + input);
		return normalProbability((double) input, prioritySigma, priorityMean);
//		if (input < 7) return 2.0;
//		if (input < 9) return 1.0;
//		return 0.5;
		
	}

	private ArrayList<Double> getDateWeights(ArrayList<Date> dates) {
		ArrayList<Double> output = new ArrayList<Double>();
		for (int i = 0; i < dates.size(); i++) {
			long elapsedTime = System.currentTimeMillis() - dates.get(i).getTime();
			output.add(((double) elapsedTime));
		}
		return output;
	}
	
	private int weightedAverageOfList(ArrayList<Integer> list, ArrayList<Double> weights) {
		double weightedSum = 0;
		double weightedTotal = 0;
//		System.out.print("elapsed time, normal weight:");
		for (int i = 0; i < list.size(); i++) {
//			System.out.format("%.0f, ", weights.get(i).doubleValue());
			double weight = normalProbability(weights.get(i).doubleValue(),dateSigma, dateMean);
			weight *= 1000.0;
			weightedSum += (double) list.get(i) * weight;
			weightedTotal += weight;
//			System.out.format("%.4f, ", weight);
		}
//		System.out.println();
		return (int) (weightedSum / weightedTotal);
	}
	
	private double normalProbability(double x, double sigma, double mu) {
		double output = sigma * (Math.sqrt(2.0 * Math.PI));
		double exponent = (-(x - mu) * (x - mu)) / (2 * sigma * sigma);
		return Math.exp(exponent) / output;
	}
	
}
