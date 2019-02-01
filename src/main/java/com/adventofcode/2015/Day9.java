package com.adventofcode;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day9 {
	final static String inputFile = "2015/day9_1.txt";
	final static Logger log = LoggerFactory.getLogger(Day9.class);
	
	public static void main(String... args) throws Exception {
		test1();
		firstStar();
		secondStar();		
	}
	
	static void test1() throws Exception {
		List<String> input = Arrays.asList("London to Dublin = 464", "London to Belfast = 518", "Dublin to Belfast = 141");
		HashMap<String, Location> locations = buildLocations(input);
		locations.values().forEach(l -> log.debug(l.toString()));
		
		int result = locations.values().stream()
			.flatMap(l -> visit(new Journey(l.name), l, locations))
			.mapToInt(j -> j.distances)
			.min().getAsInt();
				
		log.info("Min distance for test1: {}", result);
	}
			
	static void secondStar() throws Exception {
		HashMap<String, Location> locations = buildLocations(readInputFromFile(inputFile));
		
		int result = locations.values().stream()
			.flatMap(l -> visit(new Journey(l.name), l, locations))
			.mapToInt(j -> j.distances)
			.max().getAsInt();
				
		log.info("Max distance for second star: {}", result);
	}	
		
	static void firstStar() throws Exception {
		HashMap<String, Location> locations = buildLocations(readInputFromFile(inputFile));
		
		int result = locations.values().stream()
			.flatMap(l -> visit(new Journey(l.name), l, locations))
			.mapToInt(j -> j.distances)
			.min().getAsInt();
				
		log.info("Min distance for first star: {}", result);
	}
	
	static Stream<Journey> visit(Journey j, Location from, Map<String, Location> listOfLocation) {
		if (j.visited.size() == listOfLocation.size()) 
			return Stream.of(j);
		
		return from.destinations.entrySet().stream()
			.filter(e -> !j.visited.contains(e.getKey()))
			.flatMap(e -> visit(j.visit(e.getKey(), e.getValue()), listOfLocation.get(e.getKey()), listOfLocation));
	}
	
	private static HashMap<String, Location> buildLocations(List<String> input) {
		return (HashMap<String, Location>) input.stream()
			.flatMap(s -> parseDistances(s))
			.collect(Collectors.groupingBy(l -> l.name))
			.values().stream()
			.map(list -> mergeToSingleLocation(list))
			.collect(Collectors.toMap(l -> l.name, l -> l));
	}
		
	private static Stream<Location> parseDistances(String s) {
		String[] strArr = s.split("=");
		String[] locations = strArr[0].split(" to ");
		return Stream.of(
					new Location(locations[0].trim(), locations[1].trim(), Integer.parseInt(strArr[1].trim())),
					new Location(locations[1].trim(), locations[0].trim(), Integer.parseInt(strArr[1].trim()))
				);
	}
	
	private static Location mergeToSingleLocation(List<Location> locations) {
		 Map<String, Integer> destinations = locations.stream().flatMap(l -> l.destinations.entrySet().stream())
			.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		 return new Location(locations.get(0).name, destinations);
			
	}
	
	static class Journey {
		final Set<String> visited;
		final int distances;
		
		public Journey(Set<String> visited, int distances) {
			this.visited = visited;
			this.distances = distances;
		}

		public Journey(String name) {
			visited = new HashSet<>();
			visited.add(name);
			this.distances = 0;
		}

		public Journey visit(String name, int distance) {
			Set<String> newVisited = new HashSet<>(visited);
			newVisited.add(name);
			return new Journey(newVisited, distances + distance);
		}

		@Override
		public String toString() {
			return "Journey [visited=" + visited + ", distances=" + distances + "]";
		}
		
	}
	
	static class Location {
		final String name;
		final Map<String, Integer> destinations;
		
		public Location(String name) {
			this.name = name;
			this.destinations = new HashMap<>();
		}
		
		public Location(String name, String destName, Integer distance) {
			this.name = name;
			this.destinations = new HashMap<>();
			destinations.put(destName, distance);
		}
		
		private Location(String name, Map<String, Integer> destinations) {
			this.name = name;
			this.destinations = destinations;
		}

		public Location addDestination(String destName, Integer distance) {
			Map<String, Integer> dest = new HashMap<>(destinations);
			dest.put(destName, distance);
			return new Location(this.name, dest);
		}
		
		@Override
		public String toString() {
			return "Location [name=" + name + ", destinations=" + destinations + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Location other = (Location) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
		
	private static List<String> readInputFromFile(String filename) throws IOException {
		return Resources.readLines(Resources.getResource(filename), Charsets.UTF_8);
	}
}
