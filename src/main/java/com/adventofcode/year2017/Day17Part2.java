package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class Day17Part2 {
    final static int ENDING_SIZE = 50_000_000;
    final static int STEPPING = 354;

    public static void main(String... args) throws IOException {
        Day17Part2 solution = new Day17Part2();
        solution.run();
    }

    void run() throws IOException {
        var result = findShortCircuitNumber(STEPPING, ENDING_SIZE);
        System.out.println("What is the value after 0 the moment 50000000 is inserted? " + result);
    }

    int findShortCircuitNumber(int stepping, int finalSize) {
        SpinLock spinLock = new SpinLock();                
        for (int i = 1; i < finalSize; i++) {
            for (int j = 0; j < stepping; j++) {
                spinLock.next();
            }
            spinLock.add(i);            
        }
        return spinLock.originNode.next.value;
    }

    class SpinLock {
        Node head;
        Node current;
        Node originNode;

        SpinLock() {
            Node node = new Node(0, null);
            node.next = node;
            head = node;
            originNode = node;
            current = node;
        }
        
        void add(int value) {
            Node newNode = new Node(value, current.next);
            current.next = newNode;
            current = newNode;
        }

        void next() {
            current = current.next; }
    }

    class Node {
        int value;
        Node next;

        Node(int value, Node next) {
            this.value = value;
            this.next = next;
        }
    }
    
    @Test
    public void unitTest() {        
        assertEquals(638, findShortCircuitNumber(3, 2018));
    }
}
