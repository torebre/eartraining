package com.kjipo.font;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.google.common.collect.Iterators;
import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.function.ToDoubleFunction;


public class ReadFonts {
    private static final String GLYPH = "glyph";

    private static DecimalFormat decimalFormat;

    static {
        decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(3);
        decimalFormat.setGroupingUsed(false);
    }

    private static final Map<String, String> glyphElementMapping;

    static {
        Map<String, String> temp = new HashMap<String, String>();
        temp.put("clefs.G", "G_CLEF");
        temp.put("noteheads.s0", "WHOLE_NOTE");
        temp.put("noteheads.s1", "HALFNOTE");
        temp.put("noteheads.s2", "QUARTERNOTE");

        glyphElementMapping = Collections.unmodifiableMap(temp);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadFonts.class);


    private static String transformToSquare(String path, double boundingBox[]) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
//        List<Character> number = new ArrayList<Character>();

        List<FontPathElement> fontPathElements = new ArrayList<>();

        double upperLeftCorner[] = new double[]{0, 0};

        double translateX = upperLeftCorner[0] - boundingBox[0];
        double translateY = upperLeftCorner[1] - boundingBox[3];

        double xLength = boundingBox[2] - boundingBox[0];
        double yLength = boundingBox[3] - boundingBox[1];

        // TODO Put scale back
        double scale = 1.0; // diagonalLength / max;
        double unitsPerEm = boundingBox[3] - boundingBox[1];

        ByteArrayInputStream charStream = new ByteArrayInputStream(path.getBytes());



        int c = charStream.read();
        boolean absolute = false;
        while (c != -1) {
            absolute = false;

            switch (c) {

                case 'v':
                    Map.Entry<Integer, FontPathElement> onlyElement2 = Iterators.getOnlyElement(handleLowercaseV(charStream).entrySet().iterator());
                    c = onlyElement2.getKey();
                    fontPathElements.add(onlyElement2.getValue());
//                    c = charStream.read();
                    break;

//                    stringBuilder.append((char) c).append(" ");
//
//                    do {
//                        c = readNumber(charStream, number);
//                        if (number.isEmpty()) {
//                            break;
//                        }
//                        double temp = parseNumber(number);
//                        if (absolute) {
//                            temp = unitsPerEm - temp + translateY;
//                        } else {
//                            temp *= -1;
//                        }
//                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
//                        number.clear();
//
//                    } while (!CharUtils.isAsciiAlpha((char) c));
//
////                    c = charStream.read();
//                    break;

//                case 'H':
//                    absolute = true;
                case 'h':
                    Map.Entry<Integer, FontPathElement> parsedElement = Iterators.getOnlyElement(handleLowerCaseH(charStream).entrySet().iterator());
                    c = parsedElement.getKey();
                    fontPathElements.add(parsedElement.getValue());
//                    c = charStream.read();
                    break;


//                    stringBuilder.append((char) c).append(" ");
//
//                    do {
//                        c = readNumber(charStream, number);
//                        if (number.isEmpty()) {
//                            break;
//                        }
//                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
//                        number.clear();
//
//                    } while (!CharUtils.isAsciiAlpha((char) c));
//
////                    c = charStream.read();
//                    break;


                case 'M':
                    absolute = true;
                    stringBuilder.append("M 0 0 ");
                    c = 'm';
                case 'm':
                case 'l':
                    Map.Entry<Integer, FontPathElement> onlyElement1 = Iterators.getOnlyElement(handleLowerCaseL(charStream).entrySet().iterator());
                    c = onlyElement1.getKey();
                    fontPathElements.add(onlyElement1.getValue());
//                    c = charStream.read();
                    break;

//                    stringBuilder.append((char) c).append(" ");
//
//                    do {
//                        c = readNumber(charStream, number);
//                        if (number.isEmpty()) {
//                            break;
//                        }
//
//                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
//                        number.clear();
//                        c = readNumber(charStream, number);
//                        if (number.isEmpty()) {
//                            break;
//                        }
//                        double temp = parseNumber(number);
//                        if (absolute) {
//                            temp = unitsPerEm - temp + translateY;
//                        } else {
//                            temp *= -1;
//                        }
//                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
//                        number.clear();
//                    }
//                    while (!CharUtils.isAsciiAlpha((char) c));
//                    break;

                case 'c':
                    Map.Entry<Integer, FontPathElement> onlyElement = Iterators.getOnlyElement(handleLowerCaseC(charStream).entrySet().iterator());
                    c = onlyElement.getKey();
                    fontPathElements.add(onlyElement.getValue());
//                    c = charStream.read();
                    break;


//                    stringBuilder.append((char) c).append(" ");
//
//                    do {
//                        c = readNumber(charStream, number);
//
//                        if (number.isEmpty()) {
//                            break;
//                        }
//
//                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
//                        number.clear();
//                        c = readNumber(charStream, number);
//                        if (number.isEmpty()) {
//                            break;
//                        }
//                        double temp = parseNumber(number);
//                        if (absolute) {
//                            temp = unitsPerEm - temp + translateY;
//                        } else {
//                            temp *= -1;
//                        }
//                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
//                        number.clear();
//
//                        c = readNumber(charStream, number);
//                        if (number.isEmpty()) {
//                            break;
//                        }
//
//
//                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
//                        number.clear();
//                        c = readNumber(charStream, number);
//                        if (number.isEmpty()) {
//                            break;
//                        }
//                        temp = parseNumber(number);
//                        if (absolute) {
//                            temp = unitsPerEm - temp + translateY;
//                        } else {
//                            temp *= -1;
//                        }
//                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
//                        number.clear();
//
//                        c = readNumber(charStream, number);
//                        if (number.isEmpty()) {
//                            break;
//                        }
//
//                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
//                        number.clear();
//                        c = readNumber(charStream, number);
//                        if (number.isEmpty()) {
//                            break;
//                        }
//                        temp = parseNumber(number);
//                        if (absolute) {
//                            temp = unitsPerEm - temp + translateY;
//                        } else {
//                            temp *= -1;
//                        }
//                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
//                        number.clear();
//
//                    }
//                    while (!CharUtils.isAsciiAlpha((char) c));
////                    c = charStream.read();
//                    break;

                case 'z':
                    stringBuilder.append((char) c).append(" ");
                    c = charStream.read();
                    break;


                default:

                    LOGGER.info("Hit default: {}", (char) c);

                    stringBuilder.append((char) c).append(" ");
                    c = charStream.read();
            }

        }

