package com.adventofcode;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import javafx.util.Pair;

public class Day14 {
	static Logger log = LoggerFactory.getLogger(Day14.class);
	static final String MYSELF = "Myself";

	public static void main(String... args) throws Exception {
		Day14 day14 = new Day14();
		day14.firstStar();
		day14.secondStar();
	}

	void secondStar() throws Exception {
		final int sec = 2503;
		List<String> inputs = Resources.readLines(Resources.getResource("day14_1.txt"), Charsets.UTF_8);
		List<Deer> deers = parseDeer(inputs);
		
		IntStream.rangeClosed(1, sec)
			.mapToObj(i -> {
				return deers.stream()
					.map(d -> new Pair<Integer, Deer>(distanceAtSec(d, i), d))
					.collect(Collectors.groupingBy(Pair::getKey));
			})
			.flatMap(m -> {
				Integer max = m.keySet().stream().mapToInt(k -> k).max().getAsInt();
				return m.get(max).stream();
			})
			.map(p -> p.getValue().name);
	}

	void firstStar() throws Exception {
		final int sec = 2503;
		List<String> inputs = Resources.readLines(Resources.getResource("day14_1.txt"), Charsets.UTF_8);
		int longestDistance = parseDeer(inputs).stream()
								.mapToInt(d -> distanceAtSec(d, sec))
								.max().getAsInt();
		log.info("Max distance travel by deer: {} km at {} sec", longestDistance, sec);
	}

	static class Deer {
		final String name;
		final int speed;
		final int flySecs;
		final int restSecs;
		
		public Deer(String name, int speed, int flySecs, int resetSecs) {
			super();
			this.name = name;
			this.speed = speed;
			this.flySecs = flySecs;
			this.restSecs = resetSecs;
		}		
	}
	
	List<Deer> parseDeer(List<String> inputs) {
		return inputs.stream()
				.map(s -> {
					String[] arr = s.split(" ");
					return new Deer(arr[0], Integer.valueOf(arr[3]), Integer.valueOf(arr[6]), Integer.valueOf(arr[13]));
				})
				.collect(Collectors.toList());
	}
	
	int distanceAtSec(Deer deer, int time) {
		int cycle = (int) time / (deer.flySecs + deer.restSecs);
		
		int secNotInCycle = time % (deer.flySecs + deer.restSecs);
		if (secNotInCycle > deer.flySecs) secNotInCycle = deer.flySecs;
		
		return cycle * deer.flySecs * deer.speed 
				+ secNotInCycle * deer.speed;
	}
	
	@Test
	public void distanceAtSec_givenInput_shouldCalculateCorrectly() {
		List<String> inputs = Arrays.asList(
				"Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.",
				"Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.");
		List<Deer> deers = parseDeer(inputs);
		
		assertEquals(14, distanceAtSec(deers.get(0), 1));
		assertEquals(140, distanceAtSec(deers.get(0), 10));
		assertEquals(140, distanceAtSec(deers.get(0), 11));
		assertEquals(154, distanceAtSec(deers.get(0), 138));
		assertEquals(176, distanceAtSec(deers.get(1), 138));
		assertEquals(1120, distanceAtSec(deers.get(0), 1000));
		assertEquals(1056, distanceAtSec(deers.get(1), 1000));
	}
	
	@Test
	public void parseDeer_givenInput_shouldCreatedDeer() {
		List<String> inputs = Arrays.asList(
								"Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.",
								"Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.");
		
		List<Deer> deers = parseDeer(inputs);
		assertEquals(2, deers.size());
		assertEquals("Comet", deers.get(0).name);
		assertEquals("Dancer", deers.get(1).name);
		assertEquals(14, deers.get(0).speed);
		assertEquals(16, deers.get(1).speed);
		assertEquals(10, deers.get(0).flySecs);
		assertEquals(11, deers.get(1).flySecs);
		assertEquals(127, deers.get(0).restSecs);
		assertEquals(162, deers.get(1).restSecs);
		
	}
}