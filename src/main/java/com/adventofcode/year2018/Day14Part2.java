package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class Day14Part2 {
    final static String puzzleInput = "580741";
    
    public static void main(String... args) throws IOException {
        Day14Part2 solution = new Day14Part2();
        solution.run();
    }

    void run() throws IOException {        
        var result = totalRecipeWhenPatternAppear(puzzleInput);
        System.out.println("How many recipes appear on the scoreboard to the left of the score sequence in your puzzle input? " + result);
    }

    int totalRecipeWhenPatternAppear(String scorePattern) {        
        var state = new State();
        while (true) {
            nextRound(state);
            if (patternAppear(state.recipes, scorePattern, 0))
                return state.recipes.size() - scorePattern.length();
            if (patternAppear(state.recipes, scorePattern, 1))
                return state.recipes.size() - scorePattern.length() - 1;
        }
    }

    boolean patternAppear(List<Integer> recipes, String scorePattern, int offset) {
        if (recipes.size() - offset < scorePattern.length()) return false;
        
        for (int i = 0; i < scorePattern.length(); i++) {
            if (recipes.get(recipes.size() - scorePattern.length() + i - offset) != Character.getNumericValue(scorePattern.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }

    void nextRound(State state) {
        state.makeRecipes();
        state.elf1Position = nextPosition(state.elf1Position, state.recipes.get(state.elf1Position), state.recipes.size());
        state.elf2Position = nextPosition(state.elf2Position, state.recipes.get(state.elf2Position), state.recipes.size());
    }

    class State {
        List<Integer> recipes = startRecipes();
        int elf1Position = 0;
        int elf2Position = 1;

        void makeRecipes() {
            int value = recipes.get(elf1Position) + recipes.get(elf2Position);
            recipes.addAll(toList(value));
        }
    }

    void makeRecipes(List<Integer> recipes, int score1, int score2) {
        int value = score1 + score2;
        recipes.addAll(toList(value));
    }

    List<Integer> toList(int value) {
        LinkedList<Integer> list = new LinkedList<>();
        list.addFirst(value % 10);
        value /= 10;
        if (value > 0) list.addFirst(value % 10);        
        return list;
    }

    int nextPosition(int position, int score, int size) {        
        position += 1 + score;
        while (position >= size) position -= size;
        return position;
    }

    List<Integer> startRecipes() {
        var recipes = new ArrayList<Integer>();
        recipes.add(3);
        recipes.add(7);
        return recipes;
    }
    
    @Test
    public void unitTest() throws IOException {        
        assertEquals(0, nextPosition(0, 3, 4));
        assertEquals(1, nextPosition(1, 7, 4));
        var recipes = startRecipes();
        makeRecipes(recipes, 3, 7);
        assertEquals(4, recipes.size());

        assertEquals(5, totalRecipeWhenPatternAppear("01245"));
        assertEquals(9, totalRecipeWhenPatternAppear("51589"));
        assertEquals(18, totalRecipeWhenPatternAppear("92510"));
        assertEquals(2018, totalRecipeWhenPatternAppear("59414"));
    }
}
