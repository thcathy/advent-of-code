package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class Day20Part1 {
    final static String inputFile = "2022/day20.txt";

    public static void main(String... args) throws IOException {
        Day20Part1 solution = new Day20Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = findGroveCoordinates(lines);
        System.out.println("What is the sum of the three numbers that form the grove coordinates? " + result);
    }

    int findGroveCoordinates(List<String> input) {
        var nodeZero = mixing(input);
        return valueAfter(nodeZero, 1000, input.size())
                + valueAfter(nodeZero, 2000, input.size())
                + valueAfter(nodeZero, 3000, input.size());
    }

    Node mixing(List<String> input) {
        var nodeZero = parseInput(input);
        for (int i = 0; i < input.size(); i++) {
            move(Integer.parseInt(input.get(i)), i, nodeZero, input.size());
        }
        return nodeZero;
    }

    Node parseInput(List<String> input) {
        var node = new Node(Integer.parseInt(input.get(0)), 0);
        var first = node;
        Node nodeZero = null;
        Node next = null;
        for (int i = 1; i < input.size(); i++) {
            next = new Node(Integer.parseInt(input.get(i)), i);
            node.next = next;
            next.previous = node;
            node = next;

            if (next.value == 0) nodeZero = next;
        }
        next.next = first;
        first.previous = next;
        return nodeZero;
    }

    Node find(int value, int originalPosition, Node node) {
        while (true) {
            if (node.value == value && node.originalPosition == originalPosition)
                return node;
            node = node.next;
        }
    }

    Node move(int value, int originalPosition, Node node, int size) {
        if (value == 0) return node;

        var valueNode = find(value, originalPosition, node);
        var previous = valueNode.previous;
        var next = valueNode.next;
        previous.next = next;
        next.previous = previous;

        int distance = value % (size - 1);
        if (distance < 0) {
            for (long i = 0; i > distance; i--) {
                next = previous;
                previous = previous.previous;
            }
        } else {
            for (long i = 0; i < distance; i++) {
                previous = next;
                next = next.next;
            }
        }

        previous.next = valueNode;
        next.previous = valueNode;
        valueNode.previous = previous;
        valueNode.next = next;
        return valueNode;
    }

    class Node {
        int value; int originalPosition;
        Node previous, next;

        public Node(int value, int originalPosition) {
            this.value = value;
            this.originalPosition = originalPosition;
        }
    }

    int valueAfter(Node node, int position, int size) {
        position = position % size;
        while (position > 0) {
            node = node.next;
            position--;
        }
        return node.value;
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day20_test.txt").toURI()));
        var node = parseInput(lines);
        assertEquals(0, node.value);
        assertEquals(4, node.next.value);

        node = mixing(lines);
        assertEquals(4, valueAfter(node, 1000, lines.size()));
        assertEquals(-3, valueAfter(node, 2000, lines.size()));
        assertEquals(2, valueAfter(node, 3000, lines.size()));

        assertEquals(3, findGroveCoordinates(lines));
    }

}
