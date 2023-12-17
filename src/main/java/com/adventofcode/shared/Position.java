package com.adventofcode.shared;

import java.util.List;

public record Position(int x, int y) {
    public Position move(Direction direction) {
        return switch (direction) {
            case NORTH -> new Position(x, y - 1);
            case SOUTH -> new Position(x, y + 1);
            case WEST -> new Position(x - 1, y);
            case EAST -> new Position(x + 1, y);
        };
    }

    public List<Position> neighbors() {
        return List.of(
            new Position(x-1, y),
            new Position(x+1, y),
            new Position(x, y-1),
            new Position(x, y+1)
        );
    }
}
