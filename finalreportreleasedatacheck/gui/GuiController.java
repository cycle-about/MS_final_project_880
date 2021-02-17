package edu.jhu.jhg.finalreportreleasedatacheck.gui;

import edu.jhu.jhg.finalreportreleasedatacheck.JobHandler;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// author lvail1, 8/2020
public class GuiController {

    @FXML private GridPane gridPane;
    @FXML private RadioButton releaseReport;
    @FXML private RadioButton verifyId;
    @FXML private CheckBox checkReports;
    @FXML private CheckBox renameReports;
    @FXML private TextField expectedReports;
    @FXML private TextField inputDirectory;
    @FXML private TextField outputDirectory;
    @FXML private Button browseInput;
    @FXML private Button browseOutput;
    @FXML private Button submitButton;
    private final Alert exceptionDialogue = new Alert(Alert.AlertType.ERROR);
    private final Alert completionDialogue = new Alert(Alert.AlertType.CONFIRMATION);
    private final Timer exceptionCheckTimer = new Timer();
    private final ToggleGroup reportType = new ToggleGroup();
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor(); // thread for backend work
    private Stage stage;
    private final JobHandler jobHandler = new JobHandler();
    private String exceptionMessages = "";

    //////// initial gui setup ////////
    public void initialize() {
        startBackendExceptionChecker();
        releaseReport.setToggleGroup(reportType); // the two report types are mutually exclusive
        verifyId.setToggleGroup(reportType);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // check for exception message from backend every 1 second; display if found
    private synchronized void startBackendExceptionChecker() {
        exceptionCheckTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Optional<String> exceptionMessage = jobHandler.getExceptionMessage();
                exceptionMessage.ifPresent(s -> showException(s));
            }
        }, 0, 1000); // after delay of 0, run every 1 second
    }

    //////// behavior upon user interaction ////////
    @FXML private void openInput() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("R:\\active"));
        inputDirectory.setText(directoryChooser.showDialog(stage).toString());
    }

    @FXML private void openOutput() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("R:\\active"));
        outputDirectory.setText(directoryChooser.showDialog(stage).toString());
    }

    @FXML private void onSubmitPressed() {
        // printEnteredValues();
        runOperations();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) { System.exit(0); }
        });
    }

    private void printEnteredValues() {
        RadioButton selectedRadioButton = (RadioButton) reportType.getSelectedToggle();
        String enteredValues = "Values entered into gui:" +
                "\nOutput Dir: " + outputDirectory.getText() +
                "\nReport Type: " + selectedRadioButton.getText() +
                "\nExpected Reports: " + expectedReports.getText() +
                "\nReports Dir: " + inputDirectory.getText() +
                "\nCheck Reports: " + checkReports.isSelected() +
                "\nRename Reports: " + renameReports.isSelected();
        showCompletion(enteredValues);
    }

    private void runOperations() {
        backgroundExecutor.submit(() -> {
            try {
                RadioButton selectedRadioButton = (RadioButton) reportType.getSelectedToggle();
                jobHandler.generateJob(inputDirectory.getText(),
                        selectedRadioButton.getText(),
                        checkReports.isSelected(),
                        renameReports.isSelected(), Integer.parseInt(expectedReports.getText()),
                        outputDirectory.getText());

                jobHandler.start();
                showCompletion(jobHandler.getCompletionStatus());
            } catch (JobHandler.QueuedException ex) {
                showException(ex.getMessage());
            }
        });
    }

    private void showCompletion(String exitStatus) {
        Platform.runLater(() -> {
            completionDialogue.setTitle("Release Final Report Operations Completed");
            TextArea completed = new TextArea(exitStatus);
            completed.setEditable(false);
            completed.setMaxWidth(Double.MAX_VALUE);
            completed.setMaxHeight(Double.MAX_VALUE);

            GridPane.setVgrow(completed, Priority.ALWAYS);
            GridPane.setHgrow(completed, Priority.ALWAYS);
            GridPane completionPane = new GridPane();
            completionPane.setMaxWidth(Double.MAX_VALUE);
            completionPane.add(completed, 0, 0);
            completionDialogue.getDialogPane().setContent(completionPane);
            completionDialogue.show();
        });
    }

    // appends exception messages in alert window if multiple are received
    private void showException(String exMessage) {
        Platform.runLater(() -> {
            exceptionMessages += exMessage + "\n"; // append new message to prior ones
            exceptionDialogue.setTitle("Error in Final Report Release Data Check");
            exceptionDialogue.setHeaderText("Please copy all text below and contact the software team");
            TextArea exceptionText = new TextArea(exceptionMessages);
            exceptionText.setEditable(false);
            exceptionText.setMaxWidth(Double.MAX_VALUE);
            exceptionText.setMaxHeight(Double.MAX_VALUE);

            GridPane.setVgrow(exceptionText, Priority.ALWAYS);
            GridPane.setHgrow(exceptionText, Priority.ALWAYS);
            GridPane exceptionPane = new GridPane();
            exceptionPane.setMaxWidth(Double.MAX_VALUE);
            exceptionPane.add(exceptionText, 0, 0);
            exceptionDialogue.getDialogPane().setContent(exceptionPane);
            exceptionDialogue.show();
        });
    }

    private String getExceptions() {
        return exceptionMessages;
    }
}