package com.adventofcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Day10 {
	final static Logger log = LoggerFactory.getLogger(Day10.class);
	
	public static void main(String... args) throws Exception {
		test1();
		firstStar();
		secondStar();		
	}
	
	static void test1() throws Exception {
		String pattern = "1";
		
		for (int i=1; i<=5; i++) {
			pattern = lookAndSay(pattern);
			log.debug("Turn {} pattern: {}", i, pattern);
		}
	}
			
	private static String lookAndSay(String pattern) {
		StringBuffer sb = new StringBuffer();
		
		int pos = 0;
		int count = 1;
		int length = pattern.length();
		while (pos < length) {
			if (pos+1 < length && pattern.charAt(pos) == pattern.charAt(pos+1)) {
				count++;
			} else {
				sb.append(count);
				sb.append(pattern.charAt(pos));
				count = 1;
			}
			pos++;
		}
		return sb.toString();
	}

	static void secondStar() throws Exception {
		String pattern = "1321131112";
		
		for (int i=1; i<=50; i++) {
			pattern = lookAndSay(pattern);
		}
		log.info("Second Star pattern.length: {}", pattern.length());
	}	
		
	static void firstStar() throws Exception {
		String pattern = "1321131112";
		
		for (int i=1; i<=40; i++) {
			pattern = lookAndSay(pattern);
		}
		log.info("First Star pattern.length: {}", pattern.length());
	}
}