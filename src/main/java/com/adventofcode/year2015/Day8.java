package com.adventofcode;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day8 {
	final static String inputFile = "2015/day8_1.txt";
	final static String testFile = "2015/day8_test.txt";
	final static Logger log = LoggerFactory.getLogger(Day8.class);
	
	public static void main(String... args) throws Exception {
		test1();
		firstStar();
		
		test2();
		secondStar();		
	}
	
	static void test1() throws Exception {
		List<String> input = readInputFromFile(testFile);
		
		int totalMemorySize = input.stream()
								.mapToInt(String::length)
								.sum();
		
		int totalRealSize = input.stream()
								.mapToInt(Day8::calculateRealSize)
								.sum();
		
		log.info("[test1] Total Memory Size: {}, total real size: {}, difference: {}", totalMemorySize, totalRealSize, totalMemorySize - totalRealSize);
	}
	
	static void test2() throws Exception {
		List<String> input = readInputFromFile(testFile);
		List<String> transformed = input.stream()
			.map(Day8::transform)
			.collect(Collectors.toList());
		
		int totalMemorySize = transformed.stream()
				.mapToInt(String::length)
				.sum();

		int totalRealSize = transformed.stream()
				.mapToInt(Day8::calculateRealSize)
				.sum();

		log.info("[test2] Total Memory Size: {}, total real size: {}, difference: {}", totalMemorySize, totalRealSize, totalMemorySize - totalRealSize);
	}
		
	static void secondStar() throws Exception {
		List<String> input = readInputFromFile(inputFile);
		List<String> transformed = input.stream()
			.map(Day8::transform)
			.collect(Collectors.toList());
		
		int totalMemorySize = transformed.stream()
				.mapToInt(String::length)
				.sum();

		int totalRealSize = transformed.stream()
				.mapToInt(Day8::calculateRealSize)
				.sum();

		log.info("[secondStar] Total Memory Size: {}, total real size: {}, difference: {}", totalMemorySize, totalRealSize, totalMemorySize - totalRealSize);
	}	
		
	static void firstStar() throws Exception {
		List<String> input = readInputFromFile(inputFile);
		
		int totalMemorySize = input.stream()
								.mapToInt(String::length)
								.sum();
		
		int totalRealSize = input.stream()
								.mapToInt(Day8::calculateRealSize)
								.sum();
		
		log.info("[firstStar] Total Memory Size: {}, total real size: {}, difference: {}", totalMemorySize, totalRealSize, totalMemorySize - totalRealSize);
	}
	
	static int calculateRealSize(String s) {
		char[] charArr = s.substring(1, s.length()-1).toCharArray();
		
		int size=0, pos=0;
		while (pos < charArr.length) {
			if (charArr[pos] == '\\' && pos+1 < charArr.length && charArr[pos+1] == 'x') {
				pos += 4;
			} else if (charArr[pos] == '\\') {
				pos += 2;
			} else {
				pos += 1;
			}
			size++;
		}
		
		//log.debug("String '{}' real size is {}", s, size);
		return size;
	}
	
	static String transform(String s) {
		StringBuffer sb = new StringBuffer("\"\\\"");
		
		char[] charArr = s.substring(1, s.length()-1).toCharArray();
		int pos=0;
		while (pos < charArr.length) {
			sb.append(charArr[pos]);
			
			if (charArr[pos] == '\\' && pos+1 < charArr.length && charArr[pos+1] == 'x') {
				sb.append("\\");
			} else if (charArr[pos] == '\\' && pos+1 < charArr.length && charArr[pos+1] == '\\') {
				sb.append("\\\\\\");
				pos++;
			} else if (charArr[pos] == '\\') {
				sb.append("\\\\");
			}
			
			pos++;
		}
		sb.append("\\\"\"");
		log.debug("{} is transformed to {}", s, sb.toString());
		return sb.toString();
	}
	
	private static List<String> readInputFromFile(String filename) throws IOException {
		return Resources.readLines(Resources.getResource(filename), Charsets.UTF_8);
	}
}
