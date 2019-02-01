package com.adventofcode;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Day11 {
	static Logger log = LoggerFactory.getLogger(Day11.class);

	public static void main(String... args) {
		test();
		firstStar();
		secondStar();
	}

	private static void secondStar() {
		log.info("Next password after vzbxxyzz is {}", nextPassword("vzbxxyzz"));
	}

	private static void firstStar() {
		log.info("Next password after vzbxkghb is {}", nextPassword("vzbxkghb"));
	}

	private static void test() {
		log.info("Next password after abcdefgh is {}", nextPassword("abcdefgh"));
		log.info("Next password after ghijklmn is {}", nextPassword("ghijklmn"));
	}

	private static String nextPassword(String oldPassword) {
		String password = increment(oldPassword);
		
		while (!atLeastThreeStraightIncreasingLetters(password)
				|| !notContainsInvalidCharacters(password)
				|| !containAtLeastTwoDifferentPairs(password)) {
			password = increment(password);
		}

		return password;
	}

	private static boolean containAtLeastTwoDifferentPairs(String password) {
		Optional<Character> firstPairChar = IntStream.range(0, password.length() - 1)
				.filter(i -> password.charAt(i) == password.charAt(i + 1))
				.mapToObj(i -> password.charAt(i))
				.findFirst();

		if (!firstPairChar.isPresent())
			return false;

		char firstChar = firstPairChar.get();
		return IntStream.range(0, password.length() - 1)
				.anyMatch(i -> password.charAt(i) == password.charAt(i + 1) && password.charAt(i) != firstChar);

	}

	private static boolean notContainsInvalidCharacters(String password) {
		return !password.contains("i") && !password.contains("o") && !password.contains("l");
	}

	private static boolean atLeastThreeStraightIncreasingLetters(String password) {
		return IntStream.range(0, password.length() - 2)
				.anyMatch(i -> password.charAt(i) == password.charAt(i + 1) - 1 && password.charAt(i + 1) == password.charAt(i + 2) - 1);
	}

	private static String increment(String org) {
		if (org.length() == 1)
			return increment(org.charAt(0));

		String subStr = increment(org.substring(1));
		if (subStr.matches("^a*$"))
			return increment(org.charAt(0)) + subStr;
		else
			return org.charAt(0) + subStr;
	}

	private static String increment(char c) {
		c += 1;
		String newChar = String.valueOf(c);

		if ("{".equals(newChar))
			return "a";
		else
			return newChar;
	}

}