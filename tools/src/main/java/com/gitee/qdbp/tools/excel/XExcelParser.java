package com.gitee.qdbp.tools.excel;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.utils.ExcelHelper;

/**
 * excel2007版解析器
 *
 * @author zhaohuihua
 * @version 160302
 */
public class XExcelParser {

    private static final Logger log = LoggerFactory.getLogger(XExcelParser.class);

    private XMetadata metadata;

    public XExcelParser(XMetadata metadata) {
        this.metadata = metadata;
    }

    public void parse(InputStream is, ImportCallback cb) throws ServiceException {
        try (Workbook wb = WorkbookFactory.create(is)) {
            this.parse(wb, cb);
        } catch (IOException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_READ_ERROR);
        } catch (POIXMLException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_TEMPLATE_ERROR);
        } catch (InvalidFormatException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_FORMAT_ERROR);
        }
    }

    public void parse(Workbook wb, ImportCallback cb) {
        try {
            cb.init(wb, metadata);
            for (int i = 0, total = wb.getNumberOfSheets(); i < total; i++) {
                if (metadata.isEnableSheet(i, wb.getSheetName(i))) {
                    Sheet sheet = wb.getSheetAt(i);
                    if (!cb.onSheetStart(sheet, metadata)) {
                        continue; // 返回false跳过该Sheet 
                    }

                    // 解析Sheet
                    ExcelHelper.parse(sheet, metadata, cb);

                    if (!cb.onSheetFinished(sheet, metadata)) {
                        break; // 返回false跳过后面的所有Sheet 
                    }
                }
            }
            cb.finish(wb, metadata);
        } catch (POIXMLException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_TEMPLATE_ERROR);
        }
    }
}
