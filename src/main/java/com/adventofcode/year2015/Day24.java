package com.adventofcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day24 {
	static Logger log = LoggerFactory.getLogger(Day24.class);
	
	public static void main(String... args) throws Exception {
		Day24 day24 = new Day24();
		day24.firstStar();
		day24.secondStar();
	}

	void secondStar() throws Exception {
		final int numberOfPackages = 4;
		List<Integer> weights = Resources.readLines(Resources.getResource("2015/day24_1.txt"), Charsets.UTF_8).stream()
				.map(Integer::valueOf)
				.sorted((x, y) -> y-x)
				.collect(Collectors.toList());
		int balanceWeight = weights.stream().mapToInt(w -> w).sum() / numberOfPackages;
		
		List<Packages> QESortedPackages = packagesLessThanMax(new Packages(), weights, balanceWeight, weights.size() / numberOfPackages)
							.sorted((p1, p2) -> {
								if (p1.weights.size() == p2.weights.size())
									return (p1.calculateQE() - p2.calculateQE()) > 0 ? 1 : -1;
								else
									return p1.weights.size() - p2.weights.size();
							})
							.collect(Collectors.toList());
		
		Packages bestPackages = QESortedPackages.stream()
							.filter(p -> canBalanceBy4Packages(p, weights, balanceWeight))
							.findFirst().get();
		
		log.info("the smallest quantum entanglement in second star: {}", bestPackages.calculateQE());
	}

	
	void firstStar() throws Exception {
		final int numberOfPackages = 3;
		List<Integer> weights = Resources.readLines(Resources.getResource("2015/day24_1.txt"), Charsets.UTF_8).stream()
								.map(Integer::valueOf)
								.sorted((x, y) -> y-x)
								.collect(Collectors.toList());
		int balanceWeight = weights.stream().mapToInt(w -> w).sum() / numberOfPackages;
		
		List<Packages> QESortedPackages = packagesLessThanMax(new Packages(), weights, balanceWeight, weights.size() / numberOfPackages)
									.sorted((p1, p2) -> {
										if (p1.weights.size() == p2.weights.size())
											return (p1.calculateQE() - p2.calculateQE()) > 0 ? 1 : -1;
										else
											return p1.weights.size() - p2.weights.size();
									})
									.collect(Collectors.toList());
		
		Packages bestPackages = QESortedPackages.stream()
									.filter(p -> canBalanceBy3Packages(p, weights, balanceWeight))
									.findFirst().get();
				
		log.info("the smallest quantum entanglement in first star: {}", bestPackages.calculateQE());
	}
	
	boolean canBalanceBy4Packages(Packages firstPackages, List<Integer> reverseSortedWeights, int balanceWeight) {
		List<Integer> subWeights = reverseSortedWeights.stream().filter(w -> !firstPackages.weights.contains(w)).collect(Collectors.toList());
		
		return packagesLessThanMax(new Packages().addWeight(subWeights.get(0)), subWeights.subList(1, subWeights.size()), balanceWeight, subWeights.size())
					.anyMatch(p2 -> {
						List<Integer> w3 = subWeights.stream().filter(w -> !p2.weights.contains(w)).collect(Collectors.toList());
						return canBalanceBy3Packages(p2, w3, balanceWeight); 
					});

	}

	boolean canBalanceBy3Packages(Packages firstPackages, List<Integer> reverseSortedWeights, int balanceWeight) {
		List<Integer> subWeights = reverseSortedWeights.stream().filter(w -> !firstPackages.weights.contains(w)).collect(Collectors.toList());
		
		return packagesLessThanMax(new Packages().addWeight(subWeights.get(0)), subWeights.subList(1, subWeights.size()), balanceWeight, subWeights.size())
					.anyMatch(secondPackages -> subWeights.stream().filter(w -> !secondPackages.weights.contains(w)).mapToInt(x -> x).sum() == balanceWeight);
	}
	
	Stream<Packages> packagesLessThanMax(Packages group, List<Integer> weight ,int balanceWeight, int maxPackages) {
		//log.debug("{},{},{},{}", group, weight);
		if (group.totalWeight == balanceWeight)
			return Stream.of(group);
		else if (group.totalWeight > balanceWeight || weight.size() == 0) 
			return Stream.empty();
		else if (group.weights.size() >= maxPackages)
			return Stream.empty();
		
		Stream<Packages> packagesWithoutFirstPackage = packagesLessThanMax(group, weight.subList(1, weight.size()), balanceWeight, maxPackages);
		Stream<Packages> packagesWithFirstPackage;		
		if (group.totalWeight + weight.get(0) > balanceWeight)
			packagesWithFirstPackage = Stream.empty();
		else 
			packagesWithFirstPackage = packagesLessThanMax(group.addWeight(weight.get(0)), weight.subList(1, weight.size()), balanceWeight, maxPackages);
					
		return Stream.concat(packagesWithFirstPackage, packagesWithoutFirstPackage);
	}
	
	class Packages {
		final int totalWeight;
		final List<Integer> weights;
		
		public Packages() {
			this.totalWeight = 0;
			this.weights = Collections.emptyList();
		}
		
		private Packages(int totalWeight, List<Integer> weights) {
			this.totalWeight = totalWeight;
			this.weights = weights;
		}
		
		public Packages addWeight(int weight) {
			List<Integer> newList = new LinkedList<>(weights);
			newList.add(weight);
			return new Packages(totalWeight + weight, newList);
		}
		
		public long calculateQE() {
			return weights.stream().mapToLong(a -> a).reduce((x,y) -> x*y).getAsLong();
		}

		@Override
		public String toString() {
			return String.format("Packages [totalWeight=%s, weights=%s]", totalWeight, weights);
		}
		
	}
		
	@Test
	public void packagesLessThanMax_shouldReturnPossiblePackagesLessThanMaxPackages() throws Exception {
		List<Integer> weights = Arrays.asList(1, 2, 3, 4, 5, 7, 8, 9, 10, 11);
		Collections.reverse(weights);
		int balanceWeight = weights.stream().mapToInt(s -> s).sum() / 3;
				
		List<Packages> groups = packagesLessThanMax(new Packages(), weights, balanceWeight, weights.size() / 3).collect(Collectors.toList());
		groups.forEach(g -> log.debug(g.toString()));
		
		assertEquals(10, groups.size());
		groups.forEach(p -> assertTrue(p.weights.contains(weights.get(0))));
	}
	}