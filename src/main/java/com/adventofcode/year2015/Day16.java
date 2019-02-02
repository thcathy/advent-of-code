package com.adventofcode;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day16 {
	static Logger log = LoggerFactory.getLogger(Day16.class);
	static final int MAX_TEASPOON = 100;
	static final int FIXED_CALORIES = 500;

	public static void main(String... args) throws Exception {
		Day16 day16 = new Day16();
		day16.firstStar();
		day16.secondStar();
	}

	void secondStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("2015/day16_1.txt"), Charsets.UTF_8);
		List<AuntSue> aunts = parseAuntSue(inputs);
		final Map<String, Integer> tickerTape = createTickerTape();
		
		AuntSue sue = aunts.stream()
			.filter(a -> a.compounds.entrySet().stream().allMatch(e -> 
					isMatchReading(tickerTape, e)
				)
			)
			.findFirst().get();
		
		log.debug("Number of Aunt Sue in first star: {}", sue.id);	
	}

	boolean isMatchReading(Map<String, Integer> tickerTape, Entry<String, Integer> e) {
		String key = e.getKey();
		if ("cats".equals(key) || "trees".equals(key)) 
			return e.getValue() > tickerTape.get(key);
		else if ("pomeranians".equals(key) || "goldfish".equals(key))
			return e.getValue() < tickerTape.get(key);
		else
			return tickerTape.containsKey(key) && tickerTape.get(key).equals(e.getValue());
	}

	void firstStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("2015/day16_1.txt"), Charsets.UTF_8);
		List<AuntSue> aunts = parseAuntSue(inputs);
		final Map<String, Integer> tickerTape = createTickerTape();
		
		AuntSue sue = aunts.stream()
			.filter(a -> a.compounds.entrySet().stream().allMatch(e -> 
					tickerTape.containsKey(e.getKey()) && tickerTape.get(e.getKey()).equals(e.getValue())
				)
			)
			.findFirst().get();

		assert("103".equals(sue.id));
		log.debug("Number of Aunt Sue in first star: {}", sue.id);			
	}
	
	Map<String, Integer> createTickerTape() {
		return Collections.unmodifiableMap(Stream.of(
                new SimpleEntry<>("children", 3),
                new SimpleEntry<>("cats", 7),
                new SimpleEntry<>("samoyeds", 2),
                new SimpleEntry<>("pomeranians", 3),
                new SimpleEntry<>("akitas", 0),
                new SimpleEntry<>("vizslas", 0),
                new SimpleEntry<>("goldfish", 5),
                new SimpleEntry<>("trees", 3),
                new SimpleEntry<>("cars", 2),
                new SimpleEntry<>("perfumes", 1))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
	}
	
	List<AuntSue> parseAuntSue(List<String> inputs) {
		return inputs.stream()
			.map(s -> {
				String[] arr = s.split(" ");
				Map<String, Integer> compounds = new HashMap<>();
				compounds.put(arr[2].replace(":", ""), Integer.valueOf(arr[3].replace(",", "")));
				compounds.put(arr[4].replace(":", ""), Integer.valueOf(arr[5].replace(",", "")));
				compounds.put(arr[6].replace(":", ""), Integer.valueOf(arr[7].replace(",", "")));
				return new AuntSue(arr[1].replace(":", ""), compounds);
			})
			.collect(Collectors.toList());
	}
	
	static class AuntSue {
		final String id;
		final Map<String, Integer> compounds;
		
		AuntSue(String id, Map<String, Integer> compounds) {
			super();
			this.id = id;
			this.compounds = compounds;
		}		
	}
	
}