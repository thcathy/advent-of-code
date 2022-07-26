package com.adventofcode.year2017;


import java.math.BigInteger;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day23Part2 {
    final static String inputFile = "2017/day23_1.txt";
    
    public static void main(String... args) throws Exception {
        Day23Part2 solution = new Day23Part2();
        solution.run();
    }

    void run() throws Exception {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = registerHValue(lines.get(0));
        System.out.println("what value would be left in register h? " + result);
    }

    int registerHValue(String input) {
        int registerBValue = Integer.valueOf(input.split(" ")[2]);
        int counter = 0;
        final int original = registerBValue * 100 + 100000;

        for (int n = 0; n <= 1000; ++n) {
            int number = original + 17 * n;
            if (!BigInteger.valueOf(number).isProbablePrime(100000))
                counter++;

        }
        return counter;
    }
}
