package com.kjipo.font;

import javax.annotation.Nonnull;
import java.util.List;

public class FontPathElement {
    private final PathCommand command;
    private final List<Double> numbers;


    FontPathElement(@Nonnull PathCommand command, @Nonnull List<Double> numbers) {
        this.command = command;
        this.numbers = numbers;
    }


    public PathCommand getCommand() {
        return command;
    }

    public List<Double> getNumbers() {
        return numbers;
    }

}
