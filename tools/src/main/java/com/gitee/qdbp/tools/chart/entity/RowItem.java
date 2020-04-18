package com.gitee.qdbp.tools.chart.entity;

import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 行数据
 *
 * @author zhaohuihua
 * @version 20200216
 */
public class RowItem {

    private String label;
    private List<Double> data = new ArrayList<>();

    public RowItem() {
    }

    public RowItem(String label, List<Double> data) {
        this.label = label;
        this.data = data;
    }

    public RowItem(String label, double... data) {
        this.label = label;
        this.addData(data);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public void addData(double... data) {
        if (data != null && data.length > 0) {
            for (double number : data) {
                this.data.add(number);
            }
        }
    }

    public static RowItem findRowData(List<RowItem> list, String rowLabel) {
        for (RowItem item : list) {
            if (rowLabel.equals(item.getLabel())) {
                return item;
            }
        }
        return null;
    }

    public static void addRowData(List<RowItem> list, String rowLabel, double... numbers) {
        VerifyTools.requireNotBlank(rowLabel, "rowLabel");
        RowItem item = RowItem.findRowData(list, rowLabel);
        if (item == null) {
            list.add(new RowItem(rowLabel, numbers));
        } else {
            item.addData(numbers);
        }
    }
}
