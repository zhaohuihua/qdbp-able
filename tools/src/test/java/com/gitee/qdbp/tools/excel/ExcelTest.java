package com.gitee.qdbp.tools.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.utils.DateTools;
import com.gitee.qdbp.tools.excel.model.RowInfo;
import com.gitee.qdbp.tools.excel.utils.MetadataTools;
import com.gitee.qdbp.tools.files.PathTools;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.JsonTools;
import com.gitee.qdbp.tools.utils.PropertyTools;

public class ExcelTest {

    private static class Callback extends ImportCallback {

        /** 版本序列号 **/
        private static final long serialVersionUID = 1L;

        private int index;
        private List<EmployeeInfo> employees = new ArrayList<>();

        public Callback(int index) {
            this.index = index;
        }

        @Override
        public void callback(Map<String, Object> map, RowInfo row) throws ServiceException {
            JSONObject json = new JSONObject();
            json.putAll(map);
            EmployeeInfo model;
            try {
                model = JSON.toJavaObject(json, EmployeeInfo.class);
                employees.add(model);
            } catch (JSONException e) {
                throw new ServiceException(ExcelErrorCode.EXCEL_DATA_FORMAT_ERROR, e);
            }
            System.out.println(index + "\timport: " + JsonTools.toJsonString(model));
        }

    }

    public static void main(String[] args) {
        URL path = PathTools.findClassResource(ExcelTest.class, "test.txt");
        System.out.println(path);
        Properties original = PropertyTools.load(path, "UTF-8");
        Properties common = PropertyTools.filter(original, "common."); // 公共配置
        Properties test1 = PropertyTools.filter(original, "test.1."); // 测试1的配置
        Properties test2 = PropertyTools.filter(original, "test.2."); // 测试2的配置
        test(PropertyTools.concat(new Properties(), common, test1), 1);
        test(PropertyTools.concat(new Properties(), common, test2), 2);
    }

    private static void test(Properties properties, int index) {

        URL xlsx = PathTools.findClassResource(ExcelTest.class, "员工信息导入." + index + ".xlsx");
        XMetadata metadata = MetadataTools.parseMetadata(properties);
        XExcelParser parser = new XExcelParser(metadata);

        // 导入
        List<EmployeeInfo> employees;
        try (InputStream is = xlsx.openStream()) {
            Callback callback = new Callback(index);
            parser.parse(is, callback);
            System.out.println(index + "\t" + JSON.toJSONString(callback));
            employees = callback.employees;
        } catch (IOException | ServiceException e) {
            e.printStackTrace();
            return;
        }

        URL tpl = PathTools.findClassResource(ExcelTest.class, "员工信息(模板)." + index + ".xlsx");
        String save = "D:/员工信息导出." + index + ".%s.xlsx";
        XExcelExporter exporter = new XExcelExporter(metadata);

        // 导出
        int count = 5;
        for (int i = 0; i < count; i++) {
            List<EmployeeInfo> list = new ArrayList<>();
            for (int j = i; j < count; j++) {
                list.addAll(employees);
            }
            new Runner(index, exporter, list, tpl, String.format(save, i + 1)).start();
        }
    }

    private static class Runner extends Thread {

        private int index;
        private XExcelExporter exporter;
        private List<?> data;
        private URL template;
        private String savepath;

        public Runner(int index, XExcelExporter exporter, List<?> data, URL template, String savepath) {
            this.index = index;
            this.exporter = exporter;
            this.data = data;
            this.template = template;
            this.savepath = savepath;
        }

        public void run() {

            Date start = new Date();

            System.out.println(index + "\t" + this.getName() + " start --> " + DateTools.toNormativeString(start));

            File file = new File(savepath);
            try (InputStream input = template.openStream(); OutputStream out = new FileOutputStream(file)) {
                exporter.export(data, input, out);
                System.out.println(index + "\t" + this.getName() + " export successful, " + file.getAbsolutePath());
            } catch (IOException | ServiceException e) {
                e.printStackTrace();
            }

            System.out.println(index + "\t" + this.getName() + " done --> " + ConvertTools.toDuration(start, true));
        }
    }

    public static enum Gender {
        UNKNOWN, MALE, FEMALE;
    }

    public static class EmployeeInfo {

        private Long id;
        private String dept;
        private String name;
        private Boolean positive;
        private Integer height;
        private Date birthday;
        private Gender gender;
        private Integer subsidy;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDept() {
            return dept;
        }

        public void setDept(String dept) {
            this.dept = dept;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getPositive() {
            return positive;
        }

        public void setPositive(Boolean positive) {
            this.positive = positive;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public Integer getSubsidy() {
            return subsidy;
        }

        public void setSubsidy(Integer subsidy) {
            this.subsidy = subsidy;
        }

    }
}
