package com.kjipo;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
import org.xml.sax.SAXException;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Created by student on 9/13/14.
 */
public class Visualizer extends Application {

    protected JSVGCanvas svgCanvas = new JSVGCanvas();


    @Override
    public void start(Stage primaryStage) throws Exception {
        final SwingNode swingNode = new SwingNode();
        swingNode.setContent(getSvgPanel());
        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);

        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ButtonHtmlDemo Embedded in JavaFX");
        primaryStage.show();


    }

    private JPanel getSvgPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 5));

        panel.add(svgCanvas, BorderLayout.CENTER);

//SwingUtilities.invokeLater(() -> {
//    svgCanvas.setDocument(getTestSvgDocument().get());
//});


return panel;

    }


    private Optional<Document> getTestSvgDocument() {
//        SVGDOMImplementation svgDom = new SVGDOMImplementation();
//        Document doc = svgDom.createDocument("http://www.w3.org/2000/svg", "svg", null);


        try (
//            InputStream inputStream = getClass().getResourceAsStream("/svg/emmentaler-11.svg");
            InputStream inputStream = getClass().getResourceAsStream("/test/TestText2.svg");
        )
        {
//            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
//            return Optional.of(documentBuilder.parse(inputStream));

            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
//            String uri = "http://www.example.org/diagram.svg";
//            Document doc = f.createDocument("/svg/emmentaler-11.svg", inputStream);


            DOMImplementation domImplementation = SVGDOMImplementation.getDOMImplementation();
            SVGDocument doc = f.createSVGDocument("/svg/emmentaler-11.svg", inputStream);





            System.out.println("Number of glyphs: " + ((SVGDocument) doc).getElementsByTagName("glyph").getLength());

            return Optional.of(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();



    }



}
