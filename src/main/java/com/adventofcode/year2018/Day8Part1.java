package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day8Part1 {
    final static String inputFile = "2018/day8.txt";
        
    public static void main(String... args) throws IOException {
        Day8Part1 solution = new Day8Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumMetadata(parseNode(intArray(lines.get(0)), 0).node);
        System.out.println("What is the sum of all metadata entries? " + result);
    }

    int sumMetadata(Node node) {
        return node.children.stream().mapToInt(c -> sumMetadata(c)).sum() + node.metadata.stream().mapToInt(v -> v).sum();
    }

    ParseNodeResult parseNode(int[] inputs, int inputPos) {
        int totalChild = inputs[inputPos];
        int totalMetadata = inputs[inputPos+1];
        inputPos+=2;
        List<Node> children = new ArrayList<>();
        List<Integer> metadata = new ArrayList<>();
        while (children.size() < totalChild) {
            ParseNodeResult result = parseNode(inputs, inputPos);
            children.add(result.node);
            inputPos = result.nextPos;
        }
        while (metadata.size() < totalMetadata) {
            metadata.add(inputs[inputPos]);
            inputPos++;
        }
        return new ParseNodeResult(new Node(metadata, children), inputPos);
    }

    record ParseNodeResult(Node node, int nextPos) {}

    record Node(List<Integer> metadata, List<Node> children) {}

    int[] intArray(String input) {
        return Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
    }
        
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day8_test.txt"), Charsets.UTF_8);
        var result = parseNode(intArray(lines.get(0)), 0);
        assertEquals(result.node.children.size(), 2);
        assertEquals(result.node.metadata.size(), 3);
        assertEquals(result.nextPos, 16);
        assertEquals(138, sumMetadata(result.node));
    }
}
