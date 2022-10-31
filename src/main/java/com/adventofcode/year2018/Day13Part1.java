package com.adventofcode.year2018;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day13Part1 {
    final static String inputFile = "2018/day13.txt";

    public static void main(String... args) throws IOException {
        Day13Part1 solution = new Day13Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = findCrashPosition(parseInput(lines));
        System.out.println("the location of the first crash: " + result);
    }

    String findCrashPosition(State state) {
        while (!state.crashPosition.isPresent()) {
            tick(state);     
        }
        return state.crashPosition.get().x + "," + state.crashPosition.get().y;
    }

    void tick(State state) {
        state.carts.sort(Comparator.comparing((Cart c) -> c.position.y).thenComparing(c -> c.position.x));
        for (Cart cart : state.carts) {
            move(state, cart);
            if (state.crashPosition.isPresent())
                return;
        }
    }

    void move(State state, Cart cart) {
        state.cartPositions.remove(cart.position);
        cart.move();
        if (state.cartPositions.contains(cart.position)) {
            state.crashPosition = Optional.of(cart.position);
            return;
        } else {
            state.cartPositions.add(cart.position);
        }

        cart.turnIfNeeded(state.map.get(cart.position));
    }

    State parseInput(List<String> inputs) {
        var state = new State();
        for (int y = 0; y < inputs.size(); y++) {
            var line = inputs.get(y);
            for (int x = 0; x < line.length(); x++) {
                var character = line.charAt(x);
                if (character == ' ')
                    continue;

                var position = new Position(x, y);
                if (isCart(character))
                    state.addCart(createCart(position, character));

                state.map.put(position, pathOf(character));
            }
        }
        return state;
    }

    private Character pathOf(char character) {
        if (!isCart(character))
            return character;

        if (character == '>' || character == '<')
            return '-';
        else
            return '|';
    }

    private Cart createCart(Position position, char character) {
        var cart = new Cart();
        cart.position = position;
        cart.direction = directionOf(character);
        return cart;
    }

    boolean isCart(char character) {
        return character == '>' || character == '<' || character == 'v' || character == '^';
    }

    Direction directionOf(char character) {
        if (character == '>')
            return Direction.Right;
        else if (character == '<')
            return Direction.Left;
        else if (character == 'v')
            return Direction.Down;
        return Direction.Up;
    }

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

    enum Turn {
        Left, Right, Straight;

        public Turn next() {
            return switch (this) {
                case Left -> Straight;
                case Straight -> Right;
                case Right -> Left;
            };
        }
    }

    record Position(int x, int y) {
    }

    class State {
        Map<Position, Character> map = new HashMap<>();
        Set<Position> cartPositions = new HashSet<>();
        List<Cart> carts = new ArrayList<>();
        Optional<Position> crashPosition = Optional.empty();

        public void addCart(Cart cart) {
            carts.add(cart);
            cartPositions.add(cart.position);
        }
    }

    class Cart {
        Direction direction;
        Position position;
        Turn nextTurn = Turn.Left;

        void move() {
            position = switch (direction) {
                case Down -> new Position(position.x, position.y + 1);
                case Left -> new Position(position.x - 1, position.y);
                case Right -> new Position(position.x + 1, position.y);
                case Up -> new Position(position.x, position.y - 1);
            };
        }

        void turnIfNeeded(char path) {
            if (path == '/') {
                direction = switch (direction) {
                    case Up -> Direction.Right;
                    case Down -> Direction.Left;
                    case Left -> Direction.Down;
                    case Right -> Direction.Up;                    
                };
            } else if (path == '\\') {
                direction = switch (direction) {
                    case Up -> Direction.Left;
                    case Down -> Direction.Right;
                    case Left -> Direction.Up;
                    case Right -> Direction.Down; 
                };
            } else if (path == '+') {
                direction = switch (nextTurn) {
                    case Left -> direction.turnAntiClockwise();
                    case Right -> direction.turnClockwise();
                    default -> direction;
                };
                nextTurn = nextTurn.next();
            }
        }
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day13_test.txt"), Charsets.UTF_8);
        var state = parseInput(lines);

        assertEquals(2, state.carts.size());
        assertTrue(state.cartPositions.contains(new Position(2, 0)));
        assertEquals('/', state.map.get(new Position(9, 5)).charValue());
        assertEquals("7,3", findCrashPosition(state));
    }
}
