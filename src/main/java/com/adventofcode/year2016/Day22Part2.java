package com.adventofcode.year2016;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day22Part2 {
    Logger log = LoggerFactory.getLogger(Day22Part2.class);
    String inputFile = "2016/day22_1.txt";

    public static void main(String... args) throws Exception {
        Day22Part2 solution = new Day22Part2();
        solution.run();
    }

    void run() throws Exception {
        secondStar();
    }

    void secondStar() throws IOException {
        var inputs = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);

        var nodes = inputs.subList(2, inputs.size()).stream()
                .map(this::parseNode)
                .collect(Collectors.toMap(Node::key, Function.identity()));
        var result = stepToGetData(nodes, 34, 0);

        log.warn("Second star - What is the fewest number of steps required to move your goal data to node-x0-y0? {}", result);
    }

    int stepToGetData(Map<String, Node> nodes, int goalDataPositionX, int goalDataPositionY) {
        int step = 0;
        List<State> states = List.of(new State(goalDataPositionX, goalDataPositionY, nodes));
        while (!states.stream().anyMatch(State::isEnd)) {
            step++;
            log.debug("Start step: {} ({})", step, states.size());
            states = states.stream().flatMap(State::allNextStates).collect(Collectors.toList());
        }
        return step;
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
        return pair.getRight() != null
                    && pair.getLeft().used > 0
                    && isAdjacent(pair.getLeft(), pair.getRight())
                    && pair.getLeft().used <= pair.getRight().available;
    }

    boolean isAdjacent(Node n1, Node n2) {
        return Math.abs(n1.x - n2.x) + Math.abs(n1.y - n2.y) == 1;
    }

    Stream<State> allMovement(State state, Node from) {
        if (from == null) return Stream.empty();

        var possibleMove = possibleMovements(state, from);
        if (possibleMove.size() < 1) return Stream.empty();
        var movements = possibleMove.stream()
                            .filter(this::isViableParis)
                            .collect(Collectors.toList());
        if (movements.size() > 0) {
            return movements.stream().map(p -> state.move(p.getLeft(), p.getRight()));
        } else {
            return possibleMovements(state, from).stream().flatMap(p -> allMovement(state, p.getRight()));
        }
    }

    private List<ImmutablePair<Node, Node>> possibleMovements(State state, Node from) {
        if (from.x == state.goalDataPositionX && from.y == state.goalDataPositionY) {
            return List.of(
                    new ImmutablePair<>(from,  state.nodes.get(from.leftKey())),
                    new ImmutablePair<>(from,  state.nodes.get(from.downKey())));
        } else {
            return List.of(
                    new ImmutablePair<>(from,  state.nodes.get(from.leftKey())),
                    new ImmutablePair<>(from,  state.nodes.get(from.rightKey())),
                    new ImmutablePair<>(from,  state.nodes.get(from.upKey())),
                    new ImmutablePair<>(from,  state.nodes.get(from.downKey())));
        }
    }

    class State {
        final int goalDataPositionX, goalDataPositionY;
        final Map<String, Node> nodes;

        State(int goalDataPositionX, int goalDataPositionY, Map<String, Node> nodes) {
            this.goalDataPositionX = goalDataPositionX;
            this.goalDataPositionY = goalDataPositionY;
            this.nodes = nodes;
        }

        Stream<Pair<Node, Node>> allNodesPair() {
            return nodes.values().stream().flatMap(n1 -> nodes.values().stream().map(n2 -> new ImmutablePair<>(n1, n2)));
        }

        Stream<State> allNextStates() {
            return allMovement(this, nodes.get(goalDataPositionX + "," + goalDataPositionY));
        }

        State move(Node from, Node to) {
            var newNodes = new HashMap<>(nodes);
            newNodes.remove(from.key());
            newNodes.remove(to.key());
            var newSourceNode = new Node(from.x, from.y, from.size, 0, from.size);
            var newDestinationNode = new Node(to.x, to.y, to.size, to.used + from.used, to.available - from.used);
            newNodes.put(newSourceNode.key(), newSourceNode);
            newNodes.put(newDestinationNode.key(), newDestinationNode);

            if (from.x == goalDataPositionX && from.y == goalDataPositionY) {
                return new State(to.x, to.y, newNodes);
            } else {
                return new State(goalDataPositionX, goalDataPositionY, newNodes);
            }
        }

        boolean isEnd() { return goalDataPositionX == 0 && goalDataPositionY == 0; }
    }

    class Node {
        final int x;
        final int y;
        final int size;
        final int used;
        final int available;

        public Node(int x, int y, int size, int used, int available) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.used = used;
            this.available = available;
        }

        String key() {
            return x + "," + y;
        }
        String leftKey() {
            return (x - 1) + "," + y;
        }
        String rightKey() { return (x+1) + "," + y; }
        String upKey() { return x + "," + (y-1); }
        String downKey() { return x + "," + (y+1); }
    }

    @Test
    public void test_stepToGetData() {
        List<String> input = List.of("/dev/grid/node-x0-y0   10T    8T     2T   80%",
                                     "/dev/grid/node-x0-y1   11T    6T     5T   54%",
                                     "/dev/grid/node-x0-y2   32T   28T     4T   87%",
                                     "/dev/grid/node-x1-y0    9T    7T     2T   77%",
                                     "/dev/grid/node-x1-y1    8T    0T     8T    0%",
                                     "/dev/grid/node-x1-y2   11T    7T     4T   63%",
                                     "/dev/grid/node-x2-y0   10T    6T     4T   60%",
                                     "/dev/grid/node-x2-y1    9T    8T     1T   88%",
                                     "/dev/grid/node-x2-y2    9T    6T     3T   66%");
        var nodes = input.stream()
                        .map(this::parseNode)
                        .collect(Collectors.toMap(Node::key, Function.identity()));
        var result = stepToGetData(nodes, 2, 0);
        assertEquals(7, result);
    }

    @Test
    public void test_allNextState() {
        List<String> input = List.of("/dev/grid/node-x0-y0   10T    8T     2T   80%",
                "/dev/grid/node-x0-y1   11T    6T     5T   54%",
                "/dev/grid/node-x0-y2   32T   28T     4T   87%",
                "/dev/grid/node-x1-y0    9T    7T     2T   77%",
                "/dev/grid/node-x1-y1    8T    0T     8T    0%",
                "/dev/grid/node-x1-y2   11T    7T     4T   63%",
                "/dev/grid/node-x2-y0   10T    6T     4T   60%",
                "/dev/grid/node-x2-y1    9T    8T     1T   88%",
                "/dev/grid/node-x2-y2    9T    6T     3T   66%");
        var nodes = input.stream()
                .map(this::parseNode)
                .collect(Collectors.toMap(Node::key, Function.identity()));
        var state = new State(2, 0, nodes);
        assertEquals(2, state.allNextStates().count());
    }
}