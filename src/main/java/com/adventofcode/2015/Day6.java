package com.adventofcode;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day6 {
	final static String inputFile = "2015/day6_1.txt";
	final static Logger log = LoggerFactory.getLogger(Day6.class);
	final static int maxX = 1000;
	final static int maxY = 1000;
	
	public static void main(String... args) throws Exception {
		boolean[][] lights = new boolean[maxX][maxY];
		execute(lights, new Instruction("turn on 0,0 through 999,999"));
		log.info("Total light on: {}", calculateLightOn(lights));
		
		firstStar();
		secondStar();		
	}
	
	
	private static void secondStar() throws Exception {
		List<String> input = readInputFromFile();
		int[][] lights = new int[maxX][maxY];
		input.stream()
			.map(s -> new Instruction(s))
			.forEach(i -> execute(lights, i));
		
		int result = calculateLightness(lights);
		log.info("Total lightless: {}", result);
	}		
	
	private static int calculateLightness(int[][] lights) {
		AtomicInteger count = new AtomicInteger(0);
		IntStream.range(0, maxX).forEach(x -> {
			IntStream.range(0, maxY).forEach(y -> {
				count.getAndAdd(lights[x][y]);
			});
		});
		return count.get();
	}


	public static void firstStar() throws Exception {
		List<String> input = readInputFromFile();
		boolean[][] lights = new boolean[maxX][maxY];
		input.stream()
			.map(s -> new Instruction(s))
			.forEach(i -> execute(lights, i));
		
		int result = calculateLightOn(lights);
		log.info("Total light on: {}", result);
	}
	
	private static int calculateLightOn(boolean[][] lights) {
		AtomicInteger count = new AtomicInteger(0);
		IntStream.range(0, maxX).forEach(x -> {
			IntStream.range(0, maxY).forEach(y -> {
				if (lights[x][y]) count.getAndAdd(1);
			});
		});
		return count.get();
	}

	static class Instruction {
		final ACTION action;
		final int fromX;
		final int fromY;
		final int toX;
		final int toY;
		
		public Instruction(String in) {
			if (in.startsWith("turn on")) 		action = ACTION.TURNON;
			else if (in.startsWith("turn off"))	action = ACTION.TURNOFF;
			else								action = ACTION.TOGGLE;
			
			Scanner scanner = new Scanner(in).useDelimiter("[^\\d]+");
			fromX = scanner.nextInt();
			fromY = scanner.nextInt();
			toX = scanner.nextInt();
			toY = scanner.nextInt();
		}
	}
	
	enum ACTION {
		TURNON(b->true, 1), TURNOFF(b->false, -1), TOGGLE(b->!b, 2);
		
		final Function<Boolean, Boolean> turnLight;
		final int adjustment;
		
		private ACTION(Function<Boolean, Boolean> action, int adjustment) {
			turnLight = action;
			this.adjustment = adjustment;
		}
		
		public int adjust(int exist) {
			exist += adjustment;
			if (exist < 0) 	return 0;
			else 			return exist;
		}

	}
	
	static void execute(boolean[][] lights, Instruction inst) {
		IntStream.range(inst.fromX, inst.toX+1).forEach(x -> 
			IntStream.range(inst.fromY, inst.toY+1).forEach(y -> 
				lights[x][y] = inst.action.turnLight.apply(lights[x][y])
			)
		);
	}
	
	static void execute(int[][] lights, Instruction inst) {
		IntStream.range(inst.fromX, inst.toX+1).forEach(x -> 
			IntStream.range(inst.fromY, inst.toY+1).forEach(y -> 
				lights[x][y] = inst.action.adjust(lights[x][y])
			)
		);
	}
	
	private static List<String> readInputFromFile() throws IOException {
		return Resources.readLines(Resources.getResource(inputFile), Charsets.UTF_8);
	}
}
