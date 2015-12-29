package com.adventofcode;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day18 {
	static Logger log = LoggerFactory.getLogger(Day18.class);
	static final int MAX_TEASPOON = 100;
	static final int FIXED_CALORIES = 500;

	public static void main(String... args) throws Exception {
		Day18 day18 = new Day18();
		day18.firstStar();
		day18.secondStar();
	}

	void secondStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("day18_1.txt"), Charsets.UTF_8);
		boolean[][] panel = parseInput(inputs);
		turnOnCorner(panel);
	
		for (int i=0; i<100; i++) {
			panel = nextState(panel);
			turnOnCorner(panel);
		}
		
		long numberOfLightsOn = lightsAreOn(panel);
		
		log.debug("Number of lights are on in second star: {}", numberOfLightsOn);
	}

	
	void firstStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("day18_1.txt"), Charsets.UTF_8);
		boolean[][] panel = parseInput(inputs);
	
		for (int i=0; i<100; i++) {
			panel = nextState(panel);
		}
		
		long numberOfLightsOn = lightsAreOn(panel);
		
		log.debug("Number of lights are on in first star: {}", numberOfLightsOn);			
	}
	
	void turnOnCorner(boolean[][] panel) {
		panel[0][0] = true;
		panel[panel.length-1][0] = true;
		panel[0][panel.length-1] = true;
		panel[panel.length-1][panel.length-1] = true;
	}

	private long lightsAreOn(final boolean[][] finalPanel) {
		return IntStream.range(0, finalPanel.length).boxed().flatMap(i -> 
									IntStream.range(0, finalPanel[i].length).mapToObj(j -> finalPanel[i][j])
								)
								.filter(l -> l).count();
	}
	
	@Test
	public void printTestStates() throws IOException {
		List<String> inputs = Resources.readLines(Resources.getResource("day18_test.txt"), Charsets.UTF_8);
		boolean[][] panel = parseInput(inputs);
		
		log.debug(toString(panel));
		log.debug(toString(nextState(panel)));
	}
	
	String toString(boolean[][] panel) {
		StringBuffer sb = new StringBuffer("\n");
		
		IntStream.range(0, panel.length).forEach(i -> {
			IntStream.range(0, panel[i].length).forEach(j -> {
				if (panel[i][j]) 
					sb.append('#');
				else
					sb.append('.');
			});
			sb.append("\n");
		});
		return sb.toString();
	}
	
	boolean[][] parseInput(List<String> inputs) {
		boolean[][] panel = new boolean[inputs.get(0).length()][inputs.size()];
		
		IntStream.range(0, inputs.size()).forEach(i -> {
			String s = inputs.get(i);
			IntStream.range(0, inputs.get(i).length()).forEach(j -> {
				if (s.charAt(j) == '#') 
					panel[i][j] = true;
				else
					panel[i][j] = false;
			});
		});
		return panel;
	}
	
	boolean[][] nextState(boolean[][] panel) {
		boolean[][] nextPanel = new boolean[panel.length][panel[0].length];
		
		IntStream.range(0, panel.length).forEach(i -> {
			IntStream.range(0, panel[i].length).forEach(j -> {
				nextPanel[i][j] = shouldOn(i, j, panel);
			});
		});
		
		return nextPanel;
	}
	
	boolean shouldOn(int x, int y, boolean[][] panel) {
		long onNeighbors = numberOfneighborsOn(x, y, panel);
		if (panel[x][y]) {
			return onNeighbors == 3 || onNeighbors == 4;
		} else {
			return onNeighbors == 3;
		}
	}
	
	long numberOfneighborsOn(int x, int y, boolean[][] panel) {
		return IntStream.rangeClosed(x-1, x+1).boxed().flatMap(i -> {
				return IntStream.rangeClosed(y-1, y+1).mapToObj(j -> isOn(i, j, panel));
		}).filter(l -> l).count();
	}
	
	boolean isOn(int x, int y, boolean[][] panel) {
		try {
			return panel[x][y];
		} catch (Exception e) {
			return false;
		}
	}
}