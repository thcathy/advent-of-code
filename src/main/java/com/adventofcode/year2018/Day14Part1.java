package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class Day14Part1 {
    final static int puzzleInput = 580741;
    
    public static void main(String... args) throws IOException {
        Day14Part1 solution = new Day14Part1();
        solution.run();
    }

    void run() throws IOException {        
        var result = lastTenScoreAfterRound(puzzleInput);
        System.out.println("What are the scores of the ten recipes immediately after the number of recipes in your puzzle input? " + result);
    }

    String lastTenScoreAfterRound(int afterRecipes) {
        var state = new State();
        while (state.recipes.size() < afterRecipes + 10) {
            nextRound(state);            
        }

        return getTenScore(state.recipes, afterRecipes);
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

    String getTenScore(List<Integer> recipes, int from) {
        var sb = new StringBuilder();
        for (int i = from; i < from + 10; i++) sb.append(recipes.get(i));
        return sb.toString();
    }
        
    @Test
    public void unitTest() throws IOException {        
        assertEquals(0, nextPosition(0, 3, 4));
        assertEquals(1, nextPosition(1, 7, 4));
        var recipes = startRecipes();
        makeRecipes(recipes, 3, 7);
        assertEquals(4, recipes.size());

        assertEquals("0124515891", lastTenScoreAfterRound(5));
        assertEquals("5158916779", lastTenScoreAfterRound(9));
        assertEquals("9251071085", lastTenScoreAfterRound(18));
        assertEquals("5941429882", lastTenScoreAfterRound(2018));
    }
}
