package edu.jhu.jhg.finalreportreleasedatacheck.checkFiles;

import edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import static edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory.alertFound;

/*
 * BAF and logR must both be NaN if either is NaN
 * show alert with the filename and line number if only one of them is NaN
 */
public class FileStatsCheck {

    private final List<File> pretestReports;
    private final int nonSnpRows;

    public FileStatsCheck(List<File> pretestReports, int headerRows) {
        this.pretestReports = pretestReports;
        this.nonSnpRows = ++headerRows;    // also skip the row with column names
    }

    public void checkReportStats() throws Exception {
        for (File report : pretestReports) {
            getSnpLinesInReport(report);
        }
    }

    private void getSnpLinesInReport(File report) throws Exception {
        int lineNum = 1;
        try (BufferedReader reader = new BufferedReader(new FileReader(report))) {
            String line = reader.readLine();
            while (line != null) {
                if (lineNum < nonSnpRows) {     // skip lines in header section
                    line = reader.readLine();
                    lineNum++;    // Mutation 19
                    continue;
                }
                boolean statsAllowed = compareBafLogrValues(line);
                if (!statsAllowed) {
                    ReportDirectory.AlertWriter.writeAlert("Mismatch in NaN values of B Allele Freq and Log R Ratio: line " + lineNum + " of report " + report);
                }
                line = reader.readLine();
                lineNum++;
            }
        }
    }

    // B Allele Freq is second to last of the line's comma separated values
    // Log R Ratio is last of the line's comma separated values
    private boolean compareBafLogrValues(String line) {
        boolean allowed = true;
        String[] values = line.split(",");
        String baf = values[values.length - 1];    // Mutation 5, Mutation 23
        String logr = values[values.length - 2];    // Mutation 14

        // Mutation 2
        // if either is NaN, evaluate them both
        if (baf.equals("NaN") || logr.equals("NaN")) {
            // if the other is not NaN, generate alert
            if (!(baf.equals("NaN")) || !(logr.equals("NaN"))) {
                alertFound = true;
                allowed = false;
            }
        }
        return allowed;
    }
}
