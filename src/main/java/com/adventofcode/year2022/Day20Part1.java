package com.adventofcode.year2022;

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
//        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
//        var result = totalExposedSides(parseInput(lines));
//        System.out.println("What is the surface area of your scanned lava droplet? " + result);
    }

    Node mix(List<String> input) {
        var node = parseInput(input);
        input.stream().mapToInt(Integer::parseInt).forEach(i -> move(i, node, input.size()));
        return find(0, node);
    }

    Node parseInput(List<String> input) {
        var node = new Node(Integer.parseInt(input.get(0)));
        var first = node;
        Node next = null;
        for (int i = 1; i < input.size(); i++) {
            next = new Node(Integer.parseInt(input.get(i)));
            node.next = next;
            next.previous = node;
            node = next;
        }
        next.next = first;
        first.previous = next;
        return first;
    }

    Node find(int value, Node node) {
        while (true) {
            if (node.value == value)
                return node;
            node = node.next;
        }
    }

    Node move(int value, Node node, int size) {
        if (value == 0) return node;

        var valueNode = find(value, node);
        var previous = valueNode.previous;
        var next = valueNode.next;
        previous.next = next;
        next.previous = previous;

        int count = value;
        while (count < 0) count += size;
        count--;
        previous = valueNode.next;
        while (count > 0) {
            previous = previous.next;
            count--;
        }
        next = previous.next;
        previous.next = valueNode;
        valueNode.previous = previous;
        next.previous = valueNode;
        valueNode.next = next;
        return valueNode;
    }

    class Node {
        int value;
        Node previous, next;

        public Node(int value) {
            this.value = value;
        }
    }

    void print(Node node) {
        int firstValue = node.value;
        System.out.print(node.value + ", ");
        node = node.next;
        while (node.value != firstValue) {
            System.out.print(node.value + ", ");
            node = node.next;
        }
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day20_test.txt").toURI()));
        var node = parseInput(lines);
        assertEquals(1, node.value);
        assertEquals(2, node.next.value);

        node = move(1, node, lines.size());
        assertEquals(-3, node.next.value);
        assertEquals(2, node.previous.value);

        node = move(2, node, lines.size());
        assertEquals(3, node.next.value);
        assertEquals(-3, node.previous.value);

        node = mix(lines);
        print(node);
    }

}
