package edu.jhu.jhg.finalreportreleasedatacheck.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

/*
 * This class must be run with a version of java that contains JavaFX.
 * As of 1/2020, it is at Q:\java\jdk8u192-b12-JFX\bin\java.exe,
 * so execute via "Q:\java\jdk8u192-b12-JFX\bin\java.exe -jar finalreportplink.jar"
 *
 * JavaFX is included as an SDK in IntelliJ, so it can run from IntelliJ with any java version,
 * but it DOES NOT get added to the classpath, and will build successfully,
 * even though the jar will show 'Main not found' when run
 */
public class GuiMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiMain.class.getResource("GuiView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 700, 350);
        stage.setScene(scene);
        stage.setTitle("Final Report Release Data Check");
        GuiController controller = fxmlLoader.getController();
        controller.setStage(stage);
        stage.show();
    }

    // Phoenix API uses c3p0 logging, which does not readily
    // print only to file as does logback, which is used for rest of application
    // silence its printouts, connection errors will be caught elsewhere
    public static void main(String[] args) {
        Properties properties = new Properties(System.getProperties());
        properties.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        properties.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");
        System.setProperties(properties);
        Application.launch(GuiMain.class, args);
    }
}