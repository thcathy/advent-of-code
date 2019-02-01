package com.adventofcode;

import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day2 {
	final static String inputFile = "2015/day2_1.txt";
	final static Logger log = LoggerFactory.getLogger(Day2.class);
	
	public static void main(String... args) throws Exception {
		firstStar();
		secondStar();		
	}
	
	private static void secondStar() throws Exception {
		List<String> lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
		int result = lines.stream()
				.map(s -> s.split("x"))
				.mapToInt(Day2::calculateRibbonLength)
				.sum();
		log.info("Total Ribbon length: {}", result);
	}

	public static void firstStar() throws Exception {
		List<String> lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
		int result = lines.stream()
			.map(s -> s.split("x"))
			.mapToInt(Day2::calculateWrappingPaperArea)
			.sum();
		log.warn("Total area: {}", result);
	}
	
	public static int calculateRibbonLength(String[] in) {
		int w = Integer.valueOf(in[0]);
		int h = Integer.valueOf(in[1]);
		int l = Integer.valueOf(in[2]);
		
		return IntStream.of(
				2*w + 2*h,
				2*w + 2*l,
				2*l + 2*h
				)
				.min().getAsInt() + w * h * l;
	}
	
	public static int calculateWrappingPaperArea(String[] in) {
		int w = Integer.valueOf(in[0]);
		int h = Integer.valueOf(in[1]);
		int l = Integer.valueOf(in[2]);
		
		return IntStream.of(
				2*w*h + 2*w*l + 2*h*l + w*h,
				2*w*h + 2*w*l + 2*h*l + w*l,
				2*w*h + 2*w*l + 2*h*l + h*l
				)
				.min().getAsInt();
	}
}
