package com.adventofcode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day5 {
	final static String inputFile = "day5_1.txt";
	final static Logger log = LoggerFactory.getLogger(Day5.class);
	final static List<String> invalidStrings = Arrays.asList("ab", "cd", "pq", "xy");
	
	public static void main(String... args) throws Exception {
		firstStar();
		secondStar();		
	}
	
	
	private static void secondStar() throws Exception {
		List<String> input = readInputFromFile();
		
		long result = input.parallelStream()
			.filter(Day5::containsPairAppearsAtLeastTwice)
			.filter(Day5::containsLetterRepeatWithOneCharInBetween)
			.count();
		
		log.info("Total valid strings: {}", result);
	}	
	
	static boolean containsPairAppearsAtLeastTwice(String s) {
		return IntStream.range(0, s.length()-1)
			.anyMatch(i -> s.indexOf(s.substring(i, i+2), i+2) > -1);
	}
	
	static boolean containsLetterRepeatWithOneCharInBetween(String s) {
		return IntStream.range(0, s.length()-2)
				.anyMatch(i -> s.charAt(i) == s.charAt(i+2));
	}
	
	public static void firstStar() throws Exception {
		List<String> input = readInputFromFile();
		
		long result = input.parallelStream()
			.filter(Day5::containsAtLeastThreeVowels)
			.filter(Day5::containsAtLeastOneLetterContinue)
			.filter(Day5::notContainInvalidString)
			.count();
		
		log.info("Total valid strings: {}", result);
	}
	
	static boolean notContainInvalidString(String s) {
		for (String invalidString : invalidStrings) {
			if (s.contains(invalidString)) return false;
		}
		return true;
	}
	
	static boolean containsAtLeastThreeVowels(String s) {
		long count = s.chars().filter(c -> c==97 || c==101 || c==105 || c==111 || c==117)
			.limit(3)
			.count();
		return count >= 3;
	}
	
	static boolean containsAtLeastOneLetterContinue(String s) {
		char[] chars = s.toCharArray();
		int maxPos = chars.length-1;
		for (int i=0; i<maxPos; i++) {
			if (chars[i] == chars[i+1]) return true;
		}
		return false;
	}
	
	private static List<String> readInputFromFile() throws IOException {
		return Resources.readLines(Resources.getResource(inputFile), Charsets.UTF_8);
	}
}
