package com.kjipo.font;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class FontProcessingUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(FontProcessingUtilities.class);

    private FontProcessingUtilities() {

    }


    static List<PathElement> parsePathData(String pathData) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing {}", pathData);
        }

        ByteArrayInputStream charStream = new ByteArrayInputStream(pathData.getBytes());
        List<PathElement> pathElements = new ArrayList<>();

        int c = charStream.read();
        Map.Entry<Integer, PathElement> parsedElement;
        StringBuilder stringBuilder = new StringBuilder();

        while (c != -1) {
            stringBuilder.append((char)c);

            switch (c) {

                case 'v':
                    parsedElement = handleLowercaseV(charStream).entrySet().iterator().next();
                    c = parsedElement.getKey();
                    pathElements.add(parsedElement.getValue());
                    break;

//                case 'H':
//                    absolute = true;
                case 'h':
                    parsedElement = handleLowerCaseH(charStream).entrySet().iterator().next();
                    c = parsedElement.getKey();
                    pathElements.add(parsedElement.getValue());
                    break;

                case 'M':
                    parsedElement = handleGeneric(PathCommand.MOVE_TO_ABSOLUTE, charStream).entrySet().iterator().next();
                    c = parsedElement.getKey();
                    pathElements.add(parsedElement.getValue());
                    break;

                case 'm':
                    parsedElement = handleGeneric(PathCommand.MOVE_TO_RELATIVE, charStream).entrySet().iterator().next();
                    c = parsedElement.getKey();
                    pathElements.add(parsedElement.getValue());
                    break;

                case 'l':
                    parsedElement = handleLowerCaseL(charStream).entrySet().iterator().next();
                    c = parsedElement.getKey();
                    pathElements.add(parsedElement.getValue());
                    break;

                case 'c':
                    parsedElement = handleLowerCaseC(charStream).entrySet().iterator().next();
                    c = parsedElement.getKey();
                    pathElements.add(parsedElement.getValue());
                    break;

                case 'Z':
                case 'z':
                    pathElements.add(new PathElement(PathCommand.CLOSE_PATH, Collections.emptyList()));
                    c = charStream.read();
                    break;

                case 's':
                    parsedElement = handleGeneric(PathCommand.SMOOTH_CURVE_TO_RELATIVE, charStream).entrySet().iterator().next();
                    c = parsedElement.getKey();
                    pathElements.add(parsedElement.getValue());
                    break;

                case ' ':
                    // Blank space between commands
                    c = charStream.read();
                    break;

                default:

                    LOGGER.error("Hit default: {}. Characters read: {}", (char) c, stringBuilder);

//                    stringBuilder.append((char) c).append(" ");
                    c = charStream.read();
            }

        }

        return pathElements;
    }


    private static Map<Integer, PathElement> handleLowerCaseL(InputStream charStream) throws IOException {
        return handleGeneric(PathCommand.LINE_TO_RELATIVE, charStream);
    }

    private static Map<Integer, PathElement> handleGeneric(PathCommand pathCommand, InputStream charStream) throws IOException {
        int c;
        List<Character> number = new ArrayList<>();
        List<Double> numbers = new ArrayList<>();

        do {
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }

            numbers.add(parseNumber(number));

            number.clear();
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }
            numbers.add(parseNumber(number));
            number.clear();
        }
        while (!CharUtils.isAsciiAlpha((char) c));

        return Collections.singletonMap(c, new PathElement(pathCommand, numbers));
    }


    private static Map<Integer, PathElement> handleLowercaseV(InputStream charStream) throws IOException {
        int c;
        List<Character> number = new ArrayList<>();

        List<Double> numbers = new ArrayList<>();
        do {
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }
            numbers.add(parseNumber(number));
            number.clear();

        } while (!CharUtils.isAsciiAlpha((char) c));

        return Collections.singletonMap(c, new PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, numbers));

    }

    private static Map<Integer, PathElement> handleLowerCaseH(InputStream charStream) throws IOException {
        int c;
        List<Character> number = new ArrayList<>();
        List<Double> numbers = new ArrayList<>();

        do {
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }
            numbers.add(parseNumber(number));
            number.clear();
        } while (!CharUtils.isAsciiAlpha((char) c));

        return Collections.singletonMap(c, new PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, numbers));
    }


    private static Map<Integer, PathElement> handleLowerCaseC(InputStream charStream) throws IOException {
        int c = -1;
        List<Character> number = new ArrayList<>();
        List<Double> numbers = new ArrayList<>();

        do {
            Pair pair = new Pair(-1, false);
            for (int i = 0; i < 6; ++i) {
                pair = processNumber(charStream, number, numbers);
                c = pair.c;
                if (pair.shouldBreak) {
                    break;
                }
            }
            if (pair.shouldBreak) {
                break;
            }
        }
        while (!CharUtils.isAsciiAlpha((char) c));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Numbers: {}", numbers);
        }

        return Collections.singletonMap(c, new PathElement(PathCommand.CURVE_TO_RELATIVE, numbers));
    }


    private static class Pair {
        private final int c;
        private final boolean shouldBreak;

        private Pair(int c, boolean shouldBreak) {
            this.c = c;
            this.shouldBreak = shouldBreak;
        }
    }

    private static Pair processNumber(InputStream charStream, List<Character> number, List<Double> numbers) throws IOException {
        int c = readNumber(charStream, number);
        if (number.isEmpty()) {
            return new Pair(c, true);
        }
        numbers.add(parseNumber(number));
        number.clear();
        return new Pair(c, false);
    }


    private static double parseNumber(List<Character> number) {
        char temp[] = new char[number.size()];
        int i = 0;
        for (Character character : number) {
            temp[i++] = character;
        }
        return Double.valueOf(String.valueOf(temp));
    }

    private static int readNumber(InputStream input, List<Character> number) throws IOException {
        char c = (char) input.read();
        while (c == ' ') {
            c = (char) input.read();
        }
        while (CharUtils.isAsciiNumeric(c) ||
                c == 'e' ||
                c == '-' ||
                c == '.') {
            number.add(c);
            c = (char) input.read();
        }
        return c;
    }

}
