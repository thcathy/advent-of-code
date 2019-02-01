package com.adventofcode;

import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day1 {
	final static String inputFile = "2015/day1_1.txt";
	
	public static void main(String... args) throws Exception {
		firstStar();
		secondStar();		
	}
	
	private static void secondStar() throws Exception {
		List<String> lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
		int[] intArray = lines.stream()
				.flatMapToInt(String::chars)
				.map(Day1::mapToValue)
				.toArray();
		
		int level = 0;
		for (int i=0; i < intArray.length; i++) {
			level += intArray[i];
			if (level == -1) {
				System.out.println(i+1);
				return;
			}
		}
		
	}

	public static void firstStar() throws Exception {
		List<String> lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
		int result = lines.stream()
			.flatMapToInt(String::chars)
			.map(Day1::mapToValue)
			.sum();
		System.out.println(result);
	}
	
	public static int mapToValue(int in) {
		if (in == 40) 		return 1;
		else if (in == 41) 	return -1;
		else 				return 0;
	}
}
