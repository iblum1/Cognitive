package com.ksquaredLabs.test;

import java.util.Calendar;

public class CalendarText {

	public static void main(String[] args) {
		int M = Integer.parseInt(args[0]) - 1;
		int Y = Integer.parseInt(args[1]);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, M);
		c.set(Calendar.YEAR, Y);
		System.out.format("    %tb %d\n",c,Y);
		for (int i = 0; i < 7; i++) {
			c.set(Calendar.DAY_OF_WEEK, i);
			System.out.format(" %ta",c);
		}
		System.out.println();
		c.set(Calendar.DAY_OF_MONTH, 0);
		int dow = c.get(Calendar.DAY_OF_WEEK);
		for (int i = 0;i < dow; i++) {
			System.out.print("    ");
		}
		for (int i = 1;i < c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
			c.set(Calendar.DAY_OF_MONTH, i);
			System.out.format("  %td", c);
			if (( i + dow) % 7 == 0) {
				System.out.println();
			}
		}
		System.out.println();
	}
}
