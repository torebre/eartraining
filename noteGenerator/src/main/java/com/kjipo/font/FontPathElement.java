package com.kjipo.font;

import java.util.List;

public class FontPathElement {
    private final char command;
    private final List<Double> numbers;


    FontPathElement(char command, List<Double> numbers) {
        this.command = command;
        this.numbers = numbers;
    }


    public char getCommand() {
        return command;
    }

    public List<Double> getNumbers() {
        return numbers;
    }
}
