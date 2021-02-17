package edu.jhu.jhg.finalreportreleasedatacheck;

import edu.jhu.jhg.finalreportreleasedatacheck.checkFiles.FileContentCheck;
import edu.jhu.jhg.finalreportreleasedatacheck.checkFiles.FileNumberCheck;
import edu.jhu.jhg.finalreportreleasedatacheck.checkFiles.FileSizeCheck;
import edu.jhu.jhg.finalreportreleasedatacheck.checkFiles.FileStatsCheck;
import edu.jhu.jhg.finalreportreleasedatacheck.renameFiles.ReportRenamer;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReportDirectory {

    public static String STR_DATE;
    public static boolean alertFound = false;
    public static File writeLocation;
    private final File directory;
    private final List<File> pretestReports;
    private final int reportsCount;
    private final String columnsType;
    private final boolean renameReports;
    private final boolean checkReports;
    private final int expectedReports;
    private final int headerRow;
    private String exitCode = "";

    public ReportDirectory(String directory, String columnsType, boolean checkReports,
                           boolean renameReports, int expectedReports, String writeLocation)
            throws Exception {
        STR_DATE = getStrDate();
        this.directory = verifyDirectory(directory);
        this.pretestReports = setPretestReports();
        this.reportsCount = pretestReports.size();
        this.columnsType = columnsType;
        this.checkReports = checkReports;
        this.renameReports = renameReports;
        this.expectedReports = expectedReports;
        this.headerRow = getHeaderRow();
        ReportDirectory.writeLocation = verifyDirectory(writeLocation);
    }

    private String getStrDate() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        return dateFormat.format(date);
    }

   private File verifyDirectory(String dirPath) {
        return new File(dirPath);
   }

   private List<File> setPretestReports() {
       File[] allFiles = directory.listFiles();
       assert allFiles != null;
       List<File> reports = Arrays.stream(allFiles)
               .filter(e-> !(e.getName().startsWith("RENAME_LOG")))    // do not include rename logs    // Mutation 30
               .filter(e -> e.getName().endsWith(".csv"))    // omit any non-csv files
               .collect(Collectors.toList());
       if (reports.size() == 0) {                           // Mutation 20
           throw new IllegalStateException("No reports found in directory " + directory);
       }
       return reports;
   }

    // header is in the row after "[Data]"
    private int getHeaderRow() throws Exception {
        File oneReport = pretestReports.get(0);
        try (BufferedReader reader = new BufferedReader(new FileReader(oneReport))) {
            String line = "";
            int headerRow = 0;
            while (!(line.startsWith("[Data]"))) {    // some files can have trailing commas, so not ".equals"
                headerRow++;    // Mutation 4, Mutation 15, Mutation 18
                line = reader.readLine();
            }
            return headerRow;    // Mutation 17
        }
    }

   public String runOperations() throws Exception {
        if (checkReports) {                                                             // Mutation 9
            new FileNumberCheck(reportsCount, expectedReports).checkFileNumbers();
            new FileContentCheck(columnsType, pretestReports, headerRow).checkColumns();
            new FileSizeCheck(pretestReports).checkFileSizes();                         // Mutation 22
            new FileStatsCheck(pretestReports, headerRow).checkReportStats();

            // issues found by checkers will be written to a file
            // if no issues were found, display message as such to user
            if (! alertFound) {
                exitCode = "All reports pass checks";
            } else {
                exitCode = "Anomalies found in reports and written to output directory";
            }
        }
        if (renameReports) {    // Mutation 10
            new ReportRenamer(directory, pretestReports, headerRow).renameReports();
            exitCode += "\n\nReports renamed";
        }
        return exitCode;
    }

    public static class AlertWriter {

        private static File alertFile;
        private static boolean fileCreated = false;

        // there are typically few writes, which are short strings
        // FileWriter is used without buffer, and it is opened and closed for each write
        public static void writeAlert(String alert) throws IOException {
            ReportDirectory.alertFound = true;    // project-level result from all report checks
            if (! fileCreated) {    // Mutation 6
                createAlertFile();    // Mutation 21
            }
            FileWriter myWriter = new FileWriter(alertFile, true);    // append to file
            myWriter.write(alert + System.lineSeparator());
            myWriter.close();
        }

        private static void createAlertFile() throws IOException {
            String fileName = "Pretest_Final_Reports_Alerts" + STR_DATE + ".txt";
            alertFile = new File(writeLocation, fileName);
            alertFile.createNewFile();
            fileCreated = true;
        }
    }

    @Override
    public String toString() {
        return "ReportDirectory{" +
                "directory=" + directory +
                ", pretestReports=" + pretestReports +
                ", reportsCount=" + reportsCount +
                ", columnsType='" + columnsType + '\'' +
                ", renameReports=" + renameReports +
                ", checkReports=" + checkReports +
                ", expectedReports=" + expectedReports +
                ", headerRow=" + headerRow +
                ", completionStatus='" + exitCode + '\'' +
                '}';
    }
}
