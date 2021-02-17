package edu.jhu.jhg.finalreportreleasedatacheck.checkFiles;

import edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory;

import java.io.IOException;

import static edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory.alertFound;

/*
 * Evaluates whether the expected number of files entered by the user
 * matches the number in the directory entered
 * Skips any files that are not csv, or start with "RENAME_LOG"
 */
public class FileNumberCheck {

    private final int reportsCount;
    private final int expectedReports;

    public FileNumberCheck(int reportsCount, int expectedReports) {
        this.reportsCount = reportsCount;    // Mutation 16
        this.expectedReports = expectedReports;
    }

    public void checkFileNumbers() throws IOException {
        if (reportsCount != expectedReports) {    // Mutation 1, Mutation 27
            alertFound = true;
            ReportDirectory.AlertWriter.writeAlert("Mismatch between the found and expected number of release final reports. Expected " + expectedReports + ", and found " + reportsCount);
        }
    }
}
