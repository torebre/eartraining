package com.kjipo.font;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.kjipo.representation.ElementType;
import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.ToDoubleFunction;



public class ReadFonts {
    private static final String GLYPH = "glyph";

    private static DecimalFormat decimalFormat;

    static {
        decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(3);
        decimalFormat.setGroupingUsed(false);
    }

    private static final Map<String, ElementType> glyphElementMapping = new HashMap<String, ElementType>();

    static {
        glyphElementMapping.put("clefs.G", ElementType.G_CLEF);
        glyphElementMapping.put("noteheads.s0", ElementType.WHOLE_NOTE);
        glyphElementMapping.put("noteheads.s1", ElementType.HALFNOTE);
        glyphElementMapping.put("noteheads.s2", ElementType.QUARTERNOTE);
    }


//    private static final Set<String> keepGlyphs = Sets.newHashSet(
//            "clefs.G",
//            "noteheads.s0",
//            "noteheads.s1",
//            "noteheads.s2"
//    );



    private static final Logger LOGGER = LoggerFactory.getLogger(ReadFonts.class);


    private static String transformToSquare(String path, double boundingBox[]) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        List<Character> number = new ArrayList<Character>();

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

                    stringBuilder.append((char) c).append(" ");

                    do {
                        c = readNumber(charStream, number);
                        if (number.isEmpty()) {
                            break;
                        }
                        double temp = parseNumber(number);
                        if (absolute) {
                            temp = unitsPerEm - temp + translateY;
                        } else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();

                    } while (!CharUtils.isAsciiAlpha((char) c));

//                    c = charStream.read();
                    break;

//                case 'H':
//                    absolute = true;
                case 'h':
                    stringBuilder.append((char) c).append(" ");

                    do {
                        c = readNumber(charStream, number);
                        if (number.isEmpty()) {
                            break;
                        }
                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();

                    } while (!CharUtils.isAsciiAlpha((char) c));

//                    c = charStream.read();
                    break;


                case 'M':
                    absolute = true;
                    stringBuilder.append("M 0 0 ");
                    c = 'm';
                case 'm':
                case 'l':
                    stringBuilder.append((char) c).append(" ");

                    do {
                        c = readNumber(charStream, number);
                        if (number.isEmpty()) {
                            break;
                        }

                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();
                        c = readNumber(charStream, number);
                        if (number.isEmpty()) {
                            break;
                        }
                        double temp = parseNumber(number);
                        if (absolute) {
                            temp = unitsPerEm - temp + translateY;
                        } else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();
                    }
                    while (!CharUtils.isAsciiAlpha((char) c));
                    break;

                case 'c':
                    stringBuilder.append((char) c).append(" ");

                    do {
                        c = readNumber(charStream, number);

                        if (number.isEmpty()) {
                            break;
                        }

                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();
                        c = readNumber(charStream, number);
                        if (number.isEmpty()) {
                            break;
                        }
                        double temp = parseNumber(number);
                        if (absolute) {
                            temp = unitsPerEm - temp + translateY;
                        } else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();

                        c = readNumber(charStream, number);
                        if (number.isEmpty()) {
                            break;
                        }


                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();
                        c = readNumber(charStream, number);
                        if (number.isEmpty()) {
                            break;
                        }
                        temp = parseNumber(number);
                        if (absolute) {
                            temp = unitsPerEm - temp + translateY;
                        } else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();

                        c = readNumber(charStream, number);
                        if (number.isEmpty()) {
                            break;
                        }

                        stringBuilder.append(decimalFormat.format((parseNumber(number) + (absolute ? translateX : 0)) * scale)).append(" ");
                        number.clear();
                        c = readNumber(charStream, number);
                        if (number.isEmpty()) {
                            break;
                        }
                        temp = parseNumber(number);
                        if (absolute) {
                            temp = unitsPerEm - temp + translateY;
                        } else {
                            temp *= -1;
                        }
                        stringBuilder.append(decimalFormat.format(temp * scale)).append(" ");
                        number.clear();

                    }
                    while (!CharUtils.isAsciiAlpha((char) c));
//                    c = charStream.read();
                    break;

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


    private static void readVexFlowFont() throws IOException {

//        "boundingBox":{"yMin":-2065.375,"xMin":-695.53125,"yMax":1901.578125,"xMax":1159.671875}


        double boundingBox[] = new double[]{-2065.375, -695.53125, 1901.578125, 1159.671875};


        JsonFactory factory = new JsonFactory();
        InputStream inputStream = ReadFonts.class.getResourceAsStream("/VexGonville");
        LOGGER.info("Input stream: {}", inputStream);
        JsonParser jsonParser = factory.createParser(inputStream);
        JsonToken jsonToken;

        LOGGER.info("Test1: " + jsonParser.hasCurrentToken());

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/vex_output.txt")));
        com.fasterxml.jackson.core.JsonGenerator jsonGenerator = factory.createGenerator(bufferedOutputStream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
//            jsonGenerator.writeFieldName("glyphs");
        jsonGenerator.writeStartArray();

        int nameCounter = 0;

        while ((jsonToken = jsonParser.nextToken()) != null) {

            LOGGER.info("Current name: {}", jsonParser.getCurrentName());

            if ("o".equals(jsonParser.getCurrentName())) {

                jsonParser.nextToken();

                StringBuilder pathStringOutput = new StringBuilder();
                for (String s : jsonParser.getValueAsString().split(" ")) {
                        if("b".equals(s)) {
                            pathStringOutput.append("c").append(" ");
                            break;
                        }
                        else {
                            pathStringOutput.append(s).append(" ");
                        }
                }

                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("glyph_name", "glyph" + nameCounter++);

                jsonGenerator.writeStringField("d", transformToSquare(pathStringOutput.toString(), boundingBox));
                jsonGenerator.writeEndObject();

            }
        }

        jsonGenerator.writeEndArray();

//            jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
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
                ElementType elementType = glyphElementMapping.get(glyphName);
                namePathMapping.put(elementType == null ? glyphName : elementType.toString(), xmlEventReader.getAttributeValue("", "d"));
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
        for(double d : fontBoundingBox) {
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
        while((jsonToken = parser.nextToken()) != null);


        outputStream.write(tempOutputStream.toByteArray());
    }


    private static void transformFont() throws IOException, XMLStreamException {
        InputStream inputStream = ReadFonts.class.getResourceAsStream("/gonville-r9313/lilyfonts/svg/gonvillepart1.svg");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/test_output.js")));
//        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/all_glyphs.js")));

        writeGlyphElementsToStream(inputStream, bufferedOutputStream);
        bufferedOutputStream.close();
    }


    public static void main(String args[]) throws Exception {
        transformFont();
//        readVexFlowFont();

    }


}
