package com.adventofcode.year2015;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Day13 {
	static Logger log = LoggerFactory.getLogger(Day13.class);
	static final String MYSELF = "Myself";

	public static void main(String... args) throws Exception {
		Day13 day13 = new Day13();
		day13.firstStar();
		day13.secondStar();
	}

	void secondStar() throws Exception {
		List<String> input = Resources.readLines(Resources.getResource("2015/day13_1.txt"), Charsets.UTF_8);
		List<String> peopleIncludeMe = addMyself(getPeople(input));
		Map happinessMapIncludeMe = addMyself(peopleIncludeMe, constructHappinessMap(input));

		int maxHappinessWithMyself = maxHappinessOfSeating(peopleIncludeMe, happinessMapIncludeMe);
		log.info("Max happiness of second star: {}", maxHappinessWithMyself);
	}

	private List<String> addMyself(List<String> people) {
		List<String> newList = new ArrayList<>(people);
		newList.add(MYSELF);
		return newList;
	}

	Map<String, Integer> addMyself(List<String> people, Map<String, Integer> happinessMap) {
		Map<String, Integer> newHappinessMap = new HashMap<>(happinessMap);
		people.forEach(p -> {
			newHappinessMap.put(p + "," + MYSELF, 0);
			newHappinessMap.put(MYSELF + "," + p, 0);
		});
		return newHappinessMap;
	}

	void firstStar() throws Exception {
		List input = Resources.readLines(Resources.getResource("day13_1.txt"), Charsets.UTF_8);
		int maxHappiness = maxHappinessOfSeating(getPeople(input), constructHappinessMap(input));
		log.info("Max happiness of first star: {}", maxHappiness);
	}

	Stream<List<String>> possibleSeating(List<String> people) {
		if (people.size() == 1)
			return Stream.of(people);

		return people.stream().flatMap(p -> {
			List<String> subList = new ArrayList<>(people);
			subList.remove(p);
			return possibleSeating(subList).map(l -> {
				l.add(p);
				return l;
			});
		});
	}

	Map<String, Integer> constructHappinessMap(List<String> input) {
		Map<String, Integer> happinessMap = new ConcurrentHashMap<>(input.size() * 2);

		input.forEach(l -> {
			String[] inputs = l.split(" ");
			int happiness = Integer.valueOf(inputs[3]);
			if ("lose".equals(inputs[2]))
				happiness *= -1;
			happinessMap.put(inputs[0] + "," + inputs[10].replace(".", ""), happiness);
		});

		return happinessMap;
	}

	List<String> getPeople(List<String> input) {
		return input.stream().map(l -> l.split(" ")[0]).distinct().collect(Collectors.toList());
	}

	int maxHappinessOfSeating(List<String> people, Map<String, Integer> happinessMap) {
		return possibleSeating(people).mapToInt(s -> calculateHappiness(s, happinessMap)).max().getAsInt();
	}

	int calculateHappiness(List<String> seating, Map<String, Integer> happinessMap) {
		seating.add(seating.get(0));
		return IntStream.range(0, seating.size() - 1)
				.map(i -> happinessMap.get(seating.get(i) + "," + seating.get(i + 1))
						+ happinessMap.get(seating.get(i + 1) + "," + seating.get(i)))
				.sum();
	}

	@Test
	public void possibleSeating_given3People_shouldReturn6List() {	
		List<List<String>> seatings = possibleSeating(Arrays.asList("a","b","c")).collect(Collectors.toList());
		assertEquals(6, seatings.size());
	}

	@Test
	public void constructHappinessMap_given2Lines_shouldReturn4Entry() {
		Map<String, Integer> map = constructHappinessMap(Arrays.asList("Alice would gain 54 happiness units by sitting next to Bob.",
				"Carol would lose 62 happiness units by sitting next to Alice."));
		assertEquals(map.size(), 2);
		assertEquals(54, map.get("Alice,Bob").intValue());
		assertEquals(-62, map.get("Carol,Alice").intValue());
	}

	@Test
	public void maxHappinessOfSeating_givenNormalInput_shouldReturnMaxHappiness() {
		List<String> input = Arrays.asList("Alice would gain 54 happiness units by sitting next to Bob.",
				"Alice would lose 79 happiness units by sitting next to Carol.",
				"Alice would lose 2 happiness units by sitting next to David.",
				"Bob would gain 83 happiness units by sitting next to Alice.",
				"Bob would lose 7 happiness units by sitting next to Carol.",
				"Bob would lose 63 happiness units by sitting next to David.",
				"Carol would lose 62 happiness units by sitting next to Alice.",
				"Carol would gain 60 happiness units by sitting next to Bob.",
				"Carol would gain 55 happiness units by sitting next to David.",
				"David would gain 46 happiness units by sitting next to Alice.",
				"David would lose 7 happiness units by sitting next to Bob.",
				"David would gain 41 happiness units by sitting next to Carol.");

		constructHappinessMap(input).forEach((x, y) -> log.debug("{} -> {}", x, y));

		int max = maxHappinessOfSeating(getPeople(input), constructHappinessMap(input));
		assertEquals(330, max);
	}

}
