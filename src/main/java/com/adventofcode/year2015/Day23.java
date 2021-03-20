package com.adventofcode.year2015;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class Day23 {
	static Logger log = LoggerFactory.getLogger(Day23.class);
	
	public static void main(String... args) throws Exception {
		Day23 day23 = new Day23();
		day23.firstStar();
		day23.secondStar();
	}

	void secondStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("2015/day23_1.txt"), Charsets.UTF_8);
		Map<String, Long> registers = new HashMap<>();
		registers.put("a", 1l);
		registers.put("b", 0l);
		compute(inputs, registers);

		log.debug("a={}, b={} in second star", registers.get("a"), registers.get("b"));
	}

	
	void firstStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("2015/day23_1.txt"), Charsets.UTF_8);
		Map<String, Long> registers = new HashMap<>();
		registers.put("a", 0l);
		registers.put("b", 0l);
		compute(inputs, registers);

		log.debug("a={}, b={} in first star", registers.get("a"), registers.get("b"));
	}
	
		
	static enum Instruction {
		hlf(x -> x/2, x -> false),
		tpl(x -> x*3, x -> false),
		inc(x -> x+1, x -> false),
		jmp(x -> x, x -> true),
		jie(x -> x, x -> x % 2 == 0),
		jio(x -> x, x -> x == 1);
		
		final Function<Long, Long> transform;
		final Function<Long, Boolean> isOffset;
		
		private Instruction(Function<Long, Long> transform, Function<Long, Boolean> isOffset) {
			this.transform = transform;
			this.isOffset = isOffset;
		}
	}
	
	Map<String, Integer> createRegisters() {
		Map<String, Integer> registers = new HashMap<>();
		registers.put("a", 0);
		registers.put("b", 0);
		return registers;
	}
	
	void compute(List<String> inputs, Map<String, Long> registers) {
		int i = 0;
		
		while (i < inputs.size()) {
			String[] arr = inputs.get(i).split(" ");
			Instruction inst = Instruction.valueOf(arr[0]);
			String register = arr[1].replaceAll(",", "");
			registers.put(register, inst.transform.apply(registers.get(register)));
			
			if (inst.isOffset.apply(registers.get(register))) 
				if (arr.length < 3)
					i += Integer.valueOf(arr[1]);
				else
					i += Integer.valueOf(arr[2]);
			else
				i++;
		}
	}
		
	@Test
	public void registerShouldEqualToOne() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("2015/day23_test.txt"), Charsets.UTF_8);
		Map<String, Long> registers = new HashMap<>();
		registers.put("a", 0l);
		registers.put("b", 0l);
		compute(inputs, registers);
		
		assertEquals(2, registers.get("a").intValue());
		assertEquals(0, registers.get("b").intValue());
	}
	
}
