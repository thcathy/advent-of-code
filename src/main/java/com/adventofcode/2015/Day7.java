package com.adventofcode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day7 {
	final static String inputFile = "2015/day7_1.txt";
	final static Logger log = LoggerFactory.getLogger(Day7.class);
	
	public static void main(String... args) throws Exception {
		System.out.println(LogicGate.build("lx -> a"));
		
		firstStar();
		secondStar();		
	}
		
	private static void secondStar() throws Exception {
		List<String> input = readInputFromFile();
		
		Map<String, LogicGate> gates = input.stream()
			.map(LogicGate::build)
			.collect(Collectors.toMap(g -> g.name, g -> g));
		
		gates.put("b", new LogicGate("b", LOGIC.CONST, "46065", null));
		
		log.info("Gate a is {}", (int)gateValue(gates.get("a"), gates));
	}
	
	public static void firstStar() throws Exception {
		List<String> input = readInputFromFile();
		
		Map<String, LogicGate> gates = input.stream()
			.map(LogicGate::build)
			.collect(Collectors.toMap(g -> g.name, g -> g));
		
		gates.values().forEach(g -> log.info(g.toString()));
		
		log.info("Gate a is {}", (int)gateValue(gates.get("a"), gates));
		
	}
	
	static char gateValue(LogicGate g, Map<String, LogicGate> gates) {
		log.debug("get gate value of {}", g);
		char v1, v2;
		if (StringUtils.isNumeric(g.in1)) {
			v1 = (char) Integer.parseInt(g.in1);
		} else {
			v1 = gateValue(gates.get(g.in1), gates);
		}
		
		if (g.in2 == null) {
			v2 = 0;
		} else if (StringUtils.isNumeric(g.in2)) {
			v2 = (char) Integer.parseInt(g.in2);
		} else {
			v2 = gateValue(gates.get(g.in2), gates);
		}
		char result = g.logic.exec.apply(v1, v2);
		gates.put(g.name, new LogicGate(g.name, LOGIC.CONST, String.valueOf((int)result), null));
		return g.logic.exec.apply(v1, v2);
	}
	
	

	static class LogicGate {
		final String name;
		final LOGIC logic;
		final String in1;
		final String in2;
		
		public LogicGate(String name, LOGIC logic, String in1, String in2) {
			super();
			this.name = name;
			this.logic = logic;
			this.in1 = in1;
			this.in2 = in2;
		}
		
		public static LogicGate build(String s) {
			String[] arr = s.split("->");
			LOGIC logic = LOGIC.match(arr[0]);
			String[] operand = arr[0].split(logic.toString());
			
			if (operand.length == 1)
				return new LogicGate(arr[1].trim(), logic, operand[0].trim(), null);
			else if (logic.equals(LOGIC.NOT))
				return new LogicGate(arr[1].trim(), logic, operand[1].trim(), null);
			else
				return new LogicGate(arr[1].trim(), logic, operand[0].trim(), operand[1].trim());
		}
		
		@Override
		public String toString() {
			return "LogicGate [name=" + name + ", logic=" + logic + ", in1=" + in1 + ", in2=" + in2 + "]";
		}
	}
	
	enum LOGIC {
		CONST((x,y) -> x), 
		OR((x,y) -> (char)(x | y)),
		AND((x,y) -> (char)(x & y)),
		NOT((x,y) -> (char)~x),
		LSHIFT((x,y) -> (char)(x << y)),
		RSHIFT((x,y) -> (char)(x >> y));
		
		final BiFunction<Character, Character, Character> exec;
				
		private LOGIC(BiFunction<Character, Character, Character> f) {
			exec = f;
		}

		public static LOGIC match(String s) {
			for (LOGIC l : values()) {
				if (s.contains(l.toString())) return l;
			}
			return CONST;
		}
		
	}
			
	private static List<String> readInputFromFile() throws IOException {
		return Resources.readLines(Resources.getResource(inputFile), Charsets.UTF_8);
	}
}
