package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day8Part1 {
    Logger log = LoggerFactory.getLogger(Day8Part1.class);
    final static String inputFile = "2019/day8_1.txt";
    final static int PROGRAM_OUTPUT = -1;

    public static void main(String... args) throws IOException {
        Day8Part1 solution = new Day8Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var layer = fewest0Layer(convertToLayer(lines.get(0), 25, 6));
        var result = layer.totalDigits('1') * layer.totalDigits('2');
        log.warn("what is the number of 1 digits multiplied by the number of 2 digits = {}", result);
    }

    Layer fewest0Layer(List<Layer> layers) {
        long fewestZero = Integer.MAX_VALUE;
        Layer fewestZeroLayer = null;
        for (Layer l : layers) {
            long totalZero = l.totalDigits('0');
            if (totalZero < fewestZero) {
                fewestZero = totalZero;
                fewestZeroLayer = l;
            }
        }
        return fewestZeroLayer;
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

    class Layer {
        String digits;

        public Layer(String digits) {
            this.digits = digits;
        }

        public long totalDigits(char s) {
            return digits.chars().filter(c -> c == s).count();
        }
    }
}
