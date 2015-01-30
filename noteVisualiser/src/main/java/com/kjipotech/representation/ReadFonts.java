package com.kjipotech.representation;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;

/**
 * Created by student on 12/19/14.
 */
public class ReadFonts {

    private static final String GLYPH = "glyph";
    private static final String UTF8 = "UTF-8";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadFonts.class);

//    private static final double scaleFactor = 0.1;

    // TODO Only here temporarily for testing

//    private static final double xTranslate = 0; // -350;
//    private static final double yTranslate = 0; // -200;


    private static String transformToSquare(String path) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        List<Character> number = new ArrayList<>();
        double translateX = Double.NaN;
        double translateY = Double.NaN;

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(3);
        decimalFormat.setGroupingUsed(false);


        double fontBoundingBox[] = new double[] {-510.142, -1487.78, 852.632, 1370.06};
        double upperLeftCorner[] = new double[] {0, 0};
        double diagonalLength = Math.sqrt(2) * 200;

        translateX = upperLeftCorner[0] - fontBoundingBox[0];
        translateY = upperLeftCorner[1] - fontBoundingBox[3];

        double xLength = fontBoundingBox[2] - fontBoundingBox[0];
        double yLength = fontBoundingBox[3] - fontBoundingBox[1];

        double max = Math.max(xLength, yLength);

        // TODO Put scale back
        double scale = 1.0; // diagonalLength / max;

        char currentlyProcessing = ' ';
        boolean translateNextNumber = false;
        int processingArgument = 0;

        double unitsPerEm = fontBoundingBox[3] - fontBoundingBox[1];

        ByteArrayInputStream charStream = new ByteArrayInputStream(path.getBytes());

        int c = charStream.read();
        boolean absolute = false;
        while(c != -1) {
            absolute = false;

            switch(c) {

//                case 'V':
//                    absolute = true;
                case 'v':

                    stringBuilder.append((char)c).append(" ");

                    do {
                        c = readNumber(charStream, number);
                        if(number.isEmpty()) {
                            break;
                        }
                        double temp = parseNumber(number);
                        if(absolute) {
                            temp = unitsPerEm - temp + translateY;
                        }
                        else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();

                    } while(!CharUtils.isAsciiAlpha((char)c));

//                    c = charStream.read();
                    break;

//                case 'H':
//                    absolute = true;
                case 'h':
                    stringBuilder.append((char)c).append(" ");

                    do {
                        c = readNumber(charStream, number);
                        if(number.isEmpty()) {
                            break;
                        }
                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();

                    } while(!CharUtils.isAsciiAlpha((char)c));

//                    c = charStream.read();
                    break;


                case 'M':
                    absolute = true;
                    stringBuilder.append("M 0 0 ");
                    c = 'm';
                case 'm':
                case 'l':
                    stringBuilder.append((char)c).append(" ");

                    do {
                        c = readNumber(charStream, number);
                        if(number.isEmpty()) {
                            break;
                        }

                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();
                        c = readNumber(charStream, number);
                        if(number.isEmpty()) {
                            break;
                        }
                        double temp = parseNumber(number);
                        if(absolute) {
                            temp = unitsPerEm - temp + translateY;
                        }
                        else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();
                    }
                    while(!CharUtils.isAsciiAlpha((char)c));
                    break;

                case 'c':
                    stringBuilder.append((char)c).append(" ");

                    do {
                        c = readNumber(charStream, number);

                        if(number.isEmpty()) {
                            break;
                        }

                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();
                        c = readNumber(charStream, number);
                        if(number.isEmpty()) {
                            break;
                        }
                        double temp = parseNumber(number);
                        if(absolute) {
                            temp = unitsPerEm - temp + translateY;
                        }
                        else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();

                        c = readNumber(charStream, number);
                        if(number.isEmpty()) {
                            break;
                        }


                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();
                        c = readNumber(charStream, number);
                        if(number.isEmpty()) {
                            break;
                        }

                        temp = parseNumber(number);
                        if(absolute) {
                            temp = unitsPerEm - temp + translateY;
                        }
                        else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();

                        c = readNumber(charStream, number);
                        if(number.isEmpty()) {
                            break;
                        }

                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();
                        c = readNumber(charStream, number);
                        if(number.isEmpty()) {
                            break;
                        }
                        temp = parseNumber(number);
                        if(absolute) {
                            temp = unitsPerEm - temp + translateY;
                        }
                        else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();

                    }
                    while(!CharUtils.isAsciiAlpha((char)c));
//                    c = charStream.read();
                    break;

                case 'z':
                    stringBuilder.append((char)c).append(" ");
                    c = charStream.read();
                    break;


                default:

                    LOGGER.info("Hit default: {}", (char)c);

                    stringBuilder.append((char)c).append(" ");
                    c = charStream.read();
            }

        }

        // TODO Remember to check if number if empty before returning

