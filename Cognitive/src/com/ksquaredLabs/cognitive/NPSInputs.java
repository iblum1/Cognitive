package com.ksquaredLabs.cognitive;

import java.util.Date;

import com.mongodb.BasicDBObject;

public class NPSInputs {

	public int speed;
	public int cost;
	public int quality;
	public double average;
	public Date timeStamp;
	
	public BasicDBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject("speed", speed).append("cost", cost)
				.append("quality", quality).append("average", average).append("timeStamp", timeStamp);
		return obj;
	}
	
	public enum NPSInputType {
		speed,cost,quality;
	}
}
