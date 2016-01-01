package com.adventofcode;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day19 {
	static Logger log = LoggerFactory.getLogger(Day19.class);
	
	public static void main(String... args) throws Exception {
		Day19 day19 = new Day19();
		day19.firstStar();
		day19.secondStar();
	}

	void secondStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("day19_1.txt"), Charsets.UTF_8);
		String finalMolecule = inputs.get(inputs.size()-1);
		
		List<String> elements = parseElements(inputs.subList(0, inputs.size()-2));
		int totalElements = numberOfElements(finalMolecule, elements);
		int totalRn = finalMolecule.split("Rn").length - 1;
		int totalAr = finalMolecule.split("Ar").length - 1;
		int totalY = finalMolecule.split("Y").length - 1;
		
		log.debug("Number of elements in final molecule: {}", totalElements);
		log.debug("Number of steps to produce final molecule: {}", totalElements - totalRn - totalAr - 2 * totalY - 1);
		
		//log.debug("Number of iteration to produce final molecule: {}", result.get());
	}
	
	private int numberOfElements(String finalMolecule, List<String> elements) {
		return elements.stream()
			.mapToInt(e -> finalMolecule.split(e).length-1)
			.sum() + 1;
	}

	private List<String> parseElements(List<String> input) {
		List<String> elements = input.stream()
			.map(s -> s.split(" => ")[0])
			.distinct()
			.collect(Collectors.toList());
		elements.add("Rn");
		elements.add("Ar");
		elements.add("Y");
		return elements;
	}
	
	void firstStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("day19_1.txt"), Charsets.UTF_8);
		Map<String, List<String>> replacements = parseReplacements(inputs.subList(0, inputs.size()-2));
		String molecule = inputs.get(inputs.size()-1);

		long uniqueMolecule = uniqueGeneratedMolecule(molecule, replacements);

		log.debug("Number of unique molecules in first star: {}", uniqueMolecule);			
	}
	
	
	Map<String, List<String>> parseReplacements(List<String> subList) {
		return subList.stream()
			.map(s -> {
				String[] arr = s.split(" => ");
				return new SimpleEntry<>(arr[0], arr[1]);
			})
			.collect(Collectors.groupingBy(e -> e.getKey(), 
					 Collectors.mapping(e -> e.getValue(), Collectors.toList()))
			);
	}

	@Test
	public void testUniqueGeneratedMolecule() throws IOException {
		Map<String, List<String>> replacements = Collections.unmodifiableMap(Stream.of(
							                new SimpleEntry<>("H", Arrays.asList("HO", "OH")),
							                new SimpleEntry<>("O", Arrays.asList("HH")))
							                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
		String molecule = "HOH";
		
		assertEquals(4, uniqueGeneratedMolecule(molecule, replacements));
	}
	
	long uniqueGeneratedMolecule(String molecule, Map<String, List<String>> replacements) {
		return generateMolecule(molecule, replacements).distinct().count();
	}
	
	Stream<String> generateMolecule(String molecule, Map<String, List<String>> replacements) {
		return IntStream.range(0, molecule.length())
			.boxed()
			.flatMap(i -> Stream.concat(
					generateMoleculeAtPosition(molecule, i, i+1, replacements),
					generateMoleculeAtPosition(molecule, i, i+2, replacements))
			);
	}
	
	Stream<String> generateMoleculeAtPosition(String molecule, int i, int j, Map<String, List<String>> replacements) {
		if (j > molecule.length()) return Stream.empty();
		
		String chars = molecule.substring(i, j);
		if (replacements.containsKey(chars)) 
			return replacements.get(chars).stream().map(s -> molecule.substring(0, i) + s + molecule.substring(j, molecule.length()));
		else
			return Stream.empty();
	}
}	