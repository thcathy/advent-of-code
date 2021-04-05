package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day22Part1 {
    Logger log = LoggerFactory.getLogger(Day22Part1.class);
    final static String inputFile = "2019/day22_1.txt";
    final static int TOTAL_CARDS = 10007;

    public static void main(String... args) throws IOException {
        Day22Part1 solution = new Day22Part1();
        solution.run();
    }

    void run() throws IOException {
        var fileInput = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        List<Integer> cards = prepareDeck(TOTAL_CARDS);
        for (String input: fileInput) {
            cards = getCommand(input).apply(input, cards);
        }
        var position = cardPosition(cards, 2019);
        log.warn("what is the position of card 2019? {}", position);
    }

    int cardPosition(List<Integer> cards, int findCard) {
        int i=0;
        for (int card : cards) {
            if (findCard == card)
                return i;
            i++;
        }
        throw new RuntimeException("cannot find card: " + findCard);
    }

    BiFunction<String, List<Integer>, List<Integer>> getCommand(String input) {
        if (input.startsWith("deal with increment"))
            return this::dealWithIncrement;
        else if (input.startsWith("deal into new stack"))
            return this::dealIntoNewStack;
        else if (input.startsWith("cut"))
            return this::cut;
        throw new RuntimeException("cannot find command from: " + input);
    }

    List<Integer> prepareDeck(int totalCards) { return IntStream.range(0, totalCards).boxed().collect(Collectors.toList()); }

    List<Integer> dealIntoNewStack(String command, List<Integer> cards) {
        Collections.reverse(cards);
        return cards;
    }

    List<Integer> cut(String command, List<Integer> cards) {
        var commands = command.split(" ");
        var index = Integer.valueOf(commands[commands.length-1]);
        if (index < 0) index = cards.size() + index;
        var newDeck = new LinkedList<>(cards.subList(index, cards.size()));
        newDeck.addAll(cards.subList(0, index));
        return newDeck;
    }

    List<Integer> dealWithIncrement(String command, List<Integer> cards) {
        var commands = command.split(" ");
        var index = Integer.valueOf(commands[commands.length-1]);
        var deckSize = cards.size();
        var nextCardPosition = new HashMap<Integer, Integer>();
        var newDeck = new LinkedList<Integer>();
        int pos = 0;
        for (Integer card : cards) {
            nextCardPosition.put(pos % deckSize, card);
            pos += index;
        }
        for (int i=0; i<deckSize; i++)
            newDeck.add(nextCardPosition.get(i));

        return newDeck;
    }

}
