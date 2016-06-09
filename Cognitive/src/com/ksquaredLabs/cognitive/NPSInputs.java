package com.ksquaredLabs.cognitive;

import java.util.Date;

import com.ksquaredLabs.property.Client;
import com.mongodb.BasicDBObject;

public class NPSInputs {

	public int speed;
	public int cost;
	public int quality;
	public double average;
	public Date timeStamp;
	public Client client;
	
	public BasicDBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject("speed", speed).append("cost", cost)
				.append("quality", quality).append("average", average).append("timeStamp", timeStamp)
				.append("client", client.getDBObject());
		return obj;
	}
	
	public enum NPSInputType {
		speed,cost,quality,average;
	}
}
