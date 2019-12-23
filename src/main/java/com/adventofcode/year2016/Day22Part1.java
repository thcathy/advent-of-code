package com.adventofcode.year2016;

import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day22Part1 {
    Logger log = LoggerFactory.getLogger(Day22Part1.class);
    String inputFile = "2016/day22_1.txt";

    public static void main(String... args) throws Exception {
        Day22Part1 solution = new Day22Part1();
        solution.run();
    }

    void run() throws Exception {
        firstStar();
    }

    void firstStar() throws IOException {
        var inputs = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var nodes = inputs.subList(2, inputs.size()).stream().map(this::parseNode).collect(Collectors.toList());

        var count = nodes.stream().flatMap(n1 -> nodes.stream().map(n2 -> new ImmutablePair<>(n1, n2)))
                .filter(this::isViableParis)
                .count();
        log.warn("First star - How many viable pairs of nodes are there? {}", count);
    }

    Node parseNode(String input) {
        var values = input.replaceAll("\\s+", ",").split(",");
        var positions = values[0].split("-");
        var x = Integer.valueOf(positions[1].substring(1));
        var y = Integer.valueOf(positions[2].substring(1));
        return new Node(x, y, parseSize(values[1]), parseSize(values[2]), parseSize(values[3]));
    }

    int parseSize(String input) { return Integer.valueOf(input.replaceAll("T", "")); }

    boolean isViableParis(Pair<Node, Node> pair) {
        return pair.getLeft().used > 0
                    && notSamePosition(pair.getLeft(), pair.getRight())
                    && pair.getLeft().used <= pair.getRight().available;
    }

    boolean notSamePosition(Node n1, Node n2) {
        return n1.x != n2.x || n1.y != n2.y;
    }

    class Node {
        int x;
        int y;
        int size;
        int used;
        int available;

        public Node(int x, int y, int size, int used, int available) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.used = used;
            this.available = available;
        }
    }

    @Test
    public void test_parseNode() {
        var node = parseNode("/dev/grid/node-x0-y0     89T   67T    22T   75%");
        assertEquals(0, node.x);
        assertEquals(0, node.y);
        assertEquals(89, node.size);
        assertEquals(67, node.used);
        assertEquals(22, node.available);
    }
}