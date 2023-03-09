package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.adventofcode.year2022.Day19Part1.ResourceType.*;
import static org.junit.Assert.assertEquals;

public class Day19Part1 {
    final static String inputFile = "2022/day19.txt";
    final static int MAX_MINUTE = 24;

    public static void main(String... args) throws Exception {
        Day19Part1 solution = new Day19Part1();
        solution.run();
    }

    void run() throws Exception {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfQualityLevels(parseBlueprint(lines));
        System.out.println("What do you get if you add up the quality level of all of the blueprints in your list? " + result);
    }

    int sumOfQualityLevels(List<Blueprint> blueprints) {
        return blueprints.parallelStream().mapToInt(b -> b.id * maxGeodesCollected(b)).sum();
    }

    int maxGeodesCollected(Blueprint blueprint) {
        var firstState = new State();
        firstState.blueprint = blueprint;
        firstState.robots.put(Ore, 1);
        return maxGeodesCollected(firstState, 0, new HashSet<>());
    }

    int maxGeodesCollected(State state, int maxGeodesCollected, HashSet<String> visitedStateKeys) {
        if (state.minute == MAX_MINUTE) {
            return state.resources.getOrDefault(Geode, 0);
        }

        if (possibleMaxGeodeCollected(state) < maxGeodesCollected)
            return maxGeodesCollected;

        for (State newState : buildRobots(state)) {
            if (!addNewKey(newState, visitedStateKeys)) continue;
            maxGeodesCollected = Math.max(maxGeodesCollected, maxGeodesCollected(newState, maxGeodesCollected, visitedStateKeys));
        }
        state = state.collectResourcesAndRobot();
        if (!addNewKey(state, visitedStateKeys)) return maxGeodesCollected;
        maxGeodesCollected = Math.max(maxGeodesCollected, maxGeodesCollected(state, maxGeodesCollected, visitedStateKeys));
        return maxGeodesCollected;
    }

    boolean addNewKey(State state, HashSet<String> visitedStateKeys) {
        var stateKey = state.key();
        if (visitedStateKeys.contains(stateKey)) return false;
        visitedStateKeys.add(stateKey);
        return true;
    }

    int possibleMaxGeodeCollected(State state) {
        int minuteRemain = MAX_MINUTE + 1 - state.minute;
        int maxGeodeCollected = state.robots.getOrDefault(Geode, 0) * minuteRemain;
        maxGeodeCollected += minuteRemain * (minuteRemain - 1) / 2;
        maxGeodeCollected += state.resources.getOrDefault(Geode, 0);
        return maxGeodeCollected;
    }

    List<State> buildRobots(State state) {
        List<State> newStates = new ArrayList<>();
        if (state.buildingRobot.isPresent() || state.minute == MAX_MINUTE) return newStates;

        for (Map.Entry<ResourceType, Map<ResourceType, Integer>> robotCosts : state.blueprint.robotCosts.entrySet()) {
            var robotType = robotCosts.getKey();

            if (state.hasEnoughResources(robotCosts.getValue()) && state.notEnoughRobot(robotType)) {
                newStates.add(buildStateWithRobotBuilding(state, robotCosts.getValue(), robotType));
            }
        }

        return newStates;
    }

    State buildStateWithRobotBuilding(State state, Map<ResourceType, Integer> robotCost, ResourceType geode) {
        var newRobots = new HashMap<>(state.robots);
        var newResources = new HashMap<>(state.resources);
        robotCost.forEach((type, cost) -> newResources.compute(type, (key, resource) -> resource - cost));
        var newState = new State();
        newState.minute = state.minute;
        newState.blueprint = state.blueprint;
        newState.resources = newResources;
        newState.robots = newRobots;
        newState.buildingRobot = Optional.of(geode);
        return newState;
    }

    class State {
        int minute = 0;
        Blueprint blueprint;
        Map<ResourceType, Integer> resources = new HashMap<>();
        Map<ResourceType, Integer> robots = new HashMap<>();
        Optional<ResourceType> buildingRobot = Optional.empty();

