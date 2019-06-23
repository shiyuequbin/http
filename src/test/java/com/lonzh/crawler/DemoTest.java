package com.lonzh.crawler;

import java.util.Calendar;

public class DemoTest {

	public static void main(String[] args) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	    Long today = c.getTime().getTime();
	    System.out.println(today);
		
	}

}
