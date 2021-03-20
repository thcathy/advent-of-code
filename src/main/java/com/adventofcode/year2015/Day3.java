package com.adventofcode.year2015;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day3 {
	final static String inputFile = "2015/day3_1.txt";
	final static Logger log = LoggerFactory.getLogger(Day3.class);
	
	public static void main(String... args) throws Exception {
		firstStar();
		secondStar();		
	}
	
	
	private static void secondStar() throws Exception {
		String input = readInputFromFile();
		
		List<DIRECTION> directions = input.chars()
			.mapToObj(DIRECTION::valueOf)
			.collect(Collectors.toList());
		
		Set<Point> visited = new HashSet<>();
		Point pos1 = new Point(0,0);
		Point pos2 = new Point(0,0);
		visited.add(pos1);
		int turn = 1;
		for (DIRECTION p : directions) {
			if (turn % 2 == 1) {
				pos1 = p.move(pos1);
				visited.add(pos1);
			} else {
				pos2 = p.move(pos2);
				visited.add(pos2);
			}
			turn++;
		}
		log.info("Total visited: {}", visited.size());
	}


	private static String readInputFromFile() throws IOException {
		return Resources.toString(Resources.getResource(inputFile), Charsets.UTF_8);
	}

	public static void firstStar() throws Exception {
		String input = readInputFromFile();
		
		List<DIRECTION> directions = input.chars()
			.mapToObj(DIRECTION::valueOf)
			.collect(Collectors.toList());
		
		Set<Point> visited = new HashSet<>();
		Point lastVisited = new Point(0,0); 
		visited.add(lastVisited);
		for (DIRECTION p : directions) {
			lastVisited = p.move(lastVisited);
			visited.add(lastVisited);
		}
		log.info("Total visited: {}", visited.size());
	}
	
	enum DIRECTION  {
		NORTH(94, 0, 1), EAST(62, 1, 0), SOUTH(118, 0, -1), WEST(60, -1, 0);
		
		final int charValue;
		final int xTransition;
		final int yTransition;
		DIRECTION(int charValue, int xTransition, int yTransition) {
			this.charValue = charValue;
			this.xTransition = xTransition;
			this.yTransition = yTransition;
		}
		
		Point move(Point org) {
			return new Point(org.x + xTransition, org.y + yTransition);
		}
		
		static DIRECTION valueOf(int charValue) {
			if (charValue == NORTH.charValue) return NORTH;
			else if (charValue == EAST.charValue) return EAST;
			else if (charValue == SOUTH.charValue) return SOUTH;
			else return WEST;			
		}
	}
	
	static class Point {
		final int x;
		final int y;
		
		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
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
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		
		
	}
	
}
