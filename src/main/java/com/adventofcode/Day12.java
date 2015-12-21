package com.adventofcode;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day12 {
	static Logger log = LoggerFactory.getLogger(Day12.class);

	public static void main(String... args) throws Exception {
		test();
		firstStar();
		secondStar();
	}

	private static void secondStar() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Object document = mapper.readValue(Resources.getResource("day12_1.txt"), Object.class);

		log.info("sum of document ignored 'red' is {}", sumIgnoreRed(document).sum());
	}

	private static void firstStar() throws Exception {
		List<String> input = Resources.readLines(Resources.getResource("day12_1.txt"), Charsets.UTF_8);
		log.info("sum of first document is {}", sum(input.get(0)));
	}

	private static void test() {
		log.info("sum of [1,2,3] is {}", sum("[1,2,3]"));
		log.info("sum of {\"a\":2,\"b\":4} is {}", sum("{\"a\":2,\"b\":4}"));
		log.info("sum of [[[3]]] is {}", sum("[[[3]]]"));
		log.info("sum of {\"a\":{\"b\":4},\"c\":-1} is {}", sum("{\"a\":{\"b\":4},\"c\":-1}"));
		log.info("sum of {\"a\":[-1,1]} is {}", sum("{\"a\":[-1,1]}"));
		log.info("sum of [-1,{\"a\":1}] is {}", sum("[-1,{\"a\":1}]"));
		log.info("sum of [] is {}", sum("[]"));
	}

	@SuppressWarnings({ "unchecked" })
	private static IntStream sumIgnoreRed(Object a) {
		if (a instanceof Integer)
			return IntStream.of((int) a);
		else if (a instanceof List<?>)
			return ((List<Object>) a).stream().flatMapToInt(Day12::sumIgnoreRed);
		else if (a instanceof Map && !containsValueRed(a))
			return ((Map<?, ?>) a).values().stream().flatMapToInt(Day12::sumIgnoreRed);
		return IntStream.empty();

	}

	private static boolean containsValueRed(Object a) {
		return ((Map<?, ?>) a).containsValue("red");
	}

	private static int sum(String string) {
		int total = 0;
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(string);

		while (m.find()) {
			total += Integer.valueOf(m.group());
		}

		return total;
	}

}