        // TODO Remember to check if number if empty before returning

//        return breakStringIntoLines(stringBuilder.toString());
        return stringBuilder.toString();
    }


    private static Map<Integer, FontPathElement> handleLowerCaseL(InputStream charStream) throws IOException {
//        stringBuilder.append((char) c).append(" ");
        int c;
        List<Character> number = new ArrayList<>();
        List<Double> numbers = new ArrayList<>();

        do {
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }

            numbers.add(parseNumber(number));

//            stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
            number.clear();
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }
//            double temp = parseNumber(number);
//            if (absolute) {
//                temp = unitsPerEm - temp + translateY;
//            } else {
//                temp *= -1;
//            }

            numbers.add(parseNumber(number));
//            stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
            number.clear();
        }
        while (!CharUtils.isAsciiAlpha((char) c));

        return Collections.singletonMap(c, new FontPathElement('l', numbers));
    }


    private static Map<Integer, FontPathElement> handleLowercaseV(InputStream charStream) throws IOException {
        int c;
        List<Character> number = new ArrayList<Character>();
//        stringBuilder.append((char) c).append(" ");

        List<Double> numbers = new ArrayList<>();
        do {
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }
            numbers.add(parseNumber(number));

//            if (absolute) {
//                temp = unitsPerEm - temp + translateY;
//            } else {
//                temp *= -1;
//            }
//            stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
            number.clear();

        } while (!CharUtils.isAsciiAlpha((char) c));


        return Collections.singletonMap(c, new FontPathElement('v', numbers));

    }

    private static Map<Integer, FontPathElement> handleLowerCaseH(InputStream charStream) throws IOException {
//        stringBuilder.append((char) c).append(" ");
        int c;
        List<Character> number = new ArrayList<Character>();
        List<Double> numbers = new ArrayList<>();

        do {
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }
            numbers.add(parseNumber(number));
//            stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
            number.clear();

        } while (!CharUtils.isAsciiAlpha((char) c));



        return Collections.singletonMap(c, new FontPathElement('h', numbers));

    }


    private static Map<Integer, FontPathElement> handleLowerCaseC(InputStream charStream) throws IOException {
//        stringBuilder.append((char) c).append(" ");
        int c;
        List<Character> number = new ArrayList<>();
        List<Double> numbers = new ArrayList<>();

        do {
            c = readNumber(charStream, number);

            if (number.isEmpty()) {
                break;
            }

            numbers.add(parseNumber(number));

//            stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
            number.clear();
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }
            double temp = parseNumber(number);
//            if (absolute) {
//                temp = unitsPerEm - temp + translateY;
//            } else {
//                temp *= -1;
//            }

            numbers.add(temp);

//            stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
            number.clear();

            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }


            numbers.add(parseNumber(number));

//            stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
            number.clear();
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }
//            temp = parseNumber(number);

            numbers.add(parseNumber(number));
//            if (absolute) {
//                temp = unitsPerEm - temp + translateY;
//            } else {
//                temp *= -1;
//            }
//            stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
            number.clear();

            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }

//            stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
            number.clear();
            c = readNumber(charStream, number);
            if (number.isEmpty()) {
                break;
            }
//            temp = parseNumber(number);
            numbers.add(parseNumber(number));

//            if (absolute) {
//                temp = unitsPerEm - temp + translateY;
//            } else {
//                temp *= -1;
//            }
//            stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
            number.clear();

        }
        while (!CharUtils.isAsciiAlpha((char) c));

        return Collections.singletonMap(c, new FontPathElement('c', numbers));


    }



