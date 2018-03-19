package com.kjipo.font


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamException
import javax.xml.transform.TransformerException
import kotlin.streams.toList


object ReadFonts {
    private val GLYPH = "glyph"

    val decimalFormatThreadLocal = object: ThreadLocal<DecimalFormat>() {

        override fun initialValue(): DecimalFormat {
            val decimalFormat = DecimalFormat()
            decimalFormat.maximumFractionDigits = 3
            decimalFormat.isGroupingUsed = false

            return decimalFormat
        }
    }

    private val documentFactoryThreadLocal = ThreadLocal<DocumentBuilderFactory>()

    private val LOGGER = LoggerFactory.getLogger(ReadFonts::class.java)

    init {
        documentFactoryThreadLocal.set(DocumentBuilderFactory.newInstance())
    }


    @Throws(XMLStreamException::class, IOException::class, TransformerException::class, ParserConfigurationException::class)
    private fun writePathsToSvgFile(path: Path, fontData: InputStream, inputName: String) {
        val doc = documentFactoryThreadLocal.get().newDocumentBuilder().newDocument()

        //        Document doc = impl.createDocument(svgNS, "svg", null);
        val currentY = 50

        addElementsToDocument(doc, currentY, inputName, extractGlyphPaths(fontData))
        SvgTools.writeDocumentToFile(doc, path)
    }


    @Throws(IOException::class, TransformerException::class, ParserConfigurationException::class)
    private fun createDocumentWithAllGlyphs(fontFilesDirectory: Path, outputFilePath: Path, scale: Double) {
        val yIncrement = 200
        val currentY = AtomicInteger(50)
        val doc = createNewSvgDocument()

        Files.list(fontFilesDirectory).forEach { path ->
            LOGGER.info("Processing: {}", path)
            try {
                FileInputStream(path.toFile()).use { inputStream ->
                    val glyphDataCollection = extractGlyphPaths(inputStream).stream()
                            .map { glyphData ->
                                LOGGER.info("Path: {}. Processing glyph: {}", path, glyphData.name)
                                scaleGlyph(glyphData, scale)
                            }
                            .map { invertYCoordinates(it) }
                            .toList()
                    addElementsToDocument(doc, currentY.getAndAdd(yIncrement), path.fileName.toString(), glyphDataCollection)

                }
            } catch (e: XMLStreamException) {
                LOGGER.error("Exception when processing path {}", path, e)
            } catch (e: IOException) {
                LOGGER.error("Exception when processing path {}", path, e)
            }
        }

        val rootElement = doc.documentElement
        rootElement.setAttributeNS(null, "width", "10000")
        rootElement.setAttributeNS(null, "height", currentY.get().toString())

        SvgTools.writeDocumentToFile(doc, outputFilePath)
    }

    @Throws(IOException::class, TransformerException::class, XMLStreamException::class, ParserConfigurationException::class)
    private fun writeGlyphWithBoundingBoxToFile(glyphName: String, fontFile: Path, outputFilePath: Path, scale: Double = 1.0) {
        val doc = createNewSvgDocument()
        val currentY = AtomicInteger(50)

        val glyphDataCollection = FileInputStream(fontFile.toFile()).use { inputStream ->
                    extractGlyphPaths(inputStream).stream()
                            .map { invertYCoordinates(it) }
                            .map { glyphData -> scaleGlyph(glyphData, scale) }
                            .toList()
                }

        glyphDataCollection.stream()
                .filter { glyphData -> glyphName == glyphData.name }
                .findFirst()
                .ifPresent { glyphData ->
                    try {
                        addElementsToDocument(doc, currentY.get(), glyphData.name, setOf(glyphData))
                    } catch (e: IOException) {
                        // TODO
                        e.printStackTrace()
                    } catch (e: XMLStreamException) {
                        e.printStackTrace()
                    }
                }


        val rootElement = doc.documentElement
        rootElement.setAttributeNS(null, "width", "10000")
        rootElement.setAttributeNS(null, "height", "5000")

        SvgTools.writeDocumentToFile(doc, outputFilePath)
    }


