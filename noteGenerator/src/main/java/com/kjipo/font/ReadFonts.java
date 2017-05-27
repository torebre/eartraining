package com.kjipo.font;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;


public class ReadFonts {
    private static final int APPROXIMATE_CHARACTERS_ON_LINE = 200;
    private static final String GLYPH = "glyph";

    private static DecimalFormat decimalFormat;

    static {
        decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(3);
        decimalFormat.setGroupingUsed(false);
    }

    private static final Map<String, String> glyphElementMapping;

    static {
        Map<String, String> temp = new HashMap<>();
        temp.put("clefs.G", "G_CLEF");
//        temp.put("noteheads.s0", "WHOLE_NOTE");
//        temp.put("noteheads.s1", "HALFNOTE");
//        temp.put("noteheads.s2", "QUARTERNOTE");

        glyphElementMapping = Collections.unmodifiableMap(temp);
    }

    private static final String SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadFonts.class);


    private static String transformToSquare(String path, double boundingBox[]) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        List<FontPathElement> fontPathElements = new ArrayList<>();

        double upperLeftCorner[] = new double[]{0, 0};
        double translateX = upperLeftCorner[0] - boundingBox[0];
        double translateY = upperLeftCorner[1] - boundingBox[3];

        double xLength = boundingBox[2] - boundingBox[0];
        double yLength = boundingBox[3] - boundingBox[1];

        // TODO Put scale back
        double scale = 1.0; // diagonalLength / max;
        double unitsPerEm = boundingBox[3] - boundingBox[1];

//        ByteArrayInputStream charStream = new ByteArrayInputStream(path.getBytes());
//
//        int c = charStream.read();
//        Map.Entry<Integer, FontPathElement> parsedElement;
//
//        while (c != -1) {
//            switch (c) {
//
//                case 'v':
//                    parsedElement = Iterators.getOnlyElement(handleLowercaseV(charStream).entrySet().iterator());
//                    c = parsedElement.getKey();
//                    fontPathElements.add(parsedElement.getValue());
//                    break;
//
////                case 'H':
////                    absolute = true;
//                case 'h':
//                    parsedElement = Iterators.getOnlyElement(handleLowerCaseH(charStream).entrySet().iterator());
//                    c = parsedElement.getKey();
//                    fontPathElements.add(parsedElement.getValue());
//                    break;
//
//                case 'M':
//                    parsedElement = Iterators.getOnlyElement(handleGeneric('M', charStream).entrySet().iterator());
//                    c = parsedElement.getKey();
//                    fontPathElements.add(parsedElement.getValue());
//                    break;
//
//                case 'm':
//                    parsedElement = Iterators.getOnlyElement(handleGeneric('m', charStream).entrySet().iterator());
//                    c = parsedElement.getKey();
//                    fontPathElements.add(parsedElement.getValue());
//                    break;
//
//                case 'l':
//                    parsedElement = Iterators.getOnlyElement(handleLowerCaseL(charStream).entrySet().iterator());
//                    c = parsedElement.getKey();
//                    fontPathElements.add(parsedElement.getValue());
//                    break;
//
//                case 'c':
//                    parsedElement = Iterators.getOnlyElement(handleLowerCaseC(charStream).entrySet().iterator());
//                    c = parsedElement.getKey();
//                    fontPathElements.add(parsedElement.getValue());
//                    break;
//
//                case 'z':
//                    stringBuilder.append((char) c).append(" ");
//                    c = charStream.read();
//                    break;
//
//                default:
//
//                    LOGGER.info("Hit default: {}", (char) c);
//
//                    stringBuilder.append((char) c).append(" ");
//                    c = charStream.read();
//            }
//
//        }

        // TODO Remember to check if number if empty before returning

