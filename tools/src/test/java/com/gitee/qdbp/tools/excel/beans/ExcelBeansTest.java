package com.gitee.qdbp.tools.excel.beans;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.utils.DateTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.ExcelErrorCode;
import com.gitee.qdbp.tools.excel.exception.ResultSetMismatchException;
import com.gitee.qdbp.tools.excel.json.BeanContainer;
import com.gitee.qdbp.tools.excel.json.BeanGroup;
import com.gitee.qdbp.tools.excel.json.ExcelBeans;
import com.gitee.qdbp.tools.excel.rule.CellRule;
import com.gitee.qdbp.tools.excel.utils.MetadataTools;
import com.gitee.qdbp.tools.files.PathTools;
import com.gitee.qdbp.tools.utils.JsonTools;

public class ExcelBeansTest {

    public static final Logger log = LoggerFactory.getLogger(ExcelBeansTest.class);

    public static void main(String[] args) {
        new ExcelBeansTest().readData();
    }

    @Test
    public void readData() {
        URL xlsx = PathTools.findClassResource(ExcelBeansTest.class, "现金流算法-测试数据.xlsx");

        Map<String, Object> vars = readGlobalVars();
        try (InputStream is = xlsx.openStream(); Workbook wb = WorkbookFactory.create(is)) {
            Map<String, CellRule> rules = MetadataTools.parseRules(wb, "转换规则");
            ExcelBeans tools = new ExcelBeans(rules, vars);
            // 读取配置项
            BeanContainer config = tools.parse(wb, "配置项");
            // 设置全局变量
            Map<String, Object> global = config.getData("g");
            if (VerifyTools.isNotBlank(global)) {
                vars.putAll(global);
            }
            // 输出Sheet[配置项]的内容
            outputContainer(config);

            BeanContainer container = tools.parse(wb, "到期还本");
            // 输出Sheet[到期还本]的内容
            outputContainer(container);

            // 获取首次还本日
            BeanGroup in = container.findGroup("还本付息入参");
            Date firstRepayPrincipalDate = (Date) in.findData("1").get("firstRepayPrincipalDate");
            // 比较节假日
            compareHolidays(config);
            // 比较还本计划
            comparePrincipalSchedules(container, firstRepayPrincipalDate);
        } catch (IOException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_READ_ERROR);
        } catch (POIXMLException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_FORMAT_ERROR);
        } catch (InvalidFormatException e) {
            log.error("read excel error.", e);
            throw new ServiceException(ExcelErrorCode.FILE_FORMAT_ERROR);
        }
    }

    private static void compareHolidays(BeanContainer container) {
        try {
            List<Date> holidays = new ArrayList<>();
            holidays.add(DateTools.parse("2019-01-01"));
            holidays.add(DateTools.parse("2019-02-04"));
            holidays.add(DateTools.parse("2019-02-05"));
            holidays.add(DateTools.parse("2019-02-06"));
            holidays.add(DateTools.parse("2019-02-07"));
            holidays.add(DateTools.parse("2019-02-08"));
            holidays.add(DateTools.parse("2019-04-05"));
            holidays.add(DateTools.parse("2019-05-01"));
            holidays.add(DateTools.parse("2019-06-07"));
            holidays.add(DateTools.parse("2019-09-13"));
            holidays.add(DateTools.parse("2019-10-01"));
            holidays.add(DateTools.parse("2019-10-02"));
            holidays.add(DateTools.parse("2019-10-03"));
            holidays.add(DateTools.parse("2019-10-04"));
            holidays.add(DateTools.parse("2019-10-07"));
            container.compareValues(holidays, "节假日列表");
        } catch (ResultSetMismatchException e) {
            log.error(e.getMessage());
        }
    }

    private static void comparePrincipalSchedules(BeanContainer container, Date firstRepayPrincipalDate) {
        try {
            // returnPrincipal  returnDate                  payDate
            // 5000 0000        #in.firstRepayPrincipalDate #returnDate+1d
            // 7000 0000        #returnDate+2M              #returnDate
            // 7000 0000        #returnDate+2M              #returnDate
            // 7000 0000        #returnDate+2M              #returnDate
            // 7000 0000        #returnDate+2M              #returnDate
            // 7000 0000        #returnDate+2M              #returnDate+5d
            Date returnDate = null;
            List<Map<String, Object>> schedules = new ArrayList<>();
            {
                Map<String, Object> item = new HashMap<>();
                item.put("returnPrincipal", 50000000L);
                item.put("returnDate", returnDate = firstRepayPrincipalDate);
                item.put("payDate", DateTools.calculate(returnDate, "+1d"));
                schedules.add(item);
            }
            {
                Map<String, Object> item = new HashMap<>();
                item.put("returnPrincipal", 70000000L);
                item.put("returnDate", returnDate = DateTools.calculate(returnDate, "+2M"));
                item.put("payDate", returnDate);
                schedules.add(item);
            }
            {
                Map<String, Object> item = new HashMap<>();
                item.put("returnPrincipal", 70000000L);
                item.put("returnDate", returnDate = DateTools.calculate(returnDate, "+2M"));
                item.put("payDate", returnDate);
                schedules.add(item);
            }
            {
                Map<String, Object> item = new HashMap<>();
                item.put("returnPrincipal", 70000000L);
                item.put("returnDate", returnDate = DateTools.calculate(returnDate, "+2M"));
                item.put("payDate", returnDate);
                schedules.add(item);
            }
            {
                Map<String, Object> item = new HashMap<>();
                item.put("returnPrincipal", 70000000L);
                item.put("returnDate", returnDate = DateTools.calculate(returnDate, "+2M"));
                item.put("payDate", returnDate);
                schedules.add(item);
            }
            {
                Map<String, Object> item = new HashMap<>();
                item.put("returnPrincipal", 70000000L);
                item.put("returnDate", returnDate = DateTools.calculate(returnDate, "+2M"));
                item.put("payDate", DateTools.calculate(returnDate, "+5d"));
                schedules.add(item);
            }
            container.compareDatas(schedules, "还本计划", Arrays.asList("idx"));
        } catch (ResultSetMismatchException e) {
            log.error(e.getMessage());
        }
    }

    private static void outputContainer(BeanContainer container) {
        List<BeanGroup> groups = container.getContent();
        for (BeanGroup group : groups) {
            List<Object> values = group.getValues();
            int valuesSize = values == null ? 0 : values.size();
            if (valuesSize > 0) {
                System.out.println(container.getName() + ':' + group.getName() + ", values.size=" + valuesSize);
                System.out.println('\t' + JsonTools.toLogString(values));
            }
            List<Map<String, Object>> datas = group.getDatas();
            int datasSize = datas == null ? 0 : datas.size();
            if (datasSize > 0) {
                System.out.println(container.getName() + ':' + group.getName() + ", datas.size=" + datasSize);
                for (Map<String, Object> data : datas) {
                    System.out.println('\t' + JsonTools.toLogString(data));
                }
            }
        }
    }

    private static Map<String, Object> readGlobalVars() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("platDate", DateTools.parse("2019-03-05"));
        vars.put("currDate", DateTools.parse("2019-03-18"));
        return vars;
    }
}
