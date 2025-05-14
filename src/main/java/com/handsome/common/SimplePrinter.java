package com.handsome.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SimplePrinter {

	private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static int pokerDisplayFormat = 0;

	public static void printNotice(String msg) {
		System.out.println(msg);
	}

	public static void printNotice(String msgKey, String locale) {
		//TODO : read locale
		Map<String, Map<String, String>> map = new HashMap<>();
		map.put("english", new HashMap<>());
		map.get("eng").put("caterpillar", "caterpillar's message!!");

		System.out.println(map.get(locale).get(msgKey));
	}

	public static void serverLog(String msg) {
		System.out.println(FORMAT.format(new Date()) + "-> " + msg);
	}
}
