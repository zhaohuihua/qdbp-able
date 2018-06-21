package com.gitee.zhaohuihua.tools.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.zhaohuihua.core.exception.ServiceException;
import com.gitee.zhaohuihua.tools.excel.utils.ExcelHelper;

/**
 * Excel导出
 * 
 * @author zhaohuihua
 * @version 160302
 */
public class XExcelExporter {

    private static final Logger log = LoggerFactory.getLogger(XExcelExporter.class);

    private XMetadata metadata;

    private ExportCallback callback;

    public XExcelExporter(XMetadata metadata) {
        this.metadata = metadata;
        this.callback = new ExportCallback();
    }

    /**
     * Excel导出
     * 
     * @param data 待导出的数据
     * @param templatePath 模板文件的路径
     * @param output 输出流
     * @throws ServiceException
     */
    public void export(List<?> data, String templatePath, OutputStream output) throws ServiceException {
        this.export(data, templatePath, output, null);
    }

    public void export(List<?> data, InputStream template, OutputStream output) throws ServiceException {
        this.export(data, template, output, null);
    }

    public void export(List<?> data, String templatePath, OutputStream output, ExportCallback cb)
            throws ServiceException {
        try (InputStream input = new FileInputStream(new File(templatePath))) {
            export(data, input, output, cb);
        } catch (IOException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_READ_ERROR);
        }
    }

    public void export(List<?> data, InputStream template, OutputStream output, ExportCallback cb)
            throws ServiceException {

        if (cb == null) cb = this.callback;

        try (Workbook wb = WorkbookFactory.create(template)) {

            cb.init(wb, metadata);
            Sheet sheet = wb.getSheetAt(0);

            ExcelHelper.export(data, sheet, metadata, cb);

            // 计算所有公式
            // sheet.setForceFormulaRecalculation(true); // 设置Excel打开的时候计算
            wb.getCreationHelper().createFormulaEvaluator().evaluateAll(); // 事先计算

            cb.finish(wb, metadata);

            try {
                wb.write(output);
            } catch (IOException e) {
                log.error("write excel error.", e);
                throw new ServiceException(ExcelErrorCode.FILE_WRITE_ERROR);
            }
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

    public void setExportCallback(ExportCallback callback) {
        Objects.requireNonNull(callback, "callback");
        this.callback = callback;
    }

    public ExportCallback getExportCallback() {
        return this.callback;
    }
}
