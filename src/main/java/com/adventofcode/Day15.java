package com.adventofcode;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day15 {
	static Logger log = LoggerFactory.getLogger(Day15.class);
	static final int MAX_TEASPOON = 100;
	static final int FIXED_CALORIES = 500;

	public static void main(String... args) throws Exception {
		Day15 day15 = new Day15();
		day15.firstStar();
		day15.secondStar();
	}

	void secondStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("day15_1.txt"), Charsets.UTF_8);
		List<Ingredient> ingredients = parseIngredient(inputs);

		int max = addIngredient(new int[ingredients.size()], 0, ingredients)
				.filter(recipe -> calculateCalories(recipe, ingredients) == FIXED_CALORIES)
				.mapToInt(recipe -> calculateScore(recipe, ingredients))
				.max().getAsInt();
		log.debug("Highest score of second star: {}", max);			
	}

	void firstStar() throws Exception {
		List<String> inputs = Resources.readLines(Resources.getResource("day15_1.txt"), Charsets.UTF_8);
		List<Ingredient> ingredients = parseIngredient(inputs);

		int max = addIngredient(new int[ingredients.size()], 0, ingredients)
				.mapToInt(l -> calculateScore(l, ingredients))
				.max().getAsInt();
		log.debug("Highest score of first star: {}", max);			
	}

	private Stream<int[]> addIngredient(int[] recipe, int i, List<Ingredient> ingredients) {
		if (i >= recipe.length) return Stream.of(recipe);
		
		return IntStream.rangeClosed(0, emptyIngredients(recipe)).boxed()
			.flatMap(j -> {
				int[] newProperties = Arrays.copyOf(recipe, recipe.length);
				newProperties[i] = j;
				return addIngredient(newProperties, i+1, ingredients);
			});
	}

	private int emptyIngredients(int[] properties) {
		return MAX_TEASPOON - Arrays.stream(properties).sum();
	}

	private List<Ingredient> parseIngredient(List<String> inputs) {
		return inputs.stream()
			.map(i -> {
				String[] arr = i.split(" ");
			return new Ingredient(arr[0].replaceAll(":", ""), 
					Integer.valueOf(arr[2].replaceAll(",", "")), 
					Integer.valueOf(arr[4].replaceAll(",", "")), 
					Integer.valueOf(arr[6].replaceAll(",", "")), 
					Integer.valueOf(arr[8].replaceAll(",", "")), 
					Integer.valueOf(arr[10].replaceAll(",", "")));
			})
			.collect(Collectors.toList());
	}
	
	static class Ingredient {
		final String name;
		final int capacity;
		final int durability;
		final int flavor;
		final int texture;
		final int calories;

		private Ingredient(String name, int capacity, int durability, int flavor, int texture, int calories) {
			this.name = name;
			this.capacity = capacity;
			this.durability = durability;
			this.flavor = flavor;
			this.texture = texture;
			this.calories = calories;
		}
	}
	
	private int calculateCalories(int[] recipe, List<Ingredient> ingredients) {
		return IntStream.range(0, recipe.length)
			.map(i -> recipe[i] * ingredients.get(i).calories)
			.sum();
	}
		
	private int calculateScore(int[] recipe, List<Ingredient> ingredients) {
		int[] sumProperties = IntStream.range(0, recipe.length).mapToObj(i -> {
			int[] arr = new int[4];
			arr[0] = recipe[i] * ingredients.get(i).capacity;
			arr[1] = recipe[i] * ingredients.get(i).durability;
			arr[2] = recipe[i] * ingredients.get(i).flavor;
			arr[3] = recipe[i] * ingredients.get(i).texture;
			return arr;
		}).reduce((a1,a2) -> {
			int[] arr = new int[4];
			arr[0] = a1[0] + a2[0];
			arr[1] = a1[1] + a2[1];
			arr[2] = a1[2] + a2[2];
			arr[3] = a1[3] + a2[3];
			return arr;
		}).get();
		
		return Arrays.stream(sumProperties)
				.map(i -> (i<0) ? 0 : i )
				.reduce((a,b) -> a * b).getAsInt();
	}

	@Test
	public void givenIngredients_shouldFindOutMaxScore() {
		Ingredient butterscotch = new Ingredient("Butterscotch", -1, -2, 6, 3, 8);
		Ingredient cinnamon = new Ingredient("Cinnamon", 2, 3, -2, -1, 3);

		List<Ingredient> ingredients = Arrays.asList(butterscotch, cinnamon);
				
		int max = addIngredient(new int[ingredients.size()], 0, ingredients)
					.mapToInt(recipe -> calculateScore(recipe, ingredients))
					.max().getAsInt();
		
		assertEquals(62842880, max);
	}
	
	@Test
	public void givenIngredients_andFixedCalories_shouldFindOutMaxScore() {
		Ingredient butterscotch = new Ingredient("Butterscotch", -1, -2, 6, 3, 8);
		Ingredient cinnamon = new Ingredient("Cinnamon", 2, 3, -2, -1, 3);

		List<Ingredient> ingredients = Arrays.asList(butterscotch, cinnamon);
				
		int max = addIngredient(new int[ingredients.size()], 0, ingredients)
					.filter(recipe -> calculateCalories(recipe, ingredients) == FIXED_CALORIES)
					.mapToInt(recipe -> calculateScore(recipe, ingredients))
					.max().getAsInt();
		
		assertEquals(57600000, max);
	}

}