    @Throws(XMLStreamException::class, IOException::class)
    private fun addElementsToDocument(
            svgDocument: Document,
            currentY: Int,
            inputName: String,
            glyphDataCollection: Collection<GlyphData>) {
        val path1 = svgDocument.createElementNS(SvgTools.SVG_NAMESPACE_URI, "text")
        path1.setAttribute("x", "0")
        path1.setAttribute("y", currentY.toString())
        path1.setAttribute("font-size", "55")

        val rootElement = svgDocument.documentElement
        rootElement.appendChild(path1)
        path1.appendChild(svgDocument.createTextNode(inputName))

        var currentX = 500
        for (glyphData in glyphDataCollection) {

            LOGGER.info("Glyph name: {}", glyphData.name)

            try {

                val pathString = transformToSquare2(glyphData.pathElements, currentX, currentY)
                val boundingBox = findBoundingBox(glyphData.pathElements)

                SvgTools.addPath(rootElement, pathString, 1)
                addRectangle(svgDocument, rootElement, offSetBoundingBox(boundingBox, currentX, currentY))
            } catch (e: RuntimeException) {
                LOGGER.error("Exception when processing glyph {}", glyphData.name, e)
            }

            currentX += 200
        }

    }


    @Throws(XMLStreamException::class)
    private fun extractGlyphPaths(fontData: InputStream): Collection<GlyphData> {
        val inputFactory = XMLInputFactory.newFactory()
        val xmlEventReader = inputFactory.createXMLStreamReader(fontData, StandardCharsets.UTF_8.name())
        val glyphDataCollection = ArrayList<GlyphData>()

        while (xmlEventReader.hasNext()) {
            xmlEventReader.next()

            if (!xmlEventReader.isStartElement) {
                continue
            }

            val localName = xmlEventReader.name.localPart

            if (localName == GLYPH) {
                val glyphName = xmlEventReader.getAttributeValue("", "glyph-name")

                if (LOGGER.isDebugEnabled) {
                    LOGGER.debug("Processing glyph: " + glyphName)
                }

                val pathAttribute = xmlEventReader.getAttributeValue("", "d")

                if (pathAttribute == null) {
                    LOGGER.info("No path defined for element: " + glyphName)
                    continue
                }

                try {
                    val pathElements = FontProcessingUtilities.parsePathData(pathAttribute)
                    glyphDataCollection.add(GlyphData(glyphName, pathElements, findBoundingBox(pathElements)))
                } catch (e: RuntimeException) {
                    LOGGER.error("Exception when processing glyph: {}", glyphName, e)
                } catch (e: IOException) {
                    LOGGER.error("Exception when processing glyph: {}", glyphName, e)
                }

            }
        }

        return glyphDataCollection
    }


    private fun addRectangle(document: Document, node: Node, boundingBox: BoundingBox) {
        val path1 = document.createElementNS(SvgTools.SVG_NAMESPACE_URI, "rect")
        path1.setAttribute("x", boundingBox.xMin.toString())
        path1.setAttribute("y", boundingBox.yMin.toString())
        path1.setAttribute("width", (boundingBox.xMax - boundingBox.xMin).toString())
        path1.setAttribute("height", (boundingBox.yMax - boundingBox.yMin).toString())
        path1.setAttribute("stroke", "black")
        path1.setAttribute("fill", "none")
        node.appendChild(path1)
    }


    @Throws(IOException::class)
    private fun transformToSquare2(pathElements: List<PathElement>, xOffset: Int, yOffset: Int): String {
        return pathElements.stream()
                .map { pathElement ->
                    // TODO Only handling M for now
                    if (pathElement.command == PathCommand.MOVE_TO_ABSOLUTE) {
                        PathElement(PathCommand.MOVE_TO_ABSOLUTE,
                                Arrays.asList(pathElement.numbers[0] + xOffset,
                                        pathElement.numbers[1] + yOffset))
                    } else {
                        pathElement
                    }

                }
                .map {
                    it.command.toString() + " " + it.numbers.stream()
                            .map { decimalFormatThreadLocal.get().format(it) }
                            .collect(Collectors.joining(" "))
                }
                .collect(Collectors.joining(" "))
    }


