package com.kjipo;


import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;


public class TestWebView extends Application {
    private Scene scene;

    @Override
    public void start(Stage stage) {
        // create the scene
        stage.setTitle("Web View");
        scene = new Scene(new Browser(), 750, 500, Color.web("#666970"));
        stage.setScene(scene);
//        scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
        stage.show();
    }

    public static void main(String[] args) {
//        URL.setURLStreamHandlerFactory(protocol -> {
//
//            System.out.println("Test20: " +protocol);
//
//            if (protocol.equals("classpath")) {
//                return new Handler();
//            } else {
//                return null;
//            }
//        });

        launch(args);

    }
}

class Handler extends URLStreamHandler {
    private final ClassLoader classLoader;

    public Handler() {
        this.classLoader = getClass().getClassLoader();
    }

    public Handler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {

        System.out.println("Test10: " +u +"\nPath: " +u.getPath());


//        String path = getURL().getPath().startsWith("/") ? getURL().getPath().substring(1) : getURL().getPath();
//        URL resourceUrl = classLoader.getResource(path);

        URL resourceUrl = classLoader.getResource(u.getPath().startsWith("/") ? u.getPath().substring(1) : u.getPath());
        if(resourceUrl == null)
            throw new IOException("Resource not found: " + u);

        System.out.println("Found resource: " +u.getPath());

        return resourceUrl.openConnection();
    }
}

class Browser extends Region {

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    public Browser() {
        //apply the styles
        getStyleClass().add("browser");
        // load the web page
//        webEngine.load("http://www.oracle.com/products/index.html");
        webEngine.setJavaScriptEnabled(true);


        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                        if (newState == Worker.State.SUCCEEDED) {
//                            stage.setTitle(webEngine.getLocation());

                            JSObject jsobj = (JSObject) webEngine.executeScript("window");
                            jsobj.setMember("java", new Bridge());
                            webEngine.executeScript("loadData()");

                        }
                    }
                });

//        try(InputStream testPage = getClass().getResourceAsStream("/raphael.js")) {
//            webEngine.loadContent(CharStreams.toString(new InputStreamReader(testPage, Charsets.UTF_8)), "text/javascript");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try(InputStream testPage = getClass().getResourceAsStream("/index3.html")) {
////            webEngine.loadContent(CharStreams.toString(new InputStreamReader(testPage, Charsets.UTF_8)));
//
////            webEngine.load("classpath://index3.html");
//
//            webEngine.load(getClass().getResource("/index3.html").toExternalForm());
//
//                    //add the web view to the scene
//                    getChildren().add(browser);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }





    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }

    @Override protected double computePrefWidth(double height) {
        return 750;
    }

    @Override protected double computePrefHeight(double width) {
        return 500;
    }









}