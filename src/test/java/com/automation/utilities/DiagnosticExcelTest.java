package com.automation.utilities;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import java.util.Map;

public class DiagnosticExcelTest {

    @Test
    public void testReadExcel() throws Exception {
        File f = new File(System.getProperty("user.dir"), "src/test/resources/testdata/Users.xlsx");
        System.out.println("Trying to open: " + f.getAbsolutePath());
        List<Map<String, String>> rows = ExcelUtils.readSheet(f, "Logins");
        System.out.println("Rows read: " + rows.size());
        for (Map<String,String> r : rows) {
            System.out.println(r);
        }
    }
}