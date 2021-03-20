package com.adventofcode.year2015;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Day17 {
	static Logger log = LoggerFactory.getLogger(Day17.class);
	static final int MAX_TEASPOON = 100;
	static final int FIXED_CALORIES = 500;

	public static void main(String... args) throws Exception {
		Day17 day17 = new Day17();
		day17.firstStar();
		day17.secondStar();
	}

	void secondStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("2015/day17_1.txt"), Charsets.UTF_8);
		List<Container> containers = parseContainer(inputs);
		final int liters = 150;
		
		Map<Integer, List<List<Container>>> combinations = fillContainers(Collections.emptyList(), liters, containers)
										.collect(Collectors.groupingBy(l -> l.size()));
		
		int minContainersUsed = combinations.keySet().stream().mapToInt(i -> i).min().getAsInt();
		int combinationUsedMinContainer = combinations.get(minContainersUsed).size();
		log.debug("Number of combinations used min containers in second star: {}", combinationUsedMinContainer);		
	}

	
	void firstStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("day17_1.txt"), Charsets.UTF_8);
		List<Container> containers = parseContainer(inputs);
		final int liters = 150;
		
		long numberOfCombinations = fillContainers(Collections.emptyList(), liters, containers).count();
		log.debug("Number of combinations in first star: {}", numberOfCombinations);			
	}
	
	
	
	private List<Container> parseContainer(List<String> inputs) {
		return inputs.stream()
			.map(s -> new Container(Integer.valueOf(s)))
			.collect(Collectors.toList());
	}
	
	static class Container {
		final int size;
		
		Container(int size) {
			this.size = size;
		}		
		
		@Override 
		public String toString() {
			return String.valueOf(size);
		}
	}
	
	@Test
	public void calculateContainersCombinations() {
		List<Container> containers = Arrays.asList(new Container(20), new Container(15), new Container(10), new Container(5), new Container(5));
		containers.sort((c1, c2) -> c2.size - c1.size);
		final int liters = 25;
		
		long numberOfCombinations = fillContainers(Collections.emptyList(), liters, containers).count();
		
		assertEquals(4, numberOfCombinations);
	}

	Stream<List<Container>> fillContainers(List<Container> combinations, int liters, List<Container> containers) {
		if (liters == 0) 
			return Stream.of(combinations);
		else if (liters < 0 || containers.isEmpty())
			return Stream.empty();
		
		Container largestContainer = containers.get(0);
		final List<Container> newCombinations = new LinkedList<>(combinations);
		final int litersLeft = liters - largestContainer.size;
		newCombinations.add(largestContainer);
	
		return Stream.concat(
				fillContainers(combinations, liters, containers.subList(1, containers.size())),
				fillContainers(newCombinations, litersLeft, containers.subList(1, containers.size()))
			);
	}
}
