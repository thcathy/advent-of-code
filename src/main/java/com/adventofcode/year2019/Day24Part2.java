package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day24Part2 {
    Logger log = LoggerFactory.getLogger(Day24Part2.class);
    static final String inputFile = "2019/day24_a.txt";
    static final char SPACE = '.';
    static final char BUG = '#';
    static final int MAP_SIZE = 5;

    public static void main(String... args) throws IOException {
        Day24Part2 solution = new Day24Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var bugs = parseMap(lines);
        var iteration = 0;

        while (iteration < 10) {
            var nextBugs = new HashSet<Position>();
            for (Position bug : bugs) {
                for (Position tile : adjacentTiles(bug)) {
                    if (!bugs.contains(tile)) {
                        long adjacentBugs = adjacentTiles(tile).stream().filter(bugs::contains).count();
                        if (adjacentBugs == 1 || adjacentBugs == 2) {
                            nextBugs.add(tile);
                        }
                    }
                }
                long adjacentBugs = adjacentTiles(bug).stream().filter(bugs::contains).count();
                if (adjacentBugs == 1) {
                    nextBugs.add(bug);
                }
            }
            bugs = nextBugs;
            iteration++;
        }
        log.warn("What is the biodiversity rating for the first layout that appears twice? {}", bugs.size());
    }

    List<Position> adjacentTiles(Position position) {
        List<Position> tiles = new ArrayList<>();
        tiles.addAll(tiles(position.level, position.y+1, position.x, position));
        tiles.addAll(tiles(position.level, position.y-1, position.x, position));
        tiles.addAll(tiles(position.level, position.y, position.x+1, position));
        tiles.addAll(tiles(position.level, position.y, position.x-1, position));
        return tiles;
    }

    List<Position> tiles(int level, int y, int x, Position fromPosition) {
        if (x < 0) {
            return List.of(new Position(level+1, 2, 1));
        } else if (y < 0) {
            return List.of(new Position(level+1, 1, 2));
        } else if (x >= MAP_SIZE) {
            return List.of(new Position(level+1, 2, 3));
        } else if (y >= MAP_SIZE) {
            return List.of(new Position(level+1, 2, 3));
        } else if (x==2 && y==2) {
            final int nextLevel = level - 1;
            if (fromPosition.y==1) {
                return IntStream.range(0, 5).mapToObj(i -> new Position(nextLevel, 0, i)).collect(Collectors.toList());
            } else if (fromPosition.y==3) {
                return IntStream.range(0, 5).mapToObj(i -> new Position(nextLevel, 4, i)).collect(Collectors.toList());
            } else if (fromPosition.x==1) {
                return IntStream.range(0, 5).mapToObj(i -> new Position(nextLevel, i, 0)).collect(Collectors.toList());
            } else if (fromPosition.x==3) {
                return IntStream.range(0, 5).mapToObj(i -> new Position(nextLevel, i, 4)).collect(Collectors.toList());
            }
            throw new RuntimeException("unexpected input");
        } else
            return List.of(new Position(level, y, x));
    }

    Set<Position> parseMap(List<String> lines) {
        var bugs = new HashSet<Position>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == BUG)
                    bugs.add(new Position(0, y, x));
            }
        }
        return bugs;
    }

    class Position {
        int level; int x; int y;

        public Position(int level, int x, int y) {
            this.level = level;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() { return "[" + level + "," + y + "," + x + "]"; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Position position = (Position) o;

            if (level != position.level) return false;
            if (x != position.x) return false;
            return y == position.y;
        }

        @Override
        public int hashCode() {
            int result = level;
            result = 31 * result + x;
            result = 31 * result + y;
            return result;
        }
    }

}