//        return breakStringIntoLines(stringBuilder.toString());
        return stringBuilder.toString();
    }


    private static void writePathsToSvgFile(Path path, InputStream fontData) throws XMLStreamException, IOException, TransformerException {
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        XMLStreamReader xmlEventReader = inputFactory.createXMLStreamReader(fontData, StandardCharsets.UTF_8.name());
//        Map<String, String> namePathMapping = new HashMap<>();
        double fontBoundingBox[] = null;


        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = impl.createDocument(svgNS, "svg", null);

        SVGDocument svgDocument = (SVGDocument) doc;
        SVGSVGElement rootElement = svgDocument.getRootElement();

        int currentX = 0;
        while (xmlEventReader.hasNext()) {
            xmlEventReader.next();

            if (!xmlEventReader.isStartElement()) {
                continue;
            }

            String localName = xmlEventReader.getName().getLocalPart();

            String pathString;
            if (localName.equals("font-face")) {
                fontBoundingBox = Arrays.stream(xmlEventReader.getAttributeValue("", "bbox").split(" ")).mapToDouble(new ToDoubleFunction<String>() {
                    @Override
                    public double applyAsDouble(String value) {
                        return Double.valueOf(value);
                    }
                }).toArray();
            } else if (localName.equals(GLYPH)) {
                String glyphName = xmlEventReader.getAttributeValue("", "glyph-name");
//                if (!glyphElementMapping.containsKey(glyphName)) {
//                    continue;
//                }
                LOGGER.info("Processing glyph: " + glyphName);
                String elementName = glyphElementMapping.get(glyphName);
                String pathAttribute = xmlEventReader.getAttributeValue("", "d");
                String horizontalMovement = xmlEventReader.getAttributeValue("", "horiz-adv-x");
                pathString = transformToSquare2(pathAttribute, currentX, 1000);
                if (horizontalMovement != null) {
                    currentX += Integer.valueOf(horizontalMovement);
                } else {
                    LOGGER.info("No horizontal movement");
                    currentX += 200;
                }
                addPath(svgDocument, rootElement, pathString);
            }
        }

        rootElement.setAttributeNS(null, "width", "" + (currentX + 200));
        rootElement.setAttributeNS(null, "height", "2000");


        DOMSource source = new DOMSource(svgDocument);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);) {
            StreamResult result = new StreamResult(bufferedWriter);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
        }

    }

    private static void addPath(Document document, Node node, String path) {
        Element path1 = document.createElementNS(SVG_NAMESPACE_URI, "path");
        path1.setAttribute("d", path);
        path1.setAttribute("stroke", "blue");
        path1.setAttribute("fill", "yellow");
        node.appendChild(path1);
    }


    private static String transformToSquare2(String pathData, int xOffset, int yOffset) throws IOException {
        List<FontPathElement> fontPathElements = parsePathData(pathData);
        String result = fontPathElements.stream()
                .map(fontPathElement -> {
                    // TODO Only handling M for now
                    if (fontPathElement.getCommand() == 'M') {
                        return new FontPathElement('M',
                                Lists.newArrayList(fontPathElement.getNumbers().get(0) + xOffset,
                                        fontPathElement.getNumbers().get(1) + yOffset));
                    }
                    return fontPathElement;
                })
                .map(fontPathElement -> fontPathElement.getCommand() + " " + fontPathElement.getNumbers().stream()
                        .map(decimalFormat::format)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(" "));
        return result;
    }

    private static List<FontPathElement> parsePathData(String pathData) throws IOException {
        ByteArrayInputStream charStream = new ByteArrayInputStream(pathData.getBytes());
        List<FontPathElement> fontPathElements = new ArrayList<>();

        int c = charStream.read();
        Map.Entry<Integer, FontPathElement> parsedElement;

        while (c != -1) {
            switch (c) {

                case 'v':
                    parsedElement = Iterators.getOnlyElement(handleLowercaseV(charStream).entrySet().iterator());
                    c = parsedElement.getKey();
                    fontPathElements.add(parsedElement.getValue());
                    break;

//                case 'H':
//                    absolute = true;
                case 'h':
                    parsedElement = Iterators.getOnlyElement(handleLowerCaseH(charStream).entrySet().iterator());
                    c = parsedElement.getKey();
                    fontPathElements.add(parsedElement.getValue());
                    break;

                case 'M':
                    parsedElement = Iterators.getOnlyElement(handleGeneric('M', charStream).entrySet().iterator());
                    c = parsedElement.getKey();
                    fontPathElements.add(parsedElement.getValue());
                    break;

                case 'm':
                    parsedElement = Iterators.getOnlyElement(handleGeneric('m', charStream).entrySet().iterator());
                    c = parsedElement.getKey();
                    fontPathElements.add(parsedElement.getValue());
                    break;

                case 'l':
                    parsedElement = Iterators.getOnlyElement(handleLowerCaseL(charStream).entrySet().iterator());
                    c = parsedElement.getKey();
                    fontPathElements.add(parsedElement.getValue());
                    break;

                case 'c':
                    parsedElement = Iterators.getOnlyElement(handleLowerCaseC(charStream).entrySet().iterator());
                    c = parsedElement.getKey();
                    fontPathElements.add(parsedElement.getValue());
                    break;

                case 'z':
                    fontPathElements.add(new FontPathElement('z', Collections.emptyList()));
//                    stringBuilder.append((char) c).append(" ");
                    c = charStream.read();
                    break;

                case 's':
                    parsedElement = Iterators.getOnlyElement(handleGeneric('s', charStream).entrySet().iterator());
                    c = parsedElement.getKey();
                    fontPathElements.add(parsedElement.getValue());
                    break;

                default:

                    LOGGER.error("Hit default: {}", (char) c);

//                    stringBuilder.append((char) c).append(" ");
                    c = charStream.read();
            }

        }

        return fontPathElements;
    }


    private static Map<Integer, FontPathElement> handleLowerCaseL(InputStream charStream) throws IOException {
        return handleGeneric('l', charStream);
    }

    private static Map<Integer, FontPathElement> handleGeneric(char character, InputStream charStream) throws IOException {
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

        return Collections.singletonMap(c, new FontPathElement(character, numbers));
    }


    private static Map<Integer, FontPathElement> handleLowercaseV(InputStream charStream) throws IOException {
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

        return Collections.singletonMap(c, new FontPathElement('v', numbers));

    }

    private static Map<Integer, FontPathElement> handleLowerCaseH(InputStream charStream) throws IOException {
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

        return Collections.singletonMap(c, new FontPathElement('c', numbers));
    }


    private static String breakStringIntoLines(String input) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        int charactersProcessed = 0;
        for (byte b : input.getBytes(StandardCharsets.UTF_8)) {
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
                c == 'e' ||
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
        Map<String, String> namePathMapping = new HashMap<>();
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
        while ((jsonToken = parser.nextToken()) != null) ;


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
//        transformFont();

        Path outputFilePath = Paths.get("output.xml");
        try (InputStream inputStream = ReadFonts.class.getResourceAsStream("/gonville-r9313/lilyfonts/svg/gonvillepart1.svg")) {
            writePathsToSvgFile(outputFilePath, inputStream);
        }

    }


}
