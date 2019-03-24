package com.gitee.qdbp.tools.excel.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.utils.DateTools;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.ExcelErrorCode;
import com.gitee.qdbp.tools.excel.condition.Required;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.ColumnInfo;
import com.gitee.qdbp.tools.excel.model.FieldInfo;
import com.gitee.qdbp.tools.excel.rule.CellRule;
import com.gitee.qdbp.tools.excel.utils.ExcelTools;
import com.gitee.qdbp.tools.utils.ExpressionTools;
import com.gitee.qdbp.tools.utils.JsonTools;

/**
 * Bean解析器
 *
 * @author zhaohuihua
 * @version 190317
 */
public class ExcelBeans {

    private static final Logger log = LoggerFactory.getLogger(ExcelBeans.class);

    private static enum RowType {
        start, rule, field, title, bean
    }

    /** 转换规则 **/
    private Map<String, CellRule> rules;
    /** 全局变量 **/
    private Map<String, Object> globalVars;
    /** 最终结果 **/
    private BeanContainer container;
    /** 锁 **/
    private Lock lock = new ReentrantLock();

    // 运算中间结果
    /** 当前对象信息(一个对象有可能需要分多行读取) **/
    private BeanGroup currGroup;
    /** 当前对象的列信息, key=columnIndex, 从1开始 **/
    private Map<Integer, ColumnInfo> columns;
    /** 当前对象的开始行号 **/
    private int groupStartRowIndex;
    /** 当前对象最新的header的开始行号, header=field/title/rule **/
    private int headerStartRowIndex;
    /** 当前对象最新的bean定义开始行号 **/
    private int beanStartRowIndex;
    /** 当前对象最新的字段定义行号 **/
    private int fieldRowIndex;
    /** 当前对象的序号 **/
    private String currBeanIndex;
    /** 上一个对象的序号 **/
    private String lastBeanIndex;
    /** 最新的行类型标识 **/
    private RowType lastType;

    /** 构造函数 */
    public ExcelBeans() {
    }

    /**
     * 构造函数
     * 
     * @param rules 转换规则
     */
    public ExcelBeans(Map<String, CellRule> rules) {
        this(rules, null);
    }

    /**
     * 构造函数
     * 
     * @param rules 转换规则
     * @param vars 全局变量
     */
    public ExcelBeans(Map<String, CellRule> rules, Map<String, Object> vars) {
        this.rules = rules;
        this.globalVars = vars;
    }

