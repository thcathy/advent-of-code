package com.adventofcode.year2015;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

public class Day4 {
	final static Logger log = LoggerFactory.getLogger(Day4.class);
	static MessageDigest md = null;
	
	public static void main(String... args) throws Exception {
		md = MessageDigest.getInstance("MD5");
		firstStar();
		secondStar();		
	}
	
	
	private static void secondStar() throws Exception {
		String input = "bgvyzdsv";
		
		for (int i=1; i<Integer.MAX_VALUE; i++) {
			String hash = md5Hex(input + String.valueOf(i));
			if (hash.startsWith("000000")) {
				log.info("First hash with 000000: {}", i);
				break;
			}
		}
	}

	public static void firstStar() throws Exception {
		String input = "bgvyzdsv";
		
		for (int i=1; i<Integer.MAX_VALUE; i++) {
			String hash = md5Hex(input + String.valueOf(i));
			if (hash.startsWith("00000")) {
				log.info("First hash with 00000: {}", i);
				break;
			}
		}
	}
	
	private static String md5Hex(String in) {
		md.update(in.getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
	
}
