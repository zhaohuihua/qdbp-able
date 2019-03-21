package com.gitee.qdbp.tools.excel;

import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.gitee.qdbp.able.exception.ServiceException;

/**
 * 导出回调函数
 *
 * @author zhaohuihua
 * @version 170223
 */
public class ExportCallback extends SheetFillCallback {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 初始化处理逻辑 **/
    public void init(Workbook workbook, XMetadata metadata) throws ServiceException {
    }

    /** 收尾处理逻辑 **/
    public void finish(Workbook workbook, XMetadata metadata) throws ServiceException {
    }

    /** 开始导出Sheet之前的处理逻辑, 返回false跳过该Sheet **/
    public boolean onSheetStart(Sheet sheet, XMetadata metadata, List<?> data) {
        return true;
    }

    /** 导出Sheet完成之后的处理逻辑, 返回false跳过后面的所有Sheet **/
    public boolean onSheetFinished(Sheet sheet, XMetadata metadata, List<?> data) {
        return true;
    }

}
