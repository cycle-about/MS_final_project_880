package edu.jhu.jhg.finalreportreleasedatacheck.renameFiles;

import edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/*
 * Replaces the file number from GenomeStudio with the file's sample id
 *     e.g. Doe_QCArray_071170_FinalReport3 -> Doe_QCArray_071170_FinalReport-0224050215
 * Generates a new timestamped "RENAME_LOG" with original and new name of each file
 * ? Show message/skip [in tbd] of any filenames that did not match expected format
 */

public class ReportRenamer {

    private final File directory;
    private final List<File> pretestReports; // already omits any non-csv, and any 'RENAME_LOG'
    private final int headerRow;
    private final File renameLog;

    public ReportRenamer(File directory, List<File> pretestReports, int headerRow) throws Exception {
        this.directory = directory;
        this.pretestReports = pretestReports;
        this.headerRow = headerRow;
        this.renameLog = makeRenameLog();
    }

    private File makeRenameLog() throws IOException {
        String fileName = "RENAME_LOG_" + ReportDirectory.STR_DATE + ".csv";
        File renameLog = new File(ReportDirectory.writeLocation, fileName);
        renameLog.createNewFile();
        return renameLog;
    }

    public void renameReports() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(renameLog))) {
            writer.write("Original Name,New Name");    // create csv header

            for (File file : pretestReports) {
                try (Stream<String> lines = Files.lines(Paths.get(String.valueOf(file)))) {
                    String origName = file.getName();
                    // Mutation 7, Mutation 24
                    String dataRow = lines.skip(headerRow + 1).findFirst().get(); // skip header rows, and row with
                    // column names
                    String sampleId = getSampleId(dataRow);
                    String newName = replaceNumber(origName, sampleId);
                    File newFullPath = new File(directory, newName);
                    file.renameTo(newFullPath);
                    writer.newLine();
                    writer.write(origName + "," + newName);
                }
            }
        }
    }

    // Sample Name is second comma-separated item in each row
    // in both report types, Verify Id and Release Report (see FileContentCheck)
    // Sample Id is last 10 digits of Sample Name
    private String getSampleId(String dataRow) {
        String[] values = dataRow.split(",");
        String sampleName = values[1];
        return sampleName.substring(sampleName.length() - 10);    // Mutation 3, Mutation 8, Mutation 25
    }

    private String replaceNumber(String origName, String sampleId) {
        String[] split = origName.split("FinalReport");
        return split[0] + "FinalReport-" + sampleId + ".csv";
    }
}
