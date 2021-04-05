package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class Day22Part2 {
    Logger log = LoggerFactory.getLogger(Day22Part2.class);
    final static String inputFile = "2019/day22_1.txt";
    final static BigInteger DECK_SIZE = BigInteger.valueOf(119315717514047L);
    final static BigInteger SHUFFLE_TIMES = BigInteger.valueOf(101741582076661L);

    public static void main(String... args) throws IOException {
        Day22Part2 solution = new Day22Part2();
        solution.run();
    }

    void run() throws IOException {
        var fileInput = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = cardAtPosition(fileInput, DECK_SIZE, SHUFFLE_TIMES, 2020);
        log.warn("what number is on the card that ends up in position 2020? {}", result);
    }

    BigInteger value(long v) { return BigInteger.valueOf(v); }

    BigInteger cardAtPosition(List<String> inputs, BigInteger deckSize, BigInteger shuffledTimes, int position) {
        var calc = new BigInteger[] {value(1), value(0)};
        Collections.reverse(inputs);
        for (String input : inputs) {
            calculateFormula(input, calc, deckSize);
        }
        var pow = calc[0].modPow(shuffledTimes, deckSize);
        return pow.multiply(value(position))
                .add(calc[1].multiply(pow.add(deckSize).subtract(value(1)))
                        .multiply(calc[0].subtract(value(1)).modPow(deckSize.subtract(value(2)), deckSize))
                )
                .mod(deckSize);
    }

    void calculateFormula(String input, BigInteger[] calc, BigInteger deckSize) {
        if (input.startsWith("deal into new stack")) {
            calc[0] = calc[0].multiply(value(-1));
            calc[1] = calc[1].add(value(1)).multiply(value(-1));
        } else if (input.startsWith("deal with increment")) {
            var multiplier = inputParam(input).modPow(deckSize.subtract(value(2)), deckSize);
            calc[0] = calc[0].multiply(multiplier);
            calc[1] = calc[1].multiply(multiplier);
        } else if (input.startsWith("cut")) {
            calc[1] = calc[1].add(inputParam(input));
        }
        calc[0] = calc[0].mod(deckSize);
        calc[1] = calc[1].mod(deckSize);
    }

    BigInteger inputParam(String input) {
        var params = input.split(" ");
        return BigInteger.valueOf(Long.valueOf(params[params.length-1]));
    }
}