    /**
     * 解析excel, 获取bean对象
     * 
     * @param is Excel文件流对象
     * @param sheetName Sheet名称
     */
    public BeanContainer parse(InputStream is, String sheetName) throws ServiceException {
        Objects.requireNonNull(sheetName, "sheetName");
        this.lock.lock();

        try (Workbook wb = WorkbookFactory.create(is)) {
            this.init();
            this.doParseSheet(wb, sheetName, 0);
            return this.container;
        } catch (IOException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_READ_ERROR);
        } catch (POIXMLException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_TEMPLATE_ERROR);
        } catch (InvalidFormatException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_FORMAT_ERROR);
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * 解析excel, 获取bean对象
     * 
     * @param wb Excel文件对象
     * @param sheetName Sheet名称
     */
    public BeanContainer parse(Workbook wb, String sheetName) throws ServiceException {
        Objects.requireNonNull(sheetName, "sheetName");
        this.lock.lock();
        try {
            this.init();
            this.doParseSheet(wb, sheetName, 0);
            return this.container;
        } catch (POIXMLException e) {
            log.error("read sheet error， sheetName=" + sheetName, e);
            throw new ServiceException(ExcelErrorCode.FILE_TEMPLATE_ERROR);
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * 解析excel, 获取bean对象
     * 
     * @param sheet Sheet对象
     */
    public BeanContainer parse(Sheet sheet) throws ServiceException {
        this.lock.lock();
        try {
            this.init();
            this.doParseSheet(sheet, 0);
            return this.container;
        } catch (POIXMLException e) {
            log.error("read sheet error， sheetName=" + sheet.getSheetName(), e);
            throw new ServiceException(ExcelErrorCode.FILE_TEMPLATE_ERROR);
        } finally {
            this.lock.unlock();
        }
    }

    private void init() {
        this.container = new BeanContainer();
        this.currGroup = null;
        this.columns = null;
        this.groupStartRowIndex = -1;
        this.headerStartRowIndex = -1;
        this.beanStartRowIndex = -1;
        this.fieldRowIndex = -1;
        this.currBeanIndex = null;
        this.lastBeanIndex = null;
        this.lastType = null;
    }

    private void saveHeader() {
        if (this.columns != null && !this.columns.isEmpty()) {
            List<ColumnInfo> list = newSortedColumnInfos(this.columns.values());
            this.currGroup.addColumns(list);
        }
    }

    private void saveGroup() {
        if (this.currGroup != null) {
            this.container.addItem(this.currGroup);
        }
        this.currGroup = null;
        this.columns = null;
        this.groupStartRowIndex = -1;
        this.headerStartRowIndex = -1;
        this.beanStartRowIndex = -1;
        this.fieldRowIndex = -1;
        this.currBeanIndex = null;
        this.lastType = null;
    }

    private void doParseSheet(Workbook wb, String sheetName, int skipRows) throws ServiceException {
        Sheet sheet = wb.getSheet(sheetName);
        if (sheet == null) {
            throw new ServiceException(ExcelErrorCode.EXCEL_SHEET_NOT_FOUND);
        }

        this.doParseSheet(sheet, skipRows);
    }

    private void doParseSheet(Sheet sheet, int skipRows) {
        this.container.setName(sheet.getSheetName());
        int totalRows = ExcelTools.getTotalRowsOfSheet(sheet);
        for (int i = skipRows; i <= totalRows; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            // 读取第一列的类型标识: start, rule, field, title, bean
            Cell firstCell = row.getCell(0);
            Object firstCellValue = ExcelTools.getCellValue(firstCell);
            RowType rowType = doParseRowType(firstCellValue);
            if (rowType == null) {
                continue;
            }

            // 解析行数据
            this.doParseRow(row, rowType);
            this.lastType = rowType;
        }
        this.saveGroup();
    }

    private void doParseRow(Row row, RowType rowType) {
        int rowIndex = row.getRowNum() + 1;
        if (rowType == RowType.start) { // 开启一个新的组
            this.saveGroup();
            this.groupStartRowIndex = rowIndex;
            this.doParseStartRow(row);
            return;
        }

        if (groupStartRowIndex < 0) { // 未找到#start就已经找到其他标识了
            String sheetName = container.getName();
            log.warn("Sheet[{}]Row[{}]: {} must be followed by start", sheetName, rowIndex, rowType.name());
            return;
        }
        if (this.currGroup == null) {
            return; // 存在#start但是解析失败, 这一组都不需要处理了
        }

        if (rowType == RowType.bean) {
            if (this.lastType != RowType.bean) {
                this.saveHeader();
                this.beanStartRowIndex = rowIndex;
            }
            this.lastBeanIndex = this.currBeanIndex;
            this.currBeanIndex = null;
            if (this.fieldRowIndex > 0) {
                this.doParseBeanRow(row);
            } else {
                // 没有定义field, 按List<Object>处理, 如List<String>
                if (this.lastType != RowType.bean) {
                    String m = "Sheet[{}]Row[{}]: field not found start by Row[{}], To be processed by List<Object>";
                    log.trace(m, container.getName(), rowIndex, Math.max(groupStartRowIndex, headerStartRowIndex));
                }
                this.doParseValueRow(row);
            }
        } else { // field title rule
            if (this.lastType == RowType.start || this.lastType == RowType.bean) {
                this.headerStartRowIndex = rowIndex;
                this.fieldRowIndex = -1;
            }
            if (rowType == RowType.field) {
                this.fieldRowIndex = rowIndex;
            }
            this.doParseHeaderRow(row, rowType);
        }
    }

    private void doParseStartRow(Row row) {
        // start   in  还本付息入参
        // start   还本付息入参
        // 读取第二列和第三列的值
        // 如果只有第二列存在值, 就取这个值作为name, 没有alias
        // 如果只有第一列存在值, 如果是英文字符就是alias, 否则就是name
        // 如果两列都有值, 就取第二列作为alias, 第三列值作为name
        String firstValue = getCellValueOfString(row.getCell(1));
        String secondValue = getCellValueOfString(row.getCell(2));
        if (VerifyTools.isAllBlank(firstValue, secondValue)) { // 两个值都为空
            int rowIndex = row.getRowNum() + 1;
            log.warn("Sheet[{}]Row[{}]: start name and alias not found", container.getName(), rowIndex);
            return;
        }
        String name = null;
        String alias = null;
        if (VerifyTools.isBlank(firstValue)) {
            name = secondValue;
        } else if (VerifyTools.isBlank(secondValue)) {
            if (StringTools.isAscii(firstValue)) {
                alias = firstValue;
            } else {
                name = firstValue;
            }
        } else {
            alias = firstValue;
            name = secondValue;
        }
        this.currGroup = new BeanGroup(name, alias);
    }

    // 解析field title rule
    private void doParseHeaderRow(Row row, RowType rowType) {
        Map<Integer, String> map = doParseHeaderValues(row);
        int rowIndex = row.getRowNum() + 1;
        if (map.isEmpty()) {
            if (rowType == RowType.field) {
                log.warn("Sheet[{}]Row[{}]: {} cells is required", container.getName(), rowIndex, rowType.name());
            } else {
                log.warn("Sheet[{}]Row[{}]: {} cells is all blank", container.getName(), rowIndex, rowType.name());
            }
            return;
        }
        if (rowType == RowType.field) {
            // 解析序号列
            String idxValue = getCellValueOfString(row.getCell(1));
            if (VerifyTools.isNotBlank(idxValue)) {
                map.put(2, idxValue);
            }
        }

        if (this.columns == null) {
            this.columns = new HashMap<>();
        }
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            String value = entry.getValue();
            if (VerifyTools.isBlank(value)) {
                continue;
            }
            int columnIndex = entry.getKey();
            if (this.columns.containsKey(columnIndex)) {
                ColumnInfo columnInfo = this.columns.get(columnIndex);
                fillHeaderValue(columnInfo, rowType, rowIndex, value);
            } else {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setColumn(columnIndex);
                fillHeaderValue(columnInfo, rowType, rowIndex, value);
                this.columns.put(columnIndex, columnInfo);
            }
        }
    }

    private void doParseBeanRow(Row row) {
        // 生成cellInfos
        List<ColumnInfo> columnInfos = newSortedColumnInfos(columns.values());
        List<CellInfo> cellInfos = newCellInfos(columnInfos);
        // 从Excel读取单元格数据
        int rowIndex = row.getRowNum() + 1;
        for (CellInfo cellInfo : cellInfos) {
            if (cellInfo != null) {
                cellInfo.setRow(rowIndex);
                Object value = getCellValueOfObject(row.getCell(cellInfo.getColumn() - 1));
                if (VerifyTools.isNotBlank(value)) {
                    cellInfo.setValue(value);
                }
            }
        }
        // 读取序号(第1列是RowType, 故从2开始)
        String idx = null;
        CellInfo idxCell = cellInfos.get(2);
        if (idxCell != null) {
            Object value = idxCell.getValue();
            idx = value == null ? null : value.toString();
        }
        if (VerifyTools.isBlank(idx)) {
            idx = String.valueOf(rowIndex - beanStartRowIndex + 1);
        }
        this.currBeanIndex = idx;
        // 执行数据转换
        Map<String, Object> data = new HashMap<>();
        for (CellInfo cellInfo : cellInfos) {
            if (cellInfo != null) {
                // 替换占位符
                doReplacePlaceholder(cellInfo, data);
                // 根据设置的规则执行数据转换
                doExecuteConvert(cellInfo, data);
            }
        }
        this.currGroup.addData(idx, data);
    }

    private void doParseValueRow(Row row) {
        int rowIndex = row.getRowNum() + 1;
        // 读取序号, 第2列是idx
        String idx = getCellValueOfString(row.getCell(1));
        if (VerifyTools.isBlank(idx)) {
            idx = String.valueOf(rowIndex - beanStartRowIndex + 1);
        }
        this.currBeanIndex = idx;
        // 第1列是RowType, 第2列是idx, 取第3列
        Object value = getCellValueOfObject(row.getCell(2)); // 从0开始
        this.currGroup.addValue(idx, value);
    }

    private static final Pattern EXP =
            Pattern.compile("#(?:(\\w+)\\s*\\.)?\\s*(\\w+)\\s*([+\\-*/]['\"]?\\s*[^'\"]+['\"]?)*");

    private void doReplacePlaceholder(CellInfo cellInfo, Map<String, Object> data) {
        // 如果占位符带别名限定, 如#in.repayDate, 直接根据别名从对应的group中获取
        // 如果占位符不带别名限定, 分别从以下位置查找: 当前bean(data), 上一个bean(currGroup.findLastData), 全局(globalVars)
        //    idx startDate       endDate
        //    int date            date
        //    序号 区间开始日       区间结束日
        //    1   #in.repayDate   #startDate+3M-1d
        //    2   #startDate+3M   #startDate+3M-1d
        //    3   #startDate+3M   #startDate+3M-1d
        //    4   #startDate+3M   #startDate+3M-1d
        Object value = cellInfo.getValue();
        if (!(value instanceof String)) {
            return;
        }
        String string = (String) value;
        Matcher matcher = EXP.matcher(string);
        if (!matcher.matches()) {
            return;
        }
        String alias = matcher.group(1);
        String field = matcher.group(2);
        String suffix = matcher.group(3);
        // 查找占位符对应的变量值
        Object newValue = null;
        if (VerifyTools.isNotBlank(alias)) {
            newValue = findSpecVarValue(alias, field);
        } else {
            newValue = findCurrVarValue(field, data);
        }
        if (newValue == null || VerifyTools.isBlank(suffix)) {
            cellInfo.setValue(newValue);
            return;
        }
        // 计算表达式
        try {
            if (newValue instanceof Date) {
                cellInfo.setValue(DateTools.calculate((Date) newValue, suffix));
            } else if (newValue instanceof Number) {
                cellInfo.setValue(calculateExpression((Number) newValue, suffix));
            } else if (newValue instanceof String) {
                cellInfo.setValue(calculateExpression((String) newValue, suffix));
            } else {
                throw new IllegalArgumentException("unsupported value type");
            }
        } catch (Exception e) {
            cellInfo.setValue(null);
            String m = "Sheet[{}]Cell[{},{}], value:{}, failed to calculate expression:{}, {}";
            String columnName = ExcelTools.columnIndexToName(cellInfo.getColumn());
            String valueString = JsonTools.toLogString(newValue) + '(' + newValue.getClass().getSimpleName() + ')';
            log.warn(m, container.getName(), cellInfo.getRow(), columnName, valueString, suffix, e.toString());
        }
    }

    private Object calculateExpression(Number newValue, String expression) {
        return ExpressionTools.parseExpression(newValue + expression);
    }

    private Object calculateExpression(String newValue, String expression) {
        return ExpressionTools.parseExpression("'" + newValue + "'" + expression);
    }

    // 根据别名从指定的BeanGroup中查找变量
    private Object findSpecVarValue(String alias, String field) {
        if (alias.equals("g")) { // 从全局变量中查找
            return this.globalVars == null ? null : this.globalVars.get(field);
        } else { // 根据别名从指定的BeanGroup中查找
            BeanGroup group = container.findGroup(alias);
            return group == null ? null : group.findLastFieldValue(field);
        }
    }

    // 查找未指定别名的变量
    // 分别从以下位置查找: 当前bean(data), 上一个bean(currGroup.lastBean), 全局(globalVars)
    private Object findCurrVarValue(String field, Map<String, Object> data) {
        if (data.containsKey(field)) {
            return data.get(field);
        } else {
            String idx = this.lastBeanIndex;
            Map<String, Object> temp = idx == null ? null : this.currGroup.findData(idx);
            if (temp != null && temp.containsKey(field)) {
                return temp.get(field);
            } else {
                return this.globalVars == null ? null : this.globalVars.get(field);
            }
        }
    }

    private void doExecuteConvert(CellInfo cellInfo, Map<String, Object> map) {
        if (VerifyTools.isBlank(cellInfo.getField())) {
            return;
        }
        List<CellRule> rules = cellInfo.getRules();
        if (rules == null || rules.isEmpty()) {
            // 没有预置的转换规则
            map.put(cellInfo.getField(), cellInfo.getValue());
            return;
        }

        // 调用转换规则
        for (CellRule rule : rules) {
            try {
                Map<String, Object> values = rule.imports(cellInfo);
                map.put(cellInfo.getField(), cellInfo.getValue());
                if (VerifyTools.isNotBlank(values)) {
                    map.putAll(values);
                }
            } catch (Exception e) {
                Object value = cellInfo.getValue();
                cellInfo.setValue(null);
                if (log.isWarnEnabled()) {
                    String m = "Sheet[{}]Cell[{},{}], value:{}, failed to execute convert rule:{}, {}";
                    String sheetName = container.getName();
                    String columnName = ExcelTools.columnIndexToName(cellInfo.getColumn());
                    String valueString = JsonTools.toLogString(value);
                    log.warn(m, sheetName, cellInfo.getRow(), columnName, valueString, rule.toString(), e.toString());
                }
            }
        }
    }

    private void fillHeaderValue(ColumnInfo columnInfo, RowType rowType, int rowIndex, String value) {
        if (rowType == RowType.field) {
            Required required = Required.of(value);
            columnInfo.setField(required.getName());
            if (required.isRequired()) {
                columnInfo.setRequired(true);
            }
        } else if (rowType == RowType.title) {
            Required required = Required.of(value);
            columnInfo.setTitle(required.getName());
            if (required.isRequired()) {
                columnInfo.setRequired(true);
            }
        } else if (rowType == RowType.rule) {
            String[] names = StringTools.split(value, ',', '|');
            List<CellRule> rules = new ArrayList<>();
            for (String name : names) {
                if (VerifyTools.isBlank(name)) {
                    continue;
                }
                CellRule rule = this.rules == null ? null : this.rules.get(name);
                if (rule == null) {
                    String m = "Sheet[{}]Cell[{},{}], {} type [{}] not found.";
                    String columnName = ExcelTools.columnIndexToName(columnInfo.getColumn());
                    log.warn(m, container.getName(), rowIndex, columnName, rowType.name(), name);
                } else {
                    rules.add(rule);
                }
            }
            if (VerifyTools.isNotBlank(rules)) {
                columnInfo.setRules(rules);
            }
        }
    }

    /**
     * 根据columnIndex生成完整的列信息表<br>
     * 注意: 不会跳过中间列(如果中间某列为空, 则list项为null)
     * 
     * @param list
     * @return 完整的列信息表
     */
    private static <T extends FieldInfo> List<T> newSortedColumnInfos(Collection<T> list) {
        // 统计最大列序号, 用于循环
        int maxColumnIndex = 0;
        Map<Integer, T> map = new HashMap<>();
        for (T item : list) {
            if (item == null || item.getColumn() == null) {
                continue;
            }
            int columnIndex = item.getColumn();
            if (maxColumnIndex < columnIndex) {
                maxColumnIndex = columnIndex;
            }
            map.put(columnIndex, item);
        }
        List<T> columns = new ArrayList<>();
        for (int i = 0; i <= maxColumnIndex; i++) {
            columns.add(map.get(i));
        }
        return columns;
    }

    private static List<CellInfo> newCellInfos(List<ColumnInfo> columnInfos) {
        List<CellInfo> cellInfos = new ArrayList<>();
        for (ColumnInfo columnInfo : columnInfos) {
            if (columnInfo == null) {
                cellInfos.add(null);
            } else {
                CellInfo info = columnInfo.to(CellInfo.class);
                info.setCells(cellInfos);
                cellInfos.add(info);
            }
        }
        return cellInfos;
    }

    private static String getCellValueOfString(Cell cell) {
        if (cell == null) {
            return null;
        }
        Object value = ExcelTools.getCellValue(cell);
        return value == null ? null : value.toString();
    }

    private static Object getCellValueOfObject(Cell cell) {
        if (cell == null) {
            return null;
        }
        return ExcelTools.getCellValue(cell);
    }

    private static Map<Integer, String> doParseHeaderValues(Row row) {
        Map<Integer, String> map = new HashMap<>();
        int totalSize = ExcelTools.getTotalColumnsOfRow(row);
        for (int i = 2; i < totalSize; i++) {
            int columnIndex = i + 1;
            String value = getCellValueOfString(row.getCell(i));
            if (VerifyTools.isNotBlank(value)) {
                map.put(columnIndex, value);
            }
        }
        return map;
    }

    private static RowType doParseRowType(Object value) {
        if (VerifyTools.isBlank(value)) {
            return null;
        }
        String string = value.toString();
        for (RowType t : RowType.values()) {
            if (string.equals(t.name())) {
                return t;
            }
        }
        return null;
    }
}
