package edu.jhu.jhg.finalreportreleasedatacheck.checkFiles;

import edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory.alertFound;

/*
 * Shows alert if a file is 10% greater or 10% less than the mean size
 * of all files in the directory
 */
public class FileSizeCheck {

    private final List<File> pretestReports;
    private final double allowedMax;
    private final double allowedMin;

    public FileSizeCheck(List<File> pretestReports) {
        this.pretestReports = pretestReports;
        double meanSize = getMeanSize();
        this.allowedMax = meanSize * 1.1;
        this.allowedMin = meanSize * 0.9;
    }

    private double getMeanSize(){
            return pretestReports.stream()
                    .mapToDouble(File::length)
                    .average()
                    .getAsDouble();
    }

    // alert if file is 10% greater or 10% less than mean size
    public void checkFileSizes() throws IOException {
        for (File file : pretestReports) {
            if (file.length() > allowedMax) {    // Mutation 13
                alertFound = true;
                ReportDirectory.AlertWriter.writeAlert("File is at least 10% larger than average: " + file);
            } else if (file.length() < allowedMin) {
                alertFound = true;
                ReportDirectory.AlertWriter.writeAlert("File is at least 10% smaller than average: " + file);
            }
        }
    }
}
