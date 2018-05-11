package com.example.todd.calculator;

import java.util.ArrayDeque;

public class StringStack {
    private ArrayDeque<String> dataStack = new ArrayDeque<>();

    public String pop() {
        return dataStack.removeFirst();
    }

    public void push(String e) {
        dataStack.addFirst(e);
    }

    public boolean isEmpty() {
        return dataStack.isEmpty();
    }

    public String peek() {
        return dataStack.peek();
    }

    public void clear() {
        dataStack.clear();
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(dataStack.toString());
        builder.deleteCharAt(0);
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
}
