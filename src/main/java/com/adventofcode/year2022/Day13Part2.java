package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Day13Part2 {
    final static String inputFile = "2022/day13.txt";

    public static void main(String... args) throws IOException {
        Day13Part2 solution = new Day13Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = findDecoderKey(lines);
        System.out.println("What is the decoder key for the distress signal? " + result);
    }

    int findDecoderKey(List<String> inputs) {
        var dividerPackets = List.of(parse("[[2]]"), parse("[[6]]"));
        var items = new ArrayList<>(inputs.stream().filter(s -> !Strings.isNullOrEmpty(s)).map(this::parse).toList());
        items.addAll(dividerPackets);
        items.sort(this::compare);

        int key = 1;
        for (int i = 0; i < items.size(); i++) {
            if (dividerPackets.contains(items.get(i)))
                key *= i+1;
        }
        return key;
    }

    int compare(Item left, Item right) {
        if (left instanceof ValueItem leftValue && right instanceof ValueItem rightValue) return leftValue.value - rightValue.value;

        if (left instanceof ValueItem && right instanceof ListItem) return compare(new ListItem(List.of(left)), right);

        if (left instanceof ListItem && right instanceof ValueItem) return compare(left, new ListItem(List.of(right)));

        var list1 = ((ListItem)left).list;
        var list2 = ((ListItem)right).list;
        if (list1.isEmpty()) return list2.isEmpty() ? 0 : -1;

        var minSize = Math.min(list1.size(), list2.size());

        for (int i=0; i<minSize; i++) {
            var value = compare(list1.get(i), list2.get(i));
            if (value != 0) return value;
        }
        return list1.size() - list2.size();
    }

    Item parse(String input) {
        if (input.length() == 0) return new ListItem(Collections.EMPTY_LIST);

        if (input.startsWith("[")) {
            input = input.substring(1, input.length()-1);
            return new ListItem(divideTheList(input).stream().map(this::parse).toList());
        }

        if (!input.contains(",")) return new ValueItem(Integer.parseInt(input));

        throw new RuntimeException();
    }

    List<String> divideTheList(String input) {
        List<String> strings = new ArrayList<>();
        int i = 0, level = 0, start = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '[') {
                level++;
            } else if (input.charAt(i) == ']') {
                level--;
            } else if (input.charAt(i) == ',' && level == 0) {
                strings.add(input.substring(start, i));
                start = i+1;
            }

            i++;
        }
        if (start < input.length())
            strings.add(input.substring(start));
        return strings;
    }

    interface Item {}

    record ValueItem(int value) implements Item {}

    record ListItem(List<Item> list) implements Item {}

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day13_test.txt").toURI()), Charset.defaultCharset());
        assertEquals(140, findDecoderKey(lines));
    }
}