        State collectResourcesAndRobot() {
            var newState = new State();
            newState.minute = minute + 1;
            newState.blueprint = blueprint;
            newState.robots = robots;
            newState.resources = new HashMap<>(resources);
            robots.forEach((type, noOfRobot) ->
                    newState.resources.compute(type, (k, resourceQty) -> (resourceQty == null) ? noOfRobot : resourceQty + noOfRobot)
            );
            buildingRobot.ifPresent(robotType -> robots.compute(robotType, (type, count) -> count == null ? 1 : count + 1));
            buildingRobot = Optional.empty();
            return newState;
        }

        boolean hasEnoughResources(Map<ResourceType, Integer> resourcesNeeded) {
            return resourcesNeeded.entrySet().stream().allMatch(entry -> resources.getOrDefault(entry.getKey(), 0) >= entry.getValue());
        }

        boolean notEnoughRobot(ResourceType robotType) {
            int numberOfRobot = robots.getOrDefault(robotType, 0);
            if (robotType == Obsidian) {
                return numberOfRobot < blueprint.robotCosts.get(Geode).get(Obsidian);
            } else if (robotType == Clay) {
                return numberOfRobot <  blueprint.robotCosts.get(Obsidian).get(Clay);
            } else if (robotType == Ore) {
                return numberOfRobot < blueprint.maxOreCost;
            }
            return true;
        }

        String key() {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    minute,
                    resources.getOrDefault(ResourceType.Ore, 0), resources.getOrDefault(ResourceType.Clay, 0),
                    resources.getOrDefault(ResourceType.Obsidian, 0), resources.getOrDefault(ResourceType.Geode, 0),
                    robots.getOrDefault(ResourceType.Ore, 0), robots.getOrDefault(ResourceType.Clay, 0),
                    robots.getOrDefault(ResourceType.Obsidian, 0), robots.getOrDefault(ResourceType.Geode, 0),
                    buildingRobot.isPresent() ? buildingRobot.get().toString() : ""
            );
        }
    }

    List<Blueprint> parseBlueprint(List<String> inputs) {
        return inputs.stream().map(string -> {
            var input = string.split("\\. ");
            var firstSection = input[0].split(" ");
            var id = Integer.parseInt(firstSection[1].split(":")[0]);
            var robotCosts = new HashMap<ResourceType, Map<ResourceType, Integer>>();
            robotCosts.put(Ore, Map.of(Ore, Integer.parseInt(firstSection[6])));
            robotCosts.put(Clay, Map.of(Ore, Integer.parseInt(input[1].split(" ")[4])));
            var obsidianRobotLine = input[2].split(" ");
            robotCosts.put(Obsidian, Map.of(Ore, Integer.parseInt(obsidianRobotLine[4]), Clay, Integer.parseInt(obsidianRobotLine[7])));
            var geodeRobotLine = input[3].split(" ");
            robotCosts.put(Geode, Map.of(Ore, Integer.parseInt(geodeRobotLine[4]), Obsidian, Integer.parseInt(geodeRobotLine[7])));
            return new Blueprint(id, robotCosts,
                    robotCosts.values().stream().mapToInt(m -> m.getOrDefault(Ore, 0)).max().orElseThrow());
        }).toList();
    }

    record Blueprint(int id, Map<ResourceType, Map<ResourceType, Integer>> robotCosts, int maxOreCost) {}

    enum ResourceType { Ore, Clay, Obsidian, Geode }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day19_test.txt").toURI()));
        var blueprints = parseBlueprint(lines);
        assertEquals(2, blueprints.size());
        var blueprint1 = blueprints.get(0);
        assertEquals(1, blueprint1.id);
        assertEquals(4, blueprint1.robotCosts.get(Ore).get(Ore).intValue());
        assertEquals(2, blueprint1.robotCosts.get(Clay).get(Ore).intValue());
        assertEquals(3, blueprint1.robotCosts.get(Obsidian).get(Ore).intValue());
        assertEquals(14, blueprint1.robotCosts.get(Obsidian).get(Clay).intValue());
        assertEquals(2, blueprint1.robotCosts.get(Geode).get(Ore).intValue());
        assertEquals(7, blueprint1.robotCosts.get(Geode).get(Obsidian).intValue());

        assertEquals(9, maxGeodesCollected(blueprint1));
        assertEquals(12, maxGeodesCollected(blueprints.get(1)));

        assertEquals(33, sumOfQualityLevels(blueprints));
    }

}
