package com.gitee.qdbp.tools.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.gitee.qdbp.able.exception.ServiceException;

/**
 * 导入回调函数
 *
 * @author zhaohuihua
 * @version 160302
 */
public abstract class ImportCallback extends SheetParseCallback {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 初始化处理逻辑 **/
    public void init(Workbook workbook, XMetadata metadata) throws ServiceException {
    }

    /** 收尾处理逻辑 **/
    public void finish(Workbook workbook, XMetadata metadata) throws ServiceException {
    }

    /** 开始解析Sheet之前的处理逻辑, 返回false跳过该Sheet **/
    public boolean onSheetStart(Sheet sheet, XMetadata metadata) {
        return true;
    }

    /** 解析Sheet完成之后的处理逻辑, 返回false跳过后面的所有Sheet **/
    public boolean onSheetFinished(Sheet sheet, XMetadata metadata) {
        return true;
    }

}
