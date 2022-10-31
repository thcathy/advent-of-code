package com.adventofcode.year2018;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day13Part2 {
    final static String inputFile = "2018/day13.txt";

    public static void main(String... args) throws IOException {
        Day13Part2 solution = new Day13Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = lastCartPosition(parseInput(lines));
        System.out.println("the location of the first crash: " + result);
    }

    String lastCartPosition(State state) {
        while (state.carts.size() > 1) {
            tick(state);            
        }
        var position = state.carts.get(0).position;
        return position.x + "," + position.y;
    }

    void tick(State state) {
        state.carts.sort(Comparator.comparing((Cart c) -> c.position.y).thenComparing(c -> c.position.x));
        for (Cart cart : state.carts) {
            move(state, cart);            
        }
        state.carts = state.carts.stream().filter(c -> !c.crashed)
                        .collect(Collectors.toCollection(ArrayList::new));;
    }

    void move(State state, Cart cart) {        
        if (cart.crashed) return;

        var next = cart.nextPosition();
        var cartsInNextPosition = state.carts.stream().filter(c -> c.position.equals(next)).toList();
        if (cartsInNextPosition.size() > 0) {
            cart.crashed = true;
            cartsInNextPosition.forEach(c -> c.crashed = true);
        }
        cart.position = next;
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
        
        public void addCart(Cart cart) {
            carts.add(cart);
            cartPositions.add(cart.position);
        }
    }

    class Cart {
        boolean crashed = false;
        Direction direction;
        Position position;
        Turn nextTurn = Turn.Left;

        Position nextPosition() {
            return switch (direction) {
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
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day13_test2.txt"), Charsets.UTF_8);
        var state = parseInput(lines);

        assertEquals(9, state.carts.size());
        assertEquals("6,4", lastCartPosition(state));
    }
}
