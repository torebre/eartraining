package com.kjipo.font;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class ReadFonts {
    private static final String GLYPH = "glyph";


    public static final ThreadLocal<DecimalFormat> decimalFormatThreadLocal = ThreadLocal.withInitial(() -> {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(3);
        decimalFormat.setGroupingUsed(false);
        return decimalFormat;
    });

    private static final ThreadLocal<DocumentBuilderFactory> documentFactoryThreadLocal = ThreadLocal.withInitial(DocumentBuilderFactory::newInstance);


    private static final Logger LOGGER = LoggerFactory.getLogger(ReadFonts.class);


    private static void writePathsToSvgFile(Path path, InputStream fontData, String inputName) throws XMLStreamException, IOException, TransformerException, ParserConfigurationException {
        Document doc = documentFactoryThreadLocal.get().newDocumentBuilder().newDocument();

//        Document doc = impl.createDocument(svgNS, "svg", null);
        int currentY = 50;

        addElementsToDocument(doc, currentY, inputName, extractGlyphPaths(fontData));
        SvgTools.writeDocumentToFile(doc, path);
    }


    private static void createDocumentWithAllGlyphs(Path fontFilesDirectory, Path outputFilePath, double scale) throws IOException, TransformerException, ParserConfigurationException {
        int yIncrement = 200;
        AtomicInteger currentY = new AtomicInteger(50);
        Document doc = createNewSvgDocument();

        Files.list(fontFilesDirectory).forEach(path -> {
            try (InputStream inputStream = new FileInputStream(path.toFile())) {
                Collection<GlyphData> glyphDataCollection = extractGlyphPaths(inputStream).stream()
                        .map(glyphData -> PathProcessorKt.scaleGlyph(glyphData, scale))
                        .map(PathProcessorKt::invertYCoordinates)
                        .collect(Collectors.toList());
                addElementsToDocument(doc, currentY.getAndAdd(yIncrement), path.getFileName().toString(), glyphDataCollection);

            } catch (XMLStreamException | IOException e) {
                LOGGER.error("Exception when processing path {}", path, e);
            }
        });

        Element rootElement = doc.getDocumentElement();
        rootElement.setAttributeNS(null, "width", "10000");
        rootElement.setAttributeNS(null, "height", String.valueOf(currentY.get()));

        SvgTools.writeDocumentToFile(doc, outputFilePath);
    }

    private static void writeGlyphWithBoundingBoxToFile(String glyphName, Path fontFile, Path outputFilePath) throws IOException, TransformerException, XMLStreamException, ParserConfigurationException {
        writeGlyphWithBoundingBoxToFile(glyphName, fontFile, outputFilePath, 1.0);
    }

    private static void writeGlyphWithBoundingBoxToFile(String glyphName, Path fontFile, Path outputFilePath, double scale) throws IOException, TransformerException, XMLStreamException, ParserConfigurationException {
        Document doc = createNewSvgDocument();
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
                        addElementsToDocument(doc, currentY.get(), glyphData.getName(), Collections.singleton(glyphData));
                    } catch (IOException | XMLStreamException e) {
                        // TODO
                        e.printStackTrace();
                    }
                });


        Element rootElement = doc.getDocumentElement();
        rootElement.setAttributeNS(null, "width", "10000");
        rootElement.setAttributeNS(null, "height", "5000");

        SvgTools.writeDocumentToFile(doc, outputFilePath);
    }


    private static void addElementsToDocument(
            Document svgDocument,
            int currentY,
            String inputName,
            Collection<GlyphData> glyphDataCollection) throws XMLStreamException, IOException {
        Element path1 = svgDocument.createElementNS(SvgTools.SVG_NAMESPACE_URI, "text");
        path1.setAttribute("x", "0");
        path1.setAttribute("y", String.valueOf(currentY));
        path1.setAttribute("font-size", "55");

        Element rootElement = svgDocument.getDocumentElement();
        rootElement.appendChild(path1);
        path1.appendChild(svgDocument.createTextNode(inputName));

        int currentX = 500;
        for (GlyphData glyphData : glyphDataCollection) {

            LOGGER.info("Glyph name: {}", glyphData.getName());

            try {

                String pathString = transformToSquare2(glyphData.getPathElements(), currentX, currentY);
                BoundingBox boundingBox = PathProcessorKt.findBoundingBox(glyphData.getPathElements());

                SvgTools.addPath(rootElement, pathString, 1);
                addRectangle(svgDocument, rootElement, (PathProcessorKt.offSetBoundingBox(boundingBox, currentX, currentY)));
            } catch (RuntimeException e) {
                LOGGER.error("Exception when processing glyph {}", glyphData.getName(), e);
            }

            currentX += 200;
        }

    }


    public static Collection<GlyphData> extractGlyphPaths(InputStream fontData) throws XMLStreamException, IOException {
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

                List<PathElement> pathElements = FontProcessingUtilities.parsePathData(pathAttribute);
                glyphDataCollection.add(new GlyphData(glyphName, pathElements, PathProcessorKt.findBoundingBox(pathElements)));
            }
        }

        return glyphDataCollection;
    }


    private static void addRectangle(Document document, Node node, BoundingBox boundingBox) {
        Element path1 = document.createElementNS(SvgTools.SVG_NAMESPACE_URI, "rect");
        path1.setAttribute("x", String.valueOf(boundingBox.getXMin()));
        path1.setAttribute("y", String.valueOf(boundingBox.getYMin()));
        path1.setAttribute("width", String.valueOf(boundingBox.getXMax() - boundingBox.getXMin()));
        path1.setAttribute("height", String.valueOf(boundingBox.getYMax() - boundingBox.getYMin()));
        path1.setAttribute("stroke", "black");
        path1.setAttribute("fill", "none");
        node.appendChild(path1);
    }


    private static String transformToSquare2(List<PathElement> pathElements, int xOffset, int yOffset) throws IOException {
        return pathElements.stream()
                .map(pathElement -> {
                    // TODO Only handling M for now
                    if (pathElement.getCommand() == PathCommand.MOVE_TO_ABSOLUTE) {
                        return new PathElement(PathCommand.MOVE_TO_ABSOLUTE,
                                Arrays.asList(pathElement.getNumbers().get(0) + xOffset,
                                        pathElement.getNumbers().get(1) + yOffset));
                    }
                    return pathElement;
                })
                .map(pathElement -> pathElement.getCommand().getCommand() + " " + pathElement.getNumbers().stream()
                        .map(decimalFormatThreadLocal.get()::format)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(" "));
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
                return PathProcessorKt.processPath(FontProcessingUtilities.parsePathData(xmlEventReader.getAttributeValue("", "d")));
            }
        }
        return Collections.emptyList();
    }


    private static void extractGlyphsAsGlyphDataAndSave(List<String> glyphNames, InputStream inputXmlData, OutputStreamWriter outputStreamWriter) throws IOException, XMLStreamException {
        Collection<GlyphData> glyphDataCollection = extractGlyphPaths(inputXmlData);

        List<GlyphData> glyphsToSave = glyphDataCollection.stream()
                .filter(glyphData -> glyphNames.contains(glyphData.getName()))
                .collect(Collectors.toList());

        Gson gson = new Gson();
        Type typeToken = new TypeToken<Collection<GlyphData>>() {
        }.getType();
        gson.toJson(glyphsToSave, typeToken, outputStreamWriter);
    }

    private static Document createNewSvgDocument() throws ParserConfigurationException {
        DocumentBuilder documentBuilder = documentFactoryThreadLocal.get().newDocumentBuilder();
        return documentBuilder.getDOMImplementation().createDocument(SvgTools.SVG_NAMESPACE_URI, "svg", null);
    }


    public static void main(String args[]) throws Exception {
//        transformFont();

//        Path outputFilePath = Paths.get("output.xml");
//        try (InputStream inputStream = ReadFonts.class.getResourceAsStream("/gonville-r9313/lilyfonts/svg/gonvillepart1.svg")) {
//            writePathsToSvgFile(outputFilePath, inputStream, "gonvillepart1");
//        }

//        Path outputFilePath = Paths.get("output2.xml");
//        Path fontFilesDirectory = Paths.get("/home/student/workspace/EarTraining/noteGenerator/src/main/resources/gonville-r9313/lilyfonts/svg");
//        createDocumentWithAllGlyphs(fontFilesDirectory, outputFilePath, 0.1);


//        Path outputFilePath = Paths.get("glyph_with_bounding_box.xml");
//        Path svgFontFile = Paths.get("/home/student/workspace/EarTraining/noteGenerator/src/main/resources/gonville-r9313/lilyfonts/svg/emmentaler-11.svg");
//        writeGlyphWithBoundingBoxToFile("clefs.G", svgFontFile, outputFilePath, 0.1);


        Path outputFilePath = Paths.get("output3.json");
        Path svgFontFile = Paths.get("/home/student/workspace/EarTraining/noteGenerator/src/main/resources/gonville-r9313/lilyfonts/svg/emmentaler-11.svg");


        List<String> glyphNames = Arrays.asList("noteheads.s2", "noteheads.s1", "noteheads.s0");

        try (InputStream inputStream = Files.newInputStream(svgFontFile);
             OutputStream outputStream = Files.newOutputStream(outputFilePath);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
            extractGlyphsAsGlyphDataAndSave(glyphNames, inputStream, outputStreamWriter);
        }


    }


}
