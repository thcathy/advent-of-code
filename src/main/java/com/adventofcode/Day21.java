package com.adventofcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Day21 {
	static Logger log = LoggerFactory.getLogger(Day21.class);
	static final List<Equipment> weapons = Arrays.asList(
											new Equipment("Dagger", 8, 4, 0),
											new Equipment("Shortsword", 10, 5, 0),
											new Equipment("Warhammer", 25, 6, 0),
											new Equipment("Longsword", 40, 7, 0),
											new Equipment("Greataxe", 74, 8, 0)
										);
	static final List<Equipment> armors = Arrays.asList(
											new Equipment("None", 0, 0, 0),
											new Equipment("Leather", 13, 0, 1),
											new Equipment("Chainmail", 31, 0, 2),
											new Equipment("Splintmail", 53, 0, 3),
											new Equipment("Bandedmail", 75, 0, 4),
											new Equipment("Platemail", 102, 0, 5)
										);
	static final List<Equipment> rings = Arrays.asList(
											new Equipment("None", 0, 0, 0),
											new Equipment("Damage +1", 25, 1, 0),
											new Equipment("Damage +2", 50, 2, 0),
											new Equipment("Damage +3", 100, 3, 0),
											new Equipment("Defense +1", 20, 0, 1),
											new Equipment("Defense +2", 40, 0, 2),
											new Equipment("Defense +3", 80, 0, 3)
										);

	public static void main(String... args) throws Exception {
		Day21 day21 = new Day21();
		day21.firstStar();
		day21.secondStar();
	}

	void secondStar() throws Exception {
		final int playerHP = 100;
		final int bossHP = 109;
		final int bossDamage = 8;
		final int bossArmor = 2;
		
		int cost = inventoriesSortedByCost().stream()
				.filter(i -> !canWin(playerHP, i.totalDamage(), i.totalArmor(), bossHP, bossDamage, bossArmor))
				.mapToInt(Inventory::totalCost)
				.max().getAsInt();
			
		log.debug("Max cost failed to default boss in second star: {}", cost);
	}

	
	void firstStar() throws Exception {
		final int playerHP = 100;
		final int bossHP = 109;
		final int bossDamage = 8;
		final int bossArmor = 2;
		
		int cost = inventoriesSortedByCost().stream()
			.filter(i -> canWin(playerHP, i.totalDamage(), i.totalArmor(), bossHP, bossDamage, bossArmor))
			.mapToInt(Inventory::totalCost)
			.findFirst().getAsInt();
		
		log.debug("Min cost to default boss in first star: {}", cost);
	}
	
	boolean canWin(int playerHP, int playerDamage, int playerArmor, int bossHP, int bossDamage, int bossArmor) {
		int playerRealDamage = playerDamage > bossArmor ? playerDamage - bossArmor : 1;
		int bossRealDamage = bossDamage > playerArmor ? bossDamage - playerArmor : 1;
		
		while (playerHP > 0 && bossHP > 0) {
			bossHP -= playerRealDamage;
			playerHP -= bossRealDamage;
		}
		return bossHP <= 0;
	}
	
	static class Equipment {
		final String name;
		final int cost;
		final int damage;
		final int armor;
		
		Equipment(String name, int cost, int damage, int armor) {
			this.name = name;
			this.cost = cost;
			this.damage = damage;
			this.armor = armor;
		}

		@Override
		public String toString() {
			return "Equipment [name=" + name + ", cost=" + cost + ", damage=" + damage + ", armor=" + armor + "]";
		}
	}
	
	static class Inventory {
		final Equipment weapon;
		final Equipment armor;
		final Equipment ring1;
		final Equipment ring2;
		
		Inventory(Equipment weapon, Equipment armor, Equipment ring1, Equipment ring2) {
			this.weapon = weapon;
			this.armor = armor;
			this.ring1 = ring1;
			this.ring2 = ring2;
		}
		
		int totalCost() {
			return weapon.cost + armor.cost + ring1.cost + ring2.cost;
		}
		
		int totalDamage() {
			return weapon.damage + ring1.damage + ring2.damage;
		}
		
		int totalArmor() {
			return armor.armor + ring1.armor + ring2.armor;
		}

		@Override
		public String toString() {
			return "Inventory [weapon=" + weapon.name + ", armor=" + armor.name + ", ring1=" + ring1.name + ", ring2=" + ring2.name + "]";
		}
		
	}
	
	List<Inventory> inventoriesSortedByCost() {
		return weapons.stream()
			.flatMap(w -> {
				return armors.stream()
					.flatMap(a -> {
						return rings.stream()
							.flatMap(r1 -> {
								return rings.stream()
									.filter(r2 -> (!r2.name.equals(r1.name) && !r1.name.equals("None")) || (r1.name.equals("None") && r2.name.equals("None")))
									.map(r2 -> new Inventory(w, a, r1, r2));
							});
					});
			})
			.sorted((x,y) -> x.totalCost() - y.totalCost())
			.collect(Collectors.toList());
	}
	
	@Test
	public void calculateContainersCombinations() {
		List<Inventory> inventories = inventoriesSortedByCost(); 
		
		assertEquals(1110, inventories.size());
		IntStream.range(0, inventories.size()-1).forEach(i -> {
			assertTrue(inventories.get(i).totalCost() <= inventories.get(i+1).totalCost());
		});
	}

}