//    private boolean readValue(StringBuilder stringBuilder, List<Character> number, boolean absolute, double translateX,
//                              double translateY, double scale, double unitsPerEm, ByteArrayInputStream charStream ) throws IOException {
//        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
//        number.clear();
//        c = readNumber(charStream, number);
//        if(number.isEmpty()) {
//            return true;
//        }
//        double temp = parseNumber(number);
//        if(absolute) {
//            temp = unitsPerEm - temp + translateY;
//        }
//        else {
//            temp *= -1;
//        }
//        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
//        number.clear();
//        return false;
//    }


    private static final int APPROXIMATE_CHARACTERS_ON_LINE = 200;


    private static String breakStringIntoLines(String input) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        int charactersProcessed = 0;
        for (byte b : input.getBytes("UTF-8")) {
            if (charactersProcessed >= APPROXIMATE_CHARACTERS_ON_LINE) {
                if (b == ' ') {
                    result.append("\\ \n");
                    charactersProcessed = 0;
                    continue;
                }
            }
            result.append((char) b);
            ++charactersProcessed;
        }
        return result.toString();
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
                c == '-' ||
                c == '.') {
            number.add(new Character(c));
            c = (char) input.read();
        }
        return c;
    }



    private static void writeGlyphElementsToStream(InputStream inputStream, OutputStream outputStream) throws XMLStreamException, IOException {
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        XMLStreamReader xmlEventReader = inputFactory.createXMLStreamReader(inputStream, StandardCharsets.UTF_8.name());
        Map<String, String> namePathMapping = new HashMap<String, String>();
        double fontBoundingBox[] = null;

        while (xmlEventReader.hasNext()) {
            xmlEventReader.next();

            if (!xmlEventReader.isStartElement()) {
                continue;
            }

            String localName = xmlEventReader.getName().getLocalPart();

            if (localName.equals("font-face")) {
                fontBoundingBox = Arrays.stream(xmlEventReader.getAttributeValue("", "bbox").split(" ")).mapToDouble(new ToDoubleFunction<String>() {
                    @Override
                    public double applyAsDouble(String value) {
                        return Double.valueOf(value);
                    }
                }).toArray();
            } else if (localName.equals(GLYPH)) {
                String glyphName = xmlEventReader.getAttributeValue("", "glyph-name");
                if (!glyphElementMapping.containsKey(glyphName)) {
                    continue;
                }
                String elementName = glyphElementMapping.get(glyphName);
                namePathMapping.put(elementName == null ? glyphName : elementName, xmlEventReader.getAttributeValue("", "d"));
            }

        }

        writeGlyphDataAsJson(namePathMapping, fontBoundingBox, outputStream);
    }


    private static void writeGlyphDataAsJson(Map<String, String> namePathMapping, double fontBoundingBox[], OutputStream outputStream) throws IOException {
        JsonFactory factory = new JsonFactory();
        ByteArrayOutputStream tempOutputStream = new ByteArrayOutputStream();
        com.fasterxml.jackson.core.JsonGenerator jsonGenerator = factory.createGenerator(tempOutputStream, JsonEncoding.UTF8);
        jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());

        jsonGenerator.writeStartObject();

        jsonGenerator.writeFieldName("boundingBox");
        jsonGenerator.writeStartArray();
        for (double d : fontBoundingBox) {
            jsonGenerator.writeNumber(d);
        }
        jsonGenerator.writeEndArray();


        for (String glyphName : namePathMapping.keySet()) {
//            jsonGenerator.writeStartObject();

            jsonGenerator.writeFieldName(glyphName);
            jsonGenerator.writeStartObject();

//            jsonGenerator.writeStringField("name", glyphName);
            jsonGenerator.writeStringField("d", transformToSquare(namePathMapping.get(glyphName), fontBoundingBox));
//            jsonGenerator.writeEndObject();
//
            jsonGenerator.writeEndObject();
        }
//        jsonGenerator.writeEndArray();
//        jsonGenerator.writeEndObject();

        jsonGenerator.close();

        JsonParser parser = factory.createParser(tempOutputStream.toByteArray());
        JsonToken jsonToken;
        while ((jsonToken = parser.nextToken()) != null);


        outputStream.write(tempOutputStream.toByteArray());
    }


    private static void transformFont() throws IOException, XMLStreamException {
        InputStream inputStream = ReadFonts.class.getResourceAsStream("/gonville-r9313/lilyfonts/svg/gonvillepart1.svg");
//        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/test_output.js")));
//        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/all_glyphs.js")));

        Path fontOutput = Paths.get("glyphs.json");
        try (OutputStream outputStream = Files.newOutputStream(fontOutput, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            writeGlyphElementsToStream(inputStream, bufferedOutputStream);
        }
    }


    public static void main(String args[]) throws Exception {
        transformFont();

    }


}
