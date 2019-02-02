package com.adventofcode;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Day25 {
	static Logger log = LoggerFactory.getLogger(Day25.class);
	
	public static void main(String... args) throws Exception {
		Day25 day25 = new Day25();
		day25.firstStar();
		day25.secondStar();
	}

	void secondStar() throws Exception {
		
	}

	
	void firstStar() throws Exception {
		long start = 20151125;
		int row = 2978;
		int col = 3083;
		
		long code = calculateCode(start, row, col);
		log.info("code for first star: {}", code);
	}
	
	long calculateCode(long start, int row, int col) {
		long sequence = sequenceFromLocation(row, col);
		long code = start;
		for (long i=1l; i < sequence; i++) {
			code = (code * 252533) % 33554393;
		}
		return code;		
	}
	
	long sequenceFromLocation(int row, int col) {
		return triangleNumber(row + col - 2) + (row + col)-row;
	}
	
	long triangleNumber(long n) {
        return n*(n+1)/2;
    }
	
	@Test
	public void test_calculateCode() {
		long start = 20151125;
		assertEquals(20151125, calculateCode(start, 1, 1));
		assertEquals(31916031, calculateCode(start, 2, 1));
		assertEquals(18749137, calculateCode(start, 1, 2));
		assertEquals(1601130, calculateCode(start, 3, 3));
		assertEquals(16474243, calculateCode(start, 3, 6));
		assertEquals(77061, calculateCode(start, 5, 1));
		assertEquals(27995004, calculateCode(start, 6, 6));
	}
	
	@Test
	public void test_sequenceFromLocation() {
		assertEquals(1, sequenceFromLocation(1, 1));
		assertEquals(18, sequenceFromLocation(4, 3));
		assertEquals(21, sequenceFromLocation(1, 6));
		assertEquals(16, sequenceFromLocation(6, 1));
	}
	
	@Test
	public void test_triangleNumber() throws Exception {
		assertEquals(10, triangleNumber(4));
		assertEquals(15, triangleNumber(5));
		assertEquals(18352711, triangleNumber(6058));
	}
}