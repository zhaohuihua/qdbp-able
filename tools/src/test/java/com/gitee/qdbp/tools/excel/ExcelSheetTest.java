package com.gitee.qdbp.tools.excel;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.gitee.qdbp.tools.excel.utils.ExcelTools;
import com.gitee.qdbp.tools.files.PathTools;

/**
 * Sheet页复制(前景色和背景色复制有问题)
 *
 * @author zhaohuihua
 * @version 190518
 */
public class ExcelSheetTest {

    public static void main(String[] args) {
        String outFile = "D:/ExcelSheet.xlsx";

        URL xlsx = PathTools.findClassResource(ExcelInOutTest.class, "员工信息导入.1.xlsx");

        try (InputStream is = xlsx.openStream(); OutputStream os = new FileOutputStream(outFile);
                Workbook src = WorkbookFactory.create(is); Workbook wb = new XSSFWorkbook();) {
            Sheet srcSheet = src.getSheetAt(0);
            Sheet targetSheet = wb.createSheet(srcSheet.getSheetName());
            ExcelTools.copySheet(srcSheet, targetSheet, true);
            wb.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
