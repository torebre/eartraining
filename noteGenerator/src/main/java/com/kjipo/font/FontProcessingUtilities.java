package com.kjipo.font;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class FontProcessingUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(FontProcessingUtilities.class);

    private FontProcessingUtilities() {

    }


    static List<PathElement> parsePathData(String pathData) throws IOException {
        LOGGER.info("Processing {}", pathData);

        ByteArrayInputStream charStream = new ByteArrayInputStream(pathData.getBytes());
        List<PathElement> pathElements = new ArrayList<>();

        int c = charStream.read();
        Map.Entry<Integer, PathElement> parsedElement;

        while (c != -1) {
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

                default:

                    LOGGER.error("Hit default: {}", (char) c);

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

        return Collections.singletonMap(c, new PathElement(PathCommand.CURVE_TO_RELATIVE, numbers));
    }


    private static double parseNumber(List<Character> number) {
        char temp[] = new char[number.size()];
        int i = 0;
        for (Character character : number) {
            temp[i++] = character.charValue();
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
            number.add(new Character(c));
            c = (char) input.read();
        }
        return c;
    }

}
