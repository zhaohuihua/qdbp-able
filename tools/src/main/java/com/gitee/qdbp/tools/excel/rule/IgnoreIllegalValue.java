package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.Map;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 忽略格式错误的内容, 如果不加这个规则, 解析失败将抛出异常<br>
 * 这个规则只是标记一下, 由SheetParseCallback.convert()和SheetFillCallback.convert()作特殊判断
 * 
 * @author zhaohuihua
 * @version 181027
 */
public class IgnoreIllegalValue implements CellRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    @Override
    public Map<String, Object> imports(CellInfo cellInfo) throws ServiceException {
        return null;
    }

    @Override
    public Map<String, Object> exports(CellInfo cellInfo) throws ServiceException {
        return null;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{ignoreIllegalValue:true}");
        return buffer.toString();
    }

}
