package com.adventofcode.year2023;

import static com.adventofcode.year2023.Day16Part2.Direction.Down;
import static com.adventofcode.year2023.Day16Part2.Direction.Left;
import static com.adventofcode.year2023.Day16Part2.Direction.Right;
import static com.adventofcode.year2023.Day16Part2.Direction.Up;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day16Part2 {
    Logger log = LoggerFactory.getLogger(Day16Part2.class);
    final static String inputFile = "2023/day16.txt";

    public static void main(String... args) throws IOException {
        Day16Part2 solution = new Day16Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = new Day16Part2().parseInput(lines);
        var result = maxEnergized(puzzle);
        log.warn("How many tiles are energized in that configuration? {}", result);
    }

    int maxEnergized(Contraption contraption) {
        // max when beam from top
        var maxValue = IntStream.range(0, contraption.width).parallel()
            .mapToObj(x -> new Beam(new Position(x, 0), Down))
            .mapToInt(b -> totalEnergized(contraption, b))
            .max().getAsInt();
        // max when beam from bottom
        maxValue = Math.max(
            maxValue, 
            IntStream.range(0, contraption.width).parallel()
                .mapToObj(x -> new Beam(new Position(x, contraption.height - 1), Up))
                .mapToInt(b -> totalEnergized(contraption, b))
                .max().getAsInt());
        // max when beam from left
        maxValue = Math.max(
            maxValue, 
            IntStream.range(0, contraption.height).parallel()
                .mapToObj(y -> new Beam(new Position(0, y), Right))
                .mapToInt(b -> totalEnergized(contraption, b))
                .max().getAsInt());
        // max when beam from right
        maxValue = Math.max(
            maxValue, 
            IntStream.range(0, contraption.height).parallel()
                .mapToObj(y -> new Beam(new Position(contraption.width - 1, y), Left))
                .mapToInt(b -> totalEnergized(contraption, b))
                .max().getAsInt());

        return maxValue;
    }

    int totalEnergized(Contraption contraption, Beam beamStarted) {        
        var energized = new HashSet<Position>();
        List<Beam> beams = new ArrayList<>();
        beams.add(beamStarted);
        var previousBeam = new HashSet<Beam>();

        while (!beams.isEmpty()) {
            beams.forEach(b -> energized.add(b.position));                        
            previousBeam.addAll(beams);
            
            beams = beams.stream()
                                .flatMap(b -> move(b, contraption))
                                .filter(b -> !previousBeam.contains(b))
                                .filter(b -> !contraption.isOutside(b.position)).toList();
        }

        return energized.size();
    }

    Stream<Beam> move(Beam beam, Contraption contraption) {
        return switch (contraption.map.get(beam.position)) {
            case '\\', '/' -> moveFromMirror(beam, contraption.map.get(beam.position));
            case '|' -> moveFromSplitterVertical(beam);
            case '-' -> moveFromSplitterHorizontal(beam);
            default -> moveFromEmptySpace(beam);
        };        
    }

    Stream<Beam> moveFromSplitterHorizontal(Beam beam) {
        if (beam.direction == Left || beam.direction == Right) {
            return Stream.of(
                new Beam(beam.position.move(beam.direction), beam.direction)
            ); 
        }
        return Stream.of(
            new Beam(beam.position.move(Left), Left),
            new Beam(beam.position.move(Right), Right)
        );
    }

    Stream<Beam> moveFromSplitterVertical(Beam beam) {
        if (beam.direction == Up || beam.direction == Down) {
            return Stream.of(
                new Beam(beam.position.move(beam.direction), beam.direction)
            ); 
        }
        return Stream.of(            
            new Beam(beam.position.move(Up), Up),
            new Beam(beam.position.move(Down), Down)
        );
    }

    Stream<Beam> moveFromMirror(Beam beam, char mirror) {
        Direction newDirection;
        if (mirror == '\\')
            newDirection = switch (beam.direction) {
                case Up -> Left;
                case Down -> Right;
                case Left -> Up;
                case Right -> Down;
            };
        else {
            newDirection = switch (beam.direction) {
                case Up -> Right;
                case Down -> Left;
                case Left -> Down;
                case Right -> Up;
            };
        }
        return Stream.of(
            new Beam(beam.position.move(newDirection), newDirection)
        );
    }

    Stream<Beam> moveFromEmptySpace(Beam beam) {
        return Stream.of(
            new Beam(beam.position.move(beam.direction), beam.direction)
        );
    }

    //region Data Objects

    record Contraption(Map<Position, Character> map, int width, int height) {
        boolean isOutside(Position position) {
            return position.x < 0 || position.y < 0 || position.x >= width || position.y >= height;
        }
    }

    record Beam(Position position, Direction direction) {}

    record Position(int x, int y) {
        Position move(Direction direction) {
            return switch (direction) {
                case Up -> new Position(x, y-1);
                case Down -> new Position(x, y+1);
                case Left -> new Position(x-1, y);
                case Right -> new Position(x+1, y);
            };
        }
    }

    enum Direction { Up, Down, Left, Right }
    
    //endregion

    //region Input Parsing

    Contraption parseInput(List<String> inputs) {
        var height = inputs.size();
        var width = inputs.get(0).length();
        var map = new HashMap<Position, Character>();
        for (int y = 0; y < height; y++) {
            var line = inputs.get(y);
            for (int x = 0; x < width; x++) {                
                map.put(new Position(x, y), line.charAt(x));
            }
        }
        return new Contraption(map, width, height);
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day16_test.txt"), Charsets.UTF_8);
        var puzzle = new Day16Part2().parseInput(lines);
        var result = maxEnergized(puzzle);
        Assert.assertEquals(51, result);
    }
}
