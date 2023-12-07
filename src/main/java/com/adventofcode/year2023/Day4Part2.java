package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Day4Part2 {
    Logger log = LoggerFactory.getLogger(Day4Part2.class);
    final static String inputFile = "2023/day4.txt";

    public static void main(String... args) throws IOException {
        Day4Part2 solution = new Day4Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalScratchcards(lines);
        log.warn("how many total scratchcards do you end up with? {}", result);
    }

    int totalScratchcards(List<String> inputs) {
        var cards = inputs.stream().map(this::parseCard).toList();
        var cardCount = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            cardCount[i] += 1;
            var matches = matches(cards.get(i));
            for (int j = 1; j <= matches; j++) {
                cardCount[i + j] += cardCount[i];
            }
        }
        return Arrays.stream(cardCount).sum();
    }

    long matches(Card card) {
        return card.owningNumbers.stream()
                .filter(card.winningNumbers::contains)
                .count();
    }

    Card parseCard(String input) {
        var parts = input.split("\\|");
        var winningNumbers = Arrays.stream(parts[0].split(": ")[1].split(" "))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        var owningNumbers = Arrays.stream(parts[1].split(" "))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .toList();
        return new Card(winningNumbers, owningNumbers);
    }

    record Card(Set<Integer> winningNumbers, List<Integer> owningNumbers) {}

    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day4_test.txt"), Charsets.UTF_8);
        assertEquals(30, totalScratchcards(lines));
    }
}
