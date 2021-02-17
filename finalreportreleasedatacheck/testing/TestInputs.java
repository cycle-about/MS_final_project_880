package edu.jhu.jhg.finalreportreleasedatacheck.testing;

import edu.jhu.jhg.finalreportreleasedatacheck.JobHandler;
import edu.jhu.jhg.finalreportreleasedatacheck.ReportDirectory;

public class TestInputs {

    private static final String writeLocation = "C:\\Users\\lvail1\\Projects\\finalreportreleasedatacheck\\test_outputs";

    public static ReportDirectory HU_ENAMEL;
    static {
        try {
            HU_ENAMEL = new ReportDirectory(
                "C:\\Users\\lvail1\\Projects\\finalreportreleasedatacheck\\test_inputs\\hu_enamelDefects\\Final_Genotyping_Reports", "Release Final Report", false, true, 79, writeLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ReportDirectory JOSEPH_MYELOMA;
    static {
        try {
            JOSEPH_MYELOMA = new ReportDirectory(
                    "C:\\Users\\lvail1\\Projects\\finalreportreleasedatacheck\\test_inputs\\joseph_multipleMyeloma\\Final_Genotyping_Reports", "Release Final Report", true, false, 211, writeLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JobHandler HANDLER_JOSEPH;
    static {
        try {
            JobHandler handler = new JobHandler();
            handler.generateJob("C:\\Users\\lvail1\\Projects\\finalreportreleasedatacheck\\test_inputs" +
                    "\\joseph_multipleMyeloma" +
                    "\\Final_Genotyping_Reports", "Release Final Report", true, false, 211, writeLocation);
            handler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ReportDirectory EGAN_EDS;
    static {
        try {
            EGAN_EDS = new ReportDirectory(
                    "C:\\Users\\lvail1\\Projects\\finalreportreleasedatacheck\\test_inputs\\egan_eds\\Final_Genotyping_Reports", "Release Final Report", false, true, 160, writeLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ReportDirectory HOLLAND_ORIG_NAME;
    static {
        try {
            HOLLAND_ORIG_NAME = new ReportDirectory("C:\\Users\\lvail1\\Projects\\finalreportreleasedatacheck\\test_inputs\\original_names_working_set", "Release Final Report", false, true, 15,
                    writeLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
