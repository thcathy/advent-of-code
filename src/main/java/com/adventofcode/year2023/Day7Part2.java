package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class Day7Part2 {
    Logger log = LoggerFactory.getLogger(Day7Part2.class);
    final static String inputFile = "2023/day7.txt";

    public static void main(String... args) throws IOException {
        Day7Part2 solution = new Day7Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var handAndBidList = parseInput(lines);
        var result = totalWinnings(handAndBidList);
        log.warn("What do you get if you multiply these numbers together? {}", result);
    }

    List<HandAndBid> parseInput(List<String> inputs) {
        var immutableList = inputs.stream().map(this::parseHandAndBid).toList();
        return new ArrayList<>(immutableList);
    }

    long totalWinnings(List<HandAndBid> handAndBidList) {
        Collections.sort(handAndBidList);
        long total = 0;
        for (int i = 0; i < handAndBidList.size(); i++) {
            total += (long) (i + 1) * handAndBidList.get(i).bid;
        }
        return total;
    }

    HandAndBid parseHandAndBid(String input) {
        var array = input.split(" ");
        var hand = array[0].toCharArray();
        return new HandAndBid(hand, Integer.parseInt(array[1]), parseType(hand));
    }

    HandType parseType(char[] hand) {
        Map<Character, Integer> handMap = new HashMap<>();
        for (char c : hand) {
            handMap.merge(c, 1, Integer::sum);
        }

        if (isFiveOfAKind(handMap)) return HandType.FiveOfAKind;
        else if (isFourOfAKind(handMap)) return HandType.FourOfAKind;
        else if (isFullHouse(handMap)) return HandType.FullHouse;
        else if (isThreeOfAKind(handMap)) return HandType.ThreeOfAKind;
        else if (isTwoPair(handMap)) return HandType.TwoPair;
        else if (isOnePair(handMap)) return HandType.OnePair;
        return HandType.HighCard;
    }

    boolean isOnePair(Map<Character, Integer> handMap) {
        var totalJokers = handMap.getOrDefault('J', 0);
        if (totalJokers == 0 && handMap.size() == 4 && handMap.values().stream().filter(v -> v == 2).count() == 1)
            return true;
        else return totalJokers == 1;
    }

    boolean isTwoPair(Map<Character, Integer> handMap) {
        return handMap.size() == 3 && handMap.values().stream().filter(v -> v == 2).count() == 2;
    }

    boolean isThreeOfAKind(Map<Character, Integer> handMap) {
        var totalJokers = handMap.getOrDefault('J', 0);
        if (totalJokers == 0 && handMap.size() == 3 && handMap.values().stream().anyMatch(v -> v == 3))
            return true;
        if (totalJokers == 2)
            return true;
        var noneJokerCards = handMap.entrySet().stream().filter(e -> e.getKey() != 'J').toList();
        return totalJokers == 1 && noneJokerCards.stream().anyMatch(e -> e.getValue() == 2);
    }

    boolean isFullHouse(Map<Character, Integer> handMap) {
        var totalJokers = handMap.getOrDefault('J', 0);
        if (totalJokers == 0 && handMap.size() == 2 && handMap.values().stream().anyMatch(v -> v == 3))
            return true;
        var noneJokerCards = handMap.entrySet().stream().filter(e -> e.getKey() != 'J').toList();

        if (totalJokers == 2 && noneJokerCards.size() == 2)
            return true;
        return totalJokers == 1 && noneJokerCards.stream().allMatch(e -> e.getValue() == 2);
    }

    boolean isFourOfAKind(Map<Character, Integer> handMap) {
        var totalJokers = handMap.getOrDefault('J', 0);
        if (totalJokers == 0 && (handMap.size() == 2 && handMap.values().stream().anyMatch(v -> v == 4)))
                return true;
        return handMap.entrySet().stream().filter(e -> e.getKey() != 'J').anyMatch(e -> e.getValue() + totalJokers == 4);
    }

    boolean isFiveOfAKind(Map<Character, Integer> handMap) {
        if (handMap.size() == 1) return true;
        if (handMap.size() > 2) return false;

        var totalJokers = handMap.getOrDefault('J', 0);
        return handMap.entrySet().stream().filter(e -> e.getKey() != 'J').anyMatch(e -> e.getValue() + totalJokers == 5);
    }

    record HandAndBid(char[] hand, int bid, HandType type) implements Comparable<HandAndBid> {
        @Override
        public int compareTo(HandAndBid other) {
            if (type != other.type)
                return Integer.compare(type.rank, other.type.rank);

            for (int i = 0; i < 5; i++) {
                if (hand[i] != other.hand[i])
                    return Integer.compare(cardStrength(hand[i]), cardStrength(other.hand[i]));
            }

            return 0;
        }

        int cardStrength(char card) {
            return switch (card) {
                case 'A' -> 14;
                case 'K' -> 13;
                case 'Q' -> 12;
                case 'T' -> 10;
                case 'J' -> 1;
                default -> Character.getNumericValue(card);
            };
        }
    }

    enum HandType {
        FiveOfAKind(6), FourOfAKind(5), FullHouse(4), ThreeOfAKind(3), TwoPair(2), OnePair(1), HighCard(0);

        final int rank;

        HandType(int rank) {
            this.rank = rank;
        }
    }
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day7_test.txt"), Charsets.UTF_8);
        var handAndBidList = parseInput(lines);
        assertEquals(5905, totalWinnings(handAndBidList));
    }
}
