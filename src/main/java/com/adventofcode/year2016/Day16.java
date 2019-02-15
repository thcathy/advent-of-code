package com.adventofcode.year2016;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class Day16 {
    Logger log = LoggerFactory.getLogger(Day16.class);

    public static void main(String... args) throws Exception {
        Day16 solution = new Day16();
        solution.run();
    }

    void run() throws Exception {
        log.warn("First star - what is the correct checksum? {}", checksumToFill("01111010110010011", 272));
        log.warn("Second star - what is the correct checksum? {}", checksumToFill("01111010110010011", 35651584));
    }

    String checksumToFill(String input, int lengthToFill) {
        while (input.length() < lengthToFill)
            input = construct(input);
        return checksum(input.substring(0, lengthToFill));
    }

    String construct(String input) {
        var copy = StringUtils.reverse(input)
                .replaceAll("1"," ")
                .replaceAll("0", "1")
                .replaceAll(" ", "0");
        return input + '0' + copy;
    }

    String checksum(String input) {
        var stringBuilder = new StringBuilder();
        for (int i=0; i < input.length(); i+=2) {
           if (input.charAt(i) == input.charAt(i+1))
               stringBuilder.append('1');
           else
               stringBuilder.append('0');
        }
        return (stringBuilder.length() % 2 == 1) ? stringBuilder.toString() : checksum(stringBuilder.toString());
    }

    @Test
    public void test_checksumToFill() {
        assertEquals("01100", checksumToFill("10000", 20));
    }

    @Test
    public void test_checksum() {
        assertEquals("100", checksum("110010110100"));
    }

    @Test
    public void test_construct() {
        assertEquals("100", construct("1"));
        assertEquals("001", construct("0"));
        assertEquals("11111000000", construct("11111"));
        assertEquals("1111000010100101011110000", construct("111100001010"));
    }

}