package edu.jhu.jhg.finalreportreleasedatacheck;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JobHandler {

    private static final BlockingQueue<String> messages = new LinkedBlockingQueue<>();
    private ReportDirectory reportDirectory;
    private String exitStatus = "";

    public void generateJob(String directory, String columnsType,
                            boolean checkReports, boolean renameReports,
                            int expectedReports, String writeLocation)
            throws QueuedException {
        try {
            reportDirectory = new ReportDirectory(directory, columnsType,
                    checkReports, renameReports, expectedReports, writeLocation);
        } catch (Exception ex) {
            throw new QueuedException(ex);
        }
    }

    public String start() throws QueuedException {
        try {
            exitStatus = reportDirectory.runOperations();
        } catch (Exception ex) {
            throw new QueuedException(ex);
        }
        return exitStatus;
    }

    public String getCompletionStatus() {
        return exitStatus;
    }

    public static class QueuedException extends Exception {

        public QueuedException(Exception ex) {
            String exceptionText = ex.toString();
            putExceptionMessage(exceptionText, ex);
        }
    }

    private static synchronized void putExceptionMessage(String exceptionText, Exception ex) {
        messages.add(exceptionText + "\n\n" + prettyPrintStackTrace(ex));
    }

    private static String prettyPrintStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    /* Concurrency note: this method is accessed by both the JavaFX thread from GuiController,
     * and the backend thread it generates. Designated 'synchronized' for thread safety
     * returns null if queue is empty
     */
    public synchronized Optional<String> getExceptionMessage() {
        return Optional.ofNullable(messages.poll());
    }

    @Override
    public String toString() {
        return "JobHandler{" +
                "reportDirectory=" + reportDirectory +
                '}';
    }
}
