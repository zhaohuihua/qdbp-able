package com.gitee.qdbp.tools.excel.beans;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.utils.DateTools;
import com.gitee.qdbp.tools.excel.ExcelErrorCode;
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
        URL xlsx = PathTools.findClassResource(ExcelBeansTest.class, "现金流算法-测试数据.xlsx");

        Map<String, Object> vars = readGlobalVars();
        try (InputStream is = xlsx.openStream(); Workbook wb = WorkbookFactory.create(is)) {
            Map<String, CellRule> rules = MetadataTools.parseRules(wb, "转换规则");
            ExcelBeans tools = new ExcelBeans(rules, vars);
            BeanContainer container = tools.parse(wb, "到期还本");
            List<BeanGroup> groups = container.getGroups();
            for (BeanGroup group : groups) {
                List<Map<String, Object>> datas = group.getDatas();
                int size = datas == null ? 0 : datas.size();
                System.out.println(group.getName() + ", size=" + size);
                if (datas != null) {
                    for (Map<String, Object> data : datas) {
                        System.out.println('\t' + JsonTools.toLogString(data));
                    }
                }
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

    private static Map<String, Object> readGlobalVars() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("platDate", DateTools.parse("2019-03-05"));
        vars.put("currDate", DateTools.parse("2019-03-18"));
        return vars;
    }
}
