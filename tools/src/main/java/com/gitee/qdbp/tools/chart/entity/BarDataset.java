package com.gitee.qdbp.tools.chart.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 水果销售情况(万元) <pre><table>
 * <tr><td>品种</td><td>一季度</td><td>二季度</td><td>三季度</td><td>四季度</td><tr>
 * <tr><td>苹果</td><td>586000</td><td>478000</td><td>524000</td><td>628000</td><tr>
 * <tr><td>柚子</td><td>502000</td><td>423000</td><td>480000</td><td>540000</td><tr>
 * <tr><td>香蕉</td><td>475000</td><td>412000</td><td>382000</td><td>468000</td><tr>
 * </table>
    BarAndLineDataset dataset = new BarAndLineDataset();
    dataset.setChartTitle("水果销售情况");
    dataset.setBarTitle("销售量");
    dataset.setBarUnits("元,万元,亿元"); // 自动根据数据选择单位
    dataset.setColumnLabels("一季度", "二季度", "三季度", "四季度");
    dataset.addBarData("苹果", 586000, 478000, 524000, 628000);
    dataset.addBarData("柚子", 502000, 423000, 480000, 540000);
    dataset.addBarData("香蕉", 475000, 412000, 382000, 468000);
    ChartBusinessTools.createBarChart1(dataset, response.getOutputStream(), 600, 400);
 * </pre>
 *
 * @author zhaohuihua
 * @version 20200215
 */
public class BarDataset implements Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    private String chartTitle;
    private String barTitle;
    private String barUnits;

    private List<String> columnLabels = new ArrayList<>(); // 对应上表的季度
    private List<RowItem> barRowItems = new ArrayList<>(); // 对应上表的销售数据

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public String getBarTitle() {
        return barTitle;
    }

    public void setBarTitle(String barTitle) {
        this.barTitle = barTitle;
    }

    public String getBarUnits() {
        return barUnits;
    }

    public void setBarUnits(String barUnits) {
        this.barUnits = barUnits;
    }

    public void setColumnLabels(String... labels) {
        columnLabels.clear();
        if (labels != null && labels.length > 0) {
            for (String label : labels) {
                columnLabels.add(label);
            }
        }
    }

    public void setColumnLabels(List<String> columnLabels) {
        this.columnLabels = columnLabels;
    }

    public List<RowItem> getBarRowItems() {
        return barRowItems;
    }

    public List<String> getColumnLabels() {
        return columnLabels;
    }

    public void setBarRowItems(List<RowItem> barRowItems) {
        this.barRowItems = barRowItems;
    }

    public void addBarData(String rowLabel, double... numbers) {
        VerifyTools.requireNotBlank(rowLabel, "rowLabel");
        RowItem.addRowData(this.barRowItems, rowLabel, numbers);
    }

}
