package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day8Part2 {
    final static String inputFile = "2018/day8.txt";
        
    public static void main(String... args) throws IOException {
        Day8Part2 solution = new Day8Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = nodeValue(parseNode(intArray(lines.get(0)), 0).node);
        System.out.println("What is the value of the root node? " + result);
    }

    int nodeValue(Node node) {
        if (node.children().size() == 0) {
            return node.metadata.stream().mapToInt(v -> v).sum();
        }
        int value = 0;
        for (int metadata : node.metadata) {
            if (metadata > node.children.size()) continue;

            value += nodeValue(node.children().get(metadata-1));
        }
        return value;
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
        assertEquals(66, nodeValue(result.node));
    }
}
