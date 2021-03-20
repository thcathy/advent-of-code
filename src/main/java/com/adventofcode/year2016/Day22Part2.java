package com.adventofcode.year2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day22Part2 {
    static Logger log = LoggerFactory.getLogger(Day22Part1.class);
    static String inputFile = "2016/day22_1.txt";

    public static void main(String[] args) throws IOException {
        List<String> s = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        log.warn("Second star - What is the fewest number of steps required to move your goal data to node-x0-y0? {}", partTwo(parse(s)));
    }

    private static class Node extends Point {
        final int size;
        final int used;
        final int avail;

        public Node(int x, int y, int size, int used, int avail) {
            super(x, y);
            this.size = size;
            this.used = used;
            this.avail = avail;
        }
    }

    private static List<Node> parse(List<String> input) {
        return input.stream().map(s -> {
            Matcher m = Pattern.compile("(\\d+)-y(\\d+)\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)").matcher(s);
            if (m.find()) {
                Node node = new Node(Integer.parseInt(m.group(1)),
                        Integer.parseInt(m.group(2)),
                        Integer.parseInt(m.group(3)),
                        Integer.parseInt(m.group(4)),
                        Integer.valueOf(m.group(5)));
                return node;
            } else {
                return null;
            }
        }).
                filter(n -> n != null)
                .collect(Collectors.toList());
    }

    private static long partTwo(List<Node> s) {
        int xSize = s.stream().max(Comparator.comparing(n -> n.x)).get().x;
        int ySize = s.stream().max(Comparator.comparing(n -> n.y)).get().y;
        Node wStart = null, hole = null;
        Node[][] nodes = new Node[xSize + 1][ySize + 1];
        s.forEach(n -> nodes[n.x][n.y] = n);
        for (int x = 0; x < nodes.length; x++) {
            for (int y = 0; y < nodes[x].length; y++) {
                Node n = nodes[x][y];
                if (x == 0 && y == 0)
                    System.out.print("S");
                else if (x == xSize && y == 0)
                    System.out.print("G");
                else if (n.used == 0) {
                    hole = n;
                    System.out.print("_");
                } else if (n.size > 250) {
                    if (wStart == null)
                        wStart = nodes[x - 1][y];
                    System.out.print("#");
                } else
                    System.out.print(".");
            }
            System.out.println();
        }
        int result = Math.abs(hole.x - wStart.x) + Math.abs(hole.y - wStart.y);
        result += Math.abs(wStart.x - xSize) + wStart.y;
        return result + 5 * (xSize - 1);
    }

}
