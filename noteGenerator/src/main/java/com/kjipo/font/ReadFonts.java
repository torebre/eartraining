package com.kjipo.font;


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
import java.util.concurrent.atomic.AtomicInteger;
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


    private static void writePathsToSvgFile(Path path, InputStream fontData, String inputName) throws XMLStreamException, IOException, TransformerException {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = impl.createDocument(svgNS, "svg", null);

        SVGDocument svgDocument = (SVGDocument) doc;
        int currentY = 50;

        addElementsToDocument(svgDocument, currentY, inputName, extractGlyphPaths(fontData));
        writeDocumentToFile(svgDocument, path);
    }

    private static void writeDocumentToFile(SVGDocument svgDocument, Path outputPath) throws IOException, TransformerException {
        DOMSource source = new DOMSource(svgDocument);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);) {
            StreamResult result = new StreamResult(bufferedWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
        }
    }

    private static void createDocumentWithAllGlyphs(Path fontFilesDirectory, Path outputFilePath, double scale) throws IOException, TransformerException {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = impl.createDocument(svgNS, "svg", null);

        SVGDocument svgDocument = (SVGDocument) doc;

        AtomicInteger currentY = new AtomicInteger(50);

        Files.list(fontFilesDirectory).forEach(path -> {
            try (InputStream inputStream = new FileInputStream(path.toFile())) {
                Collection<GlyphData> glyphDataCollection = extractGlyphPaths(inputStream).stream()
                        .map(glyphData -> PathProcessorKt.scaleGlyph(glyphData, scale))
                        .collect(Collectors.toList());
                addElementsToDocument(svgDocument, currentY.get(), path.getFileName().toString(), glyphDataCollection);

//                currentY.set(updatedYcoordinate);


            } catch (XMLStreamException | IOException e) {
                // TODO
                e.printStackTrace();
            }
        });

        SVGSVGElement rootElement = svgDocument.getRootElement();
        rootElement.setAttributeNS(null, "width", "10000");
        rootElement.setAttributeNS(null, "height", String.valueOf(currentY.get()));

        writeDocumentToFile(svgDocument, outputFilePath);
    }

    private static void writeGlyphWithBoundingBoxToFile(String glyphName, Path fontFile, Path outputFilePath) throws IOException, TransformerException, XMLStreamException {
        writeGlyphWithBoundingBoxToFile(glyphName, fontFile, outputFilePath, 1.0);
    }

    private static void writeGlyphWithBoundingBoxToFile(String glyphName, Path fontFile, Path outputFilePath, double scale) throws IOException, TransformerException, XMLStreamException {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = impl.createDocument(svgNS, "svg", null);

        SVGDocument svgDocument = (SVGDocument) doc;

        AtomicInteger currentY = new AtomicInteger(50);

        Collection<GlyphData> glyphDataCollection;
        try (InputStream inputStream = new FileInputStream(fontFile.toFile())) {
            glyphDataCollection = extractGlyphPaths(inputStream).stream()
                    .map(PathProcessorKt::invertYCoordinates)
                    .map(glyphData -> PathProcessorKt.scaleGlyph(glyphData, scale))
                    .collect(Collectors.toList());
        }

        glyphDataCollection.stream()
                .filter(glyphData -> glyphName.equals(glyphData.getName()))
                .findFirst()
                .ifPresent(glyphData -> {
                    try {
                        addElementsToDocument(svgDocument, currentY.get(), glyphData.getName(), Collections.singleton(glyphData));
                    } catch (IOException | XMLStreamException e) {
                        // TODO
                        e.printStackTrace();
                    }
                });


        SVGSVGElement rootElement = svgDocument.getRootElement();
        rootElement.setAttributeNS(null, "width", "10000");
        rootElement.setAttributeNS(null, "height", "5000");

        writeDocumentToFile(svgDocument, outputFilePath);
    }


    private static void addElementsToDocument(SVGDocument svgDocument, int currentY,
                                              String inputName, Collection<GlyphData> glyphDataCollection) throws XMLStreamException, IOException {
        Element path1 = svgDocument.createElementNS(SVG_NAMESPACE_URI, "text");
        path1.setAttribute("x", "0");
        path1.setAttribute("y", String.valueOf(currentY));
        path1.setAttribute("font-size", "55");

        SVGSVGElement rootElement = svgDocument.getRootElement();
        rootElement.appendChild(path1);
        path1.appendChild(svgDocument.createTextNode(inputName));

        int currentX = 500;
        for (GlyphData glyphData : glyphDataCollection) {

            LOGGER.info("Glyph name: {}", glyphData.getName());

            String pathString = transformToSquare2(glyphData.getFontPathElements(), currentX, currentY);
            BoundingBox boundingBox = PathProcessorKt.findBoundingBox(glyphData.getFontPathElements());

            addPath(svgDocument, rootElement, pathString);
            addRectangle(svgDocument, rootElement, (PathProcessorKt.offSetBoundingBox(boundingBox, currentX, currentY)));

            currentX += 200;
        }

    }



    private static Collection<GlyphData> extractGlyphPaths(InputStream fontData) throws XMLStreamException, IOException {
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        XMLStreamReader xmlEventReader = inputFactory.createXMLStreamReader(fontData, StandardCharsets.UTF_8.name());
        Collection<GlyphData> glyphDataCollection = new ArrayList<>();

        while (xmlEventReader.hasNext()) {
            xmlEventReader.next();

            if (!xmlEventReader.isStartElement()) {
                continue;
            }

            String localName = xmlEventReader.getName().getLocalPart();

            if (localName.equals(GLYPH)) {
                String glyphName = xmlEventReader.getAttributeValue("", "glyph-name");

                LOGGER.info("Processing glyph: " + glyphName);

                String pathAttribute = xmlEventReader.getAttributeValue("", "d");

                if (pathAttribute == null) {
                    LOGGER.info("No path defined for element: " + glyphName);
                    continue;
                }

                glyphDataCollection.add(new GlyphData(glyphName, parsePathData(pathAttribute)));
            }
        }

        return glyphDataCollection;
    }


    private static void addPath(Document document, Node node, String path) {
        Element path1 = document.createElementNS(SVG_NAMESPACE_URI, "path");
        path1.setAttribute("d", path);
        path1.setAttribute("stroke", "blue");
        path1.setAttribute("fill", "yellow");
        node.appendChild(path1);
    }

    private static void addRectangle(Document document, Node node, BoundingBox boundingBox) {
        Element path1 = document.createElementNS(SVG_NAMESPACE_URI, "rect");
        path1.setAttribute("x", String.valueOf(boundingBox.getXMin()));
        path1.setAttribute("y", String.valueOf(boundingBox.getYMin()));
        path1.setAttribute("width", String.valueOf(boundingBox.getXMax() - boundingBox.getXMin()));
        path1.setAttribute("height", String.valueOf(boundingBox.getYMax() - boundingBox.getYMin()));
        path1.setAttribute("stroke", "black");
        path1.setAttribute("fill", "none");
        node.appendChild(path1);
    }


    private static String transformToSquare2(List<FontPathElement> fontPathElements, int xOffset, int yOffset) throws IOException {
        String result = fontPathElements.stream()
                .map(fontPathElement -> {
                    // TODO Only handling M for now
                    if (fontPathElement.getCommand() == PathCommand.MOVE_TO_ABSOLUTE) {
                        return new FontPathElement(PathCommand.MOVE_TO_ABSOLUTE,
                                Lists.newArrayList(fontPathElement.getNumbers().get(0) + xOffset,
                                        fontPathElement.getNumbers().get(1) + yOffset));
                    }
                    return fontPathElement;
                })
                .map(fontPathElement -> fontPathElement.getCommand().getCommand() + " " + fontPathElement.getNumbers().stream()
                        .map(decimalFormat::format)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(" "));
        return result;
    }


    static List<FontPathElement> parsePathData(String pathData) throws IOException {
        LOGGER.info("Processing {}", pathData);

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
                    parsedElement = Iterators.getOnlyElement(handleGeneric(PathCommand.MOVE_TO_ABSOLUTE, charStream).entrySet().iterator());
                    c = parsedElement.getKey();
                    fontPathElements.add(parsedElement.getValue());
                    break;

                case 'm':
                    parsedElement = Iterators.getOnlyElement(handleGeneric(PathCommand.MOVE_TO_RELATIVE, charStream).entrySet().iterator());
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

                case 'Z':
                case 'z':
                    fontPathElements.add(new FontPathElement(PathCommand.CLOSE_PATH, Collections.emptyList()));
                    c = charStream.read();
                    break;

                case 's':
                    parsedElement = Iterators.getOnlyElement(handleGeneric(PathCommand.SMOOTH_CURVE_TO_RELATIVE, charStream).entrySet().iterator());
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
        return handleGeneric(PathCommand.LINE_TO_RELATIVE, charStream);
    }

    private static Map<Integer, FontPathElement> handleGeneric(PathCommand pathCommand, InputStream charStream) throws IOException {
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

        return Collections.singletonMap(c, new FontPathElement(pathCommand, numbers));
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

        return Collections.singletonMap(c, new FontPathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, numbers));

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
            number.clear();
        } while (!CharUtils.isAsciiAlpha((char) c));

        return Collections.singletonMap(c, new FontPathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, numbers));
    }


    private static Map<Integer, FontPathElement> handleLowerCaseC(InputStream charStream) throws IOException {
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

        return Collections.singletonMap(c, new FontPathElement(PathCommand.CURVE_TO_RELATIVE, numbers));
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

    public static List<CoordinatePair> extractGlyphFromFile(String glyphName, InputStream inputStream) throws XMLStreamException, IOException {
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

            if (localName.equals(GLYPH) && glyphName.equals(xmlEventReader.getAttributeValue("", "glyph-name"))) {
                return PathProcessorKt.processPath(parsePathData(xmlEventReader.getAttributeValue("", "d")));
            }
        }
        return Collections.emptyList();
    }


    public static void main(String args[]) throws Exception {
//        transformFont();

//        Path outputFilePath = Paths.get("output.xml");
//        try (InputStream inputStream = ReadFonts.class.getResourceAsStream("/gonville-r9313/lilyfonts/svg/gonvillepart1.svg")) {
//            writePathsToSvgFile(outputFilePath, inputStream, "gonvillepart1");
//        }

        Path outputFilePath = Paths.get("output2.xml");
        Path fontFilesDirectory = Paths.get("/home/student/workspace/EarTraining/noteGenerator/src/main/resources/gonville-r9313/lilyfonts/svg");
        createDocumentWithAllGlyphs(fontFilesDirectory, outputFilePath, 0.1);


//        Path outputFilePath = Paths.get("glyph_with_bounding_box.xml");
//        Path svgFontFile = Paths.get("/home/student/workspace/EarTraining/noteGenerator/src/main/resources/gonville-r9313/lilyfonts/svg/emmentaler-11.svg");
//        writeGlyphWithBoundingBoxToFile("clefs.G", svgFontFile, outputFilePath, 0.1);

    }


}
