package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day22Part2 {
    final static String inputFile = "2017/day22_1.txt";
    final static int ITERATION = 10000000;
        
    public static void main(String... args) throws IOException {
        Day22Part2 solution = new Day22Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = infectedCount(parseInfectedNodes(lines));
        System.out.println("how many bursts cause a node to become infected? " + result);
    }

    int infectedCount(Map<Position, NodeState> nodes) {
        int infectedCount = 0;
        Virus virus = new Virus();
        virus.direction = Direction.Up;
        virus.position = new Position(0, 0);
        for (int i = 0; i < ITERATION; i++) {
            infectedCount = virusBurst(nodes, virus, infectedCount);
        }
        return infectedCount;
    }

    enum NodeState { Clean, Weakened, Infected, Flagged }
    
    record Position(int x, int y) {}

    enum Direction {
        Up, Down, Left, Right;

        Direction turnClockwise() {
            return switch (this) {
                case Down -> Left;
                case Left -> Up;
                case Right -> Down;
                case Up -> Right;                
            };
        }
    
        Direction turnAntiClockwise() {
            return switch (this) {
                case Down -> Right;
                case Left -> Down;
                case Right -> Up;
                case Up -> Left;
            };
        }
    }
    
    class Virus {
        Position position;
        Direction direction;

        public void move() {
            position = switch (direction) {
                case Down -> new Position(position.x, position.y+1);
                case Left -> new Position(position.x-1, position.y);           
                case Right -> new Position(position.x+1, position.y);
                case Up -> new Position(position.x, position.y-1);
            };
        }
    }

    int virusBurst(Map<Position, NodeState> nodes, Virus virus, int infectedCount) {
        NodeState currentState = nodes.getOrDefault(virus.position, NodeState.Clean);
        if (NodeState.Clean == currentState) {
            virus.direction = virus.direction.turnAntiClockwise();
            nodes.put(virus.position, NodeState.Weakened);
        } else if (NodeState.Weakened == currentState) {
            nodes.put(virus.position, NodeState.Infected);
            infectedCount++;
        } else if (NodeState.Infected == currentState) {
            virus.direction = virus.direction.turnClockwise();
            nodes.put(virus.position, NodeState.Flagged);
        } else if (NodeState.Flagged == currentState) {
            virus.direction = virus.direction.turnClockwise().turnClockwise();
            nodes.remove(virus.position);
        }

        virus.move();
        return infectedCount;
    }

    Map<Position, NodeState> parseInfectedNodes(List<String> inputs) {
        Map<Position, NodeState> infectedNodes = new HashMap<>();
        int center = inputs.size() / 2;
        for (int y = 0; y < inputs.size(); y++) {
            char[] line = inputs.get(y).toCharArray();
            for (int x = 0; x < line.length; x++) {
                if (line[x] == '#') {
                    infectedNodes.put(new Position(x - center, y - center), NodeState.Infected);
                }
            }
        }
        return infectedNodes; 
    }
      
    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2017/day22_test.txt"), Charsets.UTF_8);
        var infectedNodes = parseInfectedNodes(lines);
        assertEquals(2, infectedNodes.size());
        assertTrue(infectedNodes.containsKey(new Position(1, -1)));
        assertTrue(infectedNodes.containsKey(new Position(-1, 0)));
        
        assertEquals(2511944, infectedCount(infectedNodes));
    }
}