    @Throws(XMLStreamException::class, IOException::class)
    fun extractGlyphFromFile(glyphName: String, inputStream: InputStream): List<CoordinatePair> {
        val inputFactory = XMLInputFactory.newFactory()
        val xmlEventReader = inputFactory.createXMLStreamReader(inputStream, StandardCharsets.UTF_8.name())
        val namePathMapping = HashMap<String, String>()
        val fontBoundingBox: DoubleArray? = null

        while (xmlEventReader.hasNext()) {
            xmlEventReader.next()

            if (!xmlEventReader.isStartElement) {
                continue
            }

            val localName = xmlEventReader.name.localPart

            if (localName == GLYPH && glyphName == xmlEventReader.getAttributeValue("", "glyph-name")) {
                return processPath(FontProcessingUtilities.parsePathData(xmlEventReader.getAttributeValue("", "d")))
            }
        }
        return emptyList()
    }


    @Throws(IOException::class, XMLStreamException::class)
    private fun extractGlyphsAsGlyphDataAndSave(glyphNames: List<String>, inputXmlData: InputStream, outputStreamWriter: OutputStreamWriter) {
        val glyphDataCollection = extractGlyphPaths(inputXmlData)

        val glyphsToSave = glyphDataCollection.stream()
                .filter { glyphData -> glyphNames.contains(glyphData.name) }
                .toList()

        writeGlyphsToOutputStream(glyphsToSave, outputStreamWriter)
    }

    private fun writeGlyphsToOutputStream(glyphsToSave: Collection<GlyphData>, outputStreamWriter: OutputStreamWriter) {
        val gson = Gson()
        val typeToken = object : TypeToken<Collection<GlyphData>>() {

        }.type
        gson.toJson(glyphsToSave, typeToken, outputStreamWriter)
    }

    @Throws(ParserConfigurationException::class)
    private fun createNewSvgDocument(): Document {
        val documentBuilder = documentFactoryThreadLocal.get().newDocumentBuilder()
        return documentBuilder.domImplementation.createDocument(SvgTools.SVG_NAMESPACE_URI, "svg", null)
    }


    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        //        transformFont();

        //        Path outputFilePath = Paths.get("output.xml");
        //        try (InputStream inputStream = ReadFonts.class.getResourceAsStream("/gonville-r9313/lilyfonts/svg/gonvillepart1.svg")) {
        //            writePathsToSvgFile(outputFilePath, inputStream, "gonvillepart1");
        //        }

        //        // Creates a document with all glyphs
        //        Path outputFilePath = Paths.get("output2.xml");
        //        Path fontFilesDirectory = Paths.get("/home/student/Documents/gonville-r9313/lilyfonts/svg/");
        //        createDocumentWithAllGlyphs(fontFilesDirectory, outputFilePath, 0.1);


        val outputAlphabetFilePath = Paths.get("/home/student/workspace/EarTraining/noteVisualiser/src/main/resources/alpha.json")
        val svgFontFile = Paths.get("/home/student/Documents/gonville-r9313/lilyfonts/svg/gonvillealpha11.svg")
        Files.newInputStream(svgFontFile).use { inputStream -> Files.newOutputStream(outputAlphabetFilePath).use { outputStream -> OutputStreamWriter(outputStream).use { outputStreamWriter -> writeGlyphsToOutputStream(extractGlyphPaths(inputStream), outputStreamWriter) } } }


        //        Path outputFilePath = Paths.get("glyph_with_bounding_box.xml");
        //        Path svgFontFile = Paths.get("/home/student/workspace/EarTraining/noteGenerator/src/main/resources/gonville-r9313/lilyfonts/svg/emmentaler-11.svg");
        //        writeGlyphWithBoundingBoxToFile("clefs.G", svgFontFile, outputFilePath, 0.1);


        //        Path outputFilePath = Paths.get("/home/student/workspace/EarTraining/noteVisualiser/src/main/resources/glyphs.json");
        //        Path svgFontFile = Paths.get("/home/student/Documents/gonville-r9313/lilyfonts/svg/emmentaler-11.svg");
        //
        //
        //        List<String> glyphNames = Arrays.asList("clefs.G", "noteheads.s2", "noteheads.s1", "noteheads.s0");
        //
        //        try (InputStream inputStream = Files.newInputStream(svgFontFile);
        //             OutputStream outputStream = Files.newOutputStream(outputFilePath);
        //             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
        //            extractGlyphsAsGlyphDataAndSave(glyphNames, inputStream, outputStreamWriter);
        //        }


    }


}
