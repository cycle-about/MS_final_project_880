package edu.jhu.jhg.finalreportreleasedatacheck.checkFiles;

import edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory.alertFound;

/*
 * Verifies that the columns in each report file are those expected for the report
 * type selected by the user
 */
public class FileContentCheck {

    private static final String RELEASE_REPORT_COLS = "SNP Name,Sample Name,Sample ID,SNP,Chr,Position,Allele1 - AB,Allele2 - AB,GC Score,Allele1 - Forward,Allele2 - Forward,Allele1 - Top,Allele2 - Top,Allele1 - Design,Allele2 - Design,Theta,R,X,Y,X Raw,Y Raw,B Allele Freq,Log R Ratio";
    private static final String VERIFY_ID_COLS = "SNP Name,Sample Name,GC Score,Allele1 - AB,Allele2 - AB,X,Y,X Raw,Y" +
            " Raw";
    private final String expectedColumns;
    private final List<File> pretestReports;
    private final int headerRow;

    public FileContentCheck(String columnsType, List<File> pretestReports, int headerRow) throws Exception {
        this.expectedColumns = setExpectedColumns(columnsType);
        this.pretestReports = pretestReports;
        this.headerRow = headerRow;
    }

    private String setExpectedColumns(String columnsType) {
        if (columnsType.equals("Release Final Report")) {    // Mutation 26
            return RELEASE_REPORT_COLS;
        } else if (columnsType.equals("VerifyId")) {    // Mutation 28
            return VERIFY_ID_COLS;
        } else {
            return "Error: invalid report type";
        }
    }

    public void checkColumns() throws Exception {
        for (File report : pretestReports) {
            try (Stream<String> lines = Files.lines(Paths.get(String.valueOf(report)))) {
                String header = lines.skip(headerRow).findFirst().get();
                if (!(header.equals(expectedColumns))) {    // Mutation 29
                    alertFound = true;
                    ReportDirectory.AlertWriter.writeAlert("Columns mismatch in file: " + report);
                }
            }
        }
    }
}
