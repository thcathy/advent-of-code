package com.adventofcode;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Day20 {
	static Logger log = LoggerFactory.getLogger(Day20.class);
	
	public static void main(String... args) throws Exception {
		Day20 day20 = new Day20();
		day20.firstStar();
		day20.secondStar();
	}

	void secondStar() throws Exception {
		log.debug("Start calculate for second star");
		int presents = 36000000;
		
		int house = IntStream.range(presents/45, presents)
			.filter(s -> totalVisitedElvesNumberWhichStopDeliverAfter50House(s) * 11 >= presents)
			.findFirst()
			.getAsInt();
		
		log.debug("House {} is the first one get at least {} presents in second star", house, presents);	
	}
	
	void firstStar() throws Exception {
		log.debug("Start calculate for first star");
		int presents = 36000000;
		
		int house = IntStream.range(presents/45, presents)
			.filter(s -> totalVisitedElvesNumber(s) * 10 >= presents)
			.findFirst()
			.getAsInt();
		
		log.debug("House {} is the first one get at least {} presents in first star", house, presents);			
	}
	
	int totalVisitedElvesNumberWhichStopDeliverAfter50House(int house) {
		return IntStream.rangeClosed(1, (int)Math.sqrt(house))
						.filter(i -> house % i == 0)
						.flatMap(i -> IntStream.of(i, house / i))
						.filter(i -> i * 50 >= house)
						.sum();
	}
		
	int totalVisitedElvesNumber(int house) {
		return IntStream.rangeClosed(1, (int)Math.sqrt(house))
					.filter(i -> house % i == 0)
					.flatMap(i -> IntStream.of(i, house / i))
					.sum();
	}
	
	@Test
	public void totalVisitedElvesNumber_shouldCalculateCorrectly() throws IOException {
		assertEquals(1, totalVisitedElvesNumber(1));
		assertEquals(3, totalVisitedElvesNumber(2));
		assertEquals(8, totalVisitedElvesNumber(7));
		assertEquals(15, totalVisitedElvesNumber(8));
		assertEquals(13, totalVisitedElvesNumber(9));
	}
}	