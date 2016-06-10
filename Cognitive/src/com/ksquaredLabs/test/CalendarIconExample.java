package com.ksquaredLabs.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class CalendarIconExample extends JComponent {
	private int SIZE = 300;
	private Dimension dim = new Dimension(SIZE, SIZE);
	private int nx, ny, width = 280, height = 280;
	private Calendar cal;
	private Font dateFont, dayFont, monthFont;
	private FontMetrics date, day, month;
	private boolean showTime = true;
	private HashMap<Date, String> data = new HashMap<Date, String> ();
	
	public CalendarIconExample(Calendar c, boolean show, int nx, int ny, HashMap<Date, String> data) {
		super();
		System.out.format("Third Constructor %s\n",data);
		cal = c;
		this.ny = ny;
		this.nx = nx;
		dateFont = new Font("Monospaced", Font.BOLD, 16);
		date = getFontMetrics(dateFont);
		dayFont = new Font("Monospaced", Font.BOLD, 16);
		day = getFontMetrics(dayFont);
		monthFont =new Font("Book Antiqua", Font.BOLD, 10);
		month = getFontMetrics(monthFont);
		this.data = data;
	}
	
	public void paint(Graphics graphics) {
		paintIcon(this, graphics, 0, 0);
	}
	
	public void paintIcon(Component component, Graphics g, int x, int y) {
		Calendar c = cal;
		Calendar tempCal = Calendar.getInstance();
		
		// frame
		g.drawRect(x,  y,  dim.width - 2,  dim.height - 2);
		g.setColor(Color.gray);
		g.fillRect(x + nx + 3, y + ny + 3, (int) (width * 1.5), height);
		g.setColor(Color.white);
		g.fillRect(x + nx, y + ny, (int) (width * 1.5), height);
		g.setColor(Color.black);
		if (showTime) super.paint(g);
		
		String st = String.format("%tb", c);
		System.out.format("Month %tD\n", cal);
		g.setFont(monthFont);
		g.setColor(Color.red);
		int w = month.stringWidth(st);
		g.drawString(st, x + nx + ((width - w) / 2), y + ny + 10);

		// day of week
		st = "";
		for (int i = 0; i < 7; i++) {
			c.set(Calendar.DAY_OF_WEEK, i+1);
			st += String.format(" %ta",c);
		}
		g.setFont(dayFont);
		g.setColor(Color.red);
		w = day.stringWidth(st);
		g.drawString(st, x + nx + ((width - 2) / 2), y + ny + 25);
		
		// day of month
		st = "";
		tempCal.set(Calendar.MONTH, c.get(Calendar.MONTH));
		tempCal.set(Calendar.YEAR, c.get(Calendar.YEAR));
		tempCal.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH)-1);
		int dow = tempCal.get(Calendar.DAY_OF_WEEK);
		for (int i = 0;i < dow; i++) {
			st += "    ";
		}
		int week = 0;
		int dayOfWeek;
		String dataString = "";
		for (int dayOfMonth = 1;dayOfMonth < tempCal.getActualMaximum(Calendar.DAY_OF_MONTH); dayOfMonth++) {
			tempCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
			st += String.format("  %td", tempCal);
			String temp = data.get(tempCal.getTime());
			if (temp != null) {
				if (temp.length() > 3) temp = temp.substring(0,3);
				dataString += temp;
			}
			dataString += " ";
			if (dayOfWeek == Calendar.SUNDAY) {
				g.setFont(dateFont);
				g.setColor(Color.black);
				w = date.stringWidth(st);
				g.drawString(st, x + nx + ((width - 2) / 2), y + ny + 35 + week * 30);
				Calendar d = Calendar.getInstance();
				d.setTime(tempCal.getTime());
				st = "";
				g.setFont(dateFont);
				g.setColor(Color.blue);
				w = date.stringWidth(dataString);
				g.drawString(dataString, x + nx + ((width - 2) / 2), y + ny + 35 + week * 30 + 15);
				dataString = "";
				week++;
				
			}
		}
		g.setFont(dateFont);
		g.setColor(Color.black);
		w = date.stringWidth(st);
		g.drawString(st, x + nx + ((width - 2) / 2), y + ny + 35 + week * 30);
		Calendar d = Calendar.getInstance();
		d.setTime(tempCal.getTime());
		st = "";
		g.setFont(dateFont);
		g.setColor(Color.blue);
		w = date.stringWidth(dataString);
		g.drawString(dataString, x + nx + ((width - 2) / 2), y + ny + 35 + week * 30 + 15);
		dataString = "";
		week++;
		
		g.setFont(dateFont);
		g.setColor(Color.black);
		w = date.stringWidth(st);
		g.drawString(st, x + nx + ((width - 2) / 2), y + ny + 65 + week * 30);
		
		
	}
	
	public static void main(String[] args) {
		HashMap<Date, String> data = new HashMap<Date, String> ();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, Calendar.SEPTEMBER);
		showCalendar(data, c);
	}
	
	
	public static void showCalendar(HashMap<Date, String> data, Calendar c) {
		JFrame frame = new JFrame("Calendar");
		Container container = frame.getContentPane();
		CalendarIconExample iconExample = new CalendarIconExample(c,true,-100,10, data);
		container.add(iconExample);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
