package com.ksquaredLabs.property;

import java.util.Random;

public class Util {

	public static double normalProbability(double mu) {
		double y = -1;
		while (y < 0 || y > 11.0) {
			y = (new Random().nextGaussian() * (12.0 - mu)) + mu;
		}
		return y;
//		return Math.exp(exponent) / output;
	}

}
