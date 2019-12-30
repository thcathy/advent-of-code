package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day8Part2 {
    Logger log = LoggerFactory.getLogger(Day8Part2.class);
    final static String inputFile = "2019/day8_1.txt";
    final static int PROGRAM_OUTPUT = -1;

    public static void main(String... args) throws IOException {
        Day8Part2 solution = new Day8Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var image = decodeImage(convertToLayer(lines.get(0), 25, 6));
        log.warn("What message is produced after decoding your image?");
        printImage(image, 25);
    }

    private void printImage(List<String> image, int width) {
        for (int i = 0; i < image.size(); i++) {
            if (i%width == 0) System.out.println();
            System.out.print(image.get(i).equals("1") ? "#" : " ");
        }
    }

    List<Layer> convertToLayer(String input, int wide, int tall) {
        var layers = new ArrayList<Layer>();
        int size = wide * tall;
        int pointer = size;
        while (pointer <= input.length()) {
            layers.add(new Layer(input.substring(pointer-size, pointer)));
            pointer += size;
        }
        return layers;
    }

    List<String> decodeImage(List<Layer> layers) {
        var image = new LinkedList<String>();
        int digitLength = layers.get(0).digits.length();
        for (int i = 0; i < digitLength; i++) {
            image.add(decodePixel(getDigitsFromLayers(layers, i)));
        }
        return image;
    }

    private List<String> getDigitsFromLayers(List<Layer> layers, int i) {
        return layers.stream()
                .map(l -> l.digits.substring(i, i+1))
                .collect(Collectors.toList());
    }

    String decodePixel(List<String> digits) {
        for (String digit : digits) {
            if ("1".equals(digit) || "0".equals(digit))
                return digit;
        }
        return "2";
    }

    class Layer {
        String digits;

        public Layer(String digits) {
            this.digits = digits;
        }

        public long totalDigits(char s) {
            return digits.chars().filter(c -> c == s).count();
        }
    }

    @Test
    public void decodeImage_testcases() {
        var image = decodeImage(convertToLayer("0222112222120000", 2, 2));
        assertEquals("0", image.get(0));
        assertEquals("1", image.get(1));
        assertEquals("1", image.get(2));
        assertEquals("0", image.get(3));
    }
}