//        return breakStringIntoLines(stringBuilder.toString());
        return stringBuilder.toString();
    }


    private static final int APPROXIMATE_CHARACTERS_ON_LINE = 200;


    private static String breakStringIntoLines(String input) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        int charactersProcessed = 0;
        for(byte b : input.getBytes("UTF-8")) {
            if(charactersProcessed >= APPROXIMATE_CHARACTERS_ON_LINE) {
                if(b == ' ') {
                    result.append("\\ \n");
                    charactersProcessed = 0;
                    continue;
                }
            }
            result.append((char)b);
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
        char c = (char)input.read();
        while(c == ' ') {
            c = (char)input.read();
        }
        while(CharUtils.isAsciiNumeric(c) ||
                c == '-' ||
                c == '.') {
            number.add(new Character(c));
            c = (char)input.read();
        }
        return c;
    }


    private static void readVexFlowFont() throws IOException {
        JsonFactory factory = new JsonFactory();
//        JsonParser jsonParser = factory.createParser(new File("/home/student/projects/vexflow/src/fonts/gonville_original.js"));
        InputStream inputStream = ReadFonts.class.getResourceAsStream("/VexFlowFont");
        LOGGER.info("Input stream: {}", inputStream);
        JsonParser jsonParser = factory.createParser(inputStream);
        JsonToken jsonToken;

        LOGGER.info("Test1: " +jsonParser.hasCurrentToken());

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/vex_output.txt")));
        com.fasterxml.jackson.core.JsonGenerator jsonGenerator = factory.createGenerator(bufferedOutputStream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
//            jsonGenerator.writeFieldName("glyphs");
        jsonGenerator.writeStartArray();

        int nameCounter = 0;

        while((jsonToken = jsonParser.nextToken()) != null) {

            if("o".equals(jsonParser.getCurrentName())) {

                LOGGER.info("Writing glyph");
                LOGGER.info(jsonToken.toString() +", " +jsonParser.getValueAsString());

                jsonParser.nextToken();

                StringBuilder pathStringOutput = new StringBuilder();
                for(String s : jsonParser.getValueAsString().split(" ")) {
                    switch(s) {
                        case "b":
                            pathStringOutput.append("c").append(" ");
                            break;

                        default:
                            pathStringOutput.append(s).append(" ");

                    }

                }

                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("glyph_name", "glyph" +nameCounter++);

                jsonGenerator.writeStringField("d", transformToSquare(pathStringOutput.toString()));
                jsonGenerator.writeEndObject();

            }

//            LOGGER.info("Current name: {}", jsonParser.getCurrentName());


//            LOGGER.info(jsonToken.toString() +", " +jsonParser.getValueAsString());

        }

        jsonGenerator.writeEndArray();

//            jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        LOGGER.info("Test2");




    }



    private static void transformFont() throws IOException, XMLStreamException {

        try (
                InputStream inputStream = ReadFonts.class.getResourceAsStream("/gonville-r9313/lilyfonts/svg/gonvillepart1.svg");
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/test_output.txt")));
        ) {
            XMLInputFactory inputFactory = XMLInputFactory.newFactory();
            XMLStreamReader xmlEventReader = inputFactory.createXMLStreamReader(inputStream, UTF8);
            Map<String, String> namePathMapping = new HashMap<>();

            while(xmlEventReader.hasNext()) {
                xmlEventReader.next();
                if(xmlEventReader.isStartElement() && GLYPH.equals(xmlEventReader.getName().getLocalPart())) {
                    String glyphName = xmlEventReader.getAttributeValue("", "glyph-name");
                    if(!keepGlyphs.contains(glyphName)) {
                        continue;
                    }
                    namePathMapping.put(glyphName, xmlEventReader.getAttributeValue("", "d"));
                }

            }

            JsonFactory factory = new JsonFactory();
            com.fasterxml.jackson.core.JsonGenerator jsonGenerator = factory.createGenerator(bufferedOutputStream, JsonEncoding.UTF8);
//            jsonGenerator.writeStartObject();
            jsonGenerator.writeStartArray();

            for(String glyphName : keepGlyphs) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("glyph_name", glyphName);
                jsonGenerator.writeStringField("d", transformToSquare(namePathMapping.get(glyphName)));
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
//            jsonGenerator.writeEndObject();
            jsonGenerator.close();
        }

    }

    private static final Set<String> keepGlyphs = Sets.newHashSet("clefs.G",
            "noteheads.s0",
            "noteheads.s1",
            "noteheads.s2"
    );

    public static void main(String args[]) throws Exception {
//        transformFont();
        readVexFlowFont();

    }




}
