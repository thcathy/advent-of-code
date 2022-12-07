package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day7Part2 {
    final static String inputFile = "2022/day7.txt";
    final static int TOTAL_SPACE = 70000000;
    final static int REQUIRE_SPACE = 30000000;

    public static void main(String... args) throws IOException {
        Day7Part2 solution = new Day7Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = smallestDirectorySizeToDelete(parseDirectory(lines));
        System.out.println("What is the total size of that directory? " + result);
    }

    long smallestDirectorySizeToDelete(Directory root) {
        var sizesMap = new HashMap<Directory, Long>();
        findAllSizes(root, sizesMap);
        long sizeToDelete = REQUIRE_SPACE - (TOTAL_SPACE - sizesMap.get(root));
        return sizesMap.values().stream().filter(v -> v >= sizeToDelete).mapToLong(v -> v).min().getAsLong();
    }

    long findAllSizes(Directory dir, Map<Directory, Long> sizes) {
        var size = dir.size + dir.subDirectories.stream().mapToLong(d -> findAllSizes(d, sizes)).sum();
        sizes.put(dir, size);
        return size;
    }

    Directory parseDirectory(List<String> inputs) {
        Directory root = new Directory("/", null);
        Directory current = root;
        inputs.remove(0);

        for (String input : inputs) {
            if (input.startsWith("dir"))
                continue;
            else if (!input.startsWith("$")) {
                current.size += Long.parseLong(input.split(" ")[0]);
            } else if (input.startsWith("$ cd ")) {
                current = changeDirectory(input.substring(5), current);
            }
        }
        return root;
    }

    Directory changeDirectory(String name, Directory current) {
        if (name.equals("..")) {
            return current.parent;
        } else {
            var newDir = new Directory(name, current);
            current.subDirectories.add(newDir);
            return newDir;
        }
    }

    class Directory {
        String name;
        long size = 0;
        Directory parent;
        List<Directory> subDirectories = new ArrayList<>();

        Directory(String name, Directory parent) { 
            this.name = name; 
            this.parent = parent;
        }
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day7_test.txt").toURI()), Charset.defaultCharset());
        var result = smallestDirectorySizeToDelete(parseDirectory(lines));
        assertEquals(24933642, result);
    }
}
