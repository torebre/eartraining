package com.kjipo.font;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class SvgTools {
    public static final String SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";

    private SvgTools() {

    }


    public static void writeDocumentToFile(SVGDocument svgDocument, Path outputPath) throws IOException, TransformerException {
        DOMSource source = new DOMSource(svgDocument);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);) {
            StreamResult result = new StreamResult(bufferedWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
        }
    }


    public static void addPath(Node node, String path, int strokeWidth) {
        Element path1 = node.getOwnerDocument().createElementNS(SVG_NAMESPACE_URI, "path");
        path1.setAttribute("d", path);
        path1.setAttribute("stroke", "blue");
        path1.setAttribute("fill", "yellow");

        path1.setAttribute("stroke-width", String.valueOf(strokeWidth));

        node.appendChild(path1);
    }

    public static void addLine(int xStart, int yStart, int xEnd, int yEnd, Node node, int strokeWidth) {
        Element path1 = node.getOwnerDocument().createElementNS(SVG_NAMESPACE_URI, "line");
        path1.setAttribute("x1", String.valueOf(xStart));
        path1.setAttribute("y1", String.valueOf(yStart));
        path1.setAttribute("x2", String.valueOf(xEnd));
        path1.setAttribute("y2", String.valueOf(yEnd));


        path1.setAttribute("stroke-width", String.valueOf(strokeWidth));
        path1.setAttribute("stroke", "black");


        node.appendChild(path1);
    }

}



