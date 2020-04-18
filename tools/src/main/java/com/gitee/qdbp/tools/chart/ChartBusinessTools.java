package com.gitee.qdbp.tools.chart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import com.gitee.qdbp.tools.chart.entity.BarAndLineDataset;
import com.gitee.qdbp.tools.chart.entity.BarDataset;
import com.gitee.qdbp.tools.chart.entity.LineDataset;
import com.gitee.qdbp.tools.chart.entity.RowItem;
import com.gitee.qdbp.tools.utils.StringTools;

/**
 * 图表业务工具类
 *
 * @author zhaohuihua
 * @version 20200216
 */
public class ChartBusinessTools {

    /**
     * 创建折线图
     * 
     * @param dataset 数据集
     * @param width 图片宽度
     * @param height 图片高度
     */
    public static BufferedImage createLineImage1(LineDataset dataset, int width, int height) {
        JFreeChart chart = createLineChart1(dataset, width, height);
        return chart.createBufferedImage(width, height, null);
    }

    /**
     * 创建折线图
     * 
     * @param dataset 数据集
     * @param output 输出流
     * @param width 图片宽度
     * @param height 图片高度
     * @throws IOException IO异常
     */
    public static void createLineChart1(LineDataset dataset, OutputStream output, int width, int height)
            throws IOException {
        JFreeChart chart = createLineChart1(dataset, width, height);
        ChartUtils.writeChartAsPNG(output, chart, width, height);
    }

    /**
     * 创建折线图
     * 
     * @param dataset 数据集
     * @param width 图片宽度
     * @param height 图片高度
     */
    public static JFreeChart createLineChart1(LineDataset dataset, int width, int height) {
        List<String> columnLabels = dataset.getColumnLabels();

        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        List<RowItem> lineRowItems = dataset.getLineRowItems();
        for (RowItem item : lineRowItems) {
            for (int i = 0, z = item.getData().size(); i < z; i++) {
                String columnLabel = getListItem(columnLabels, i);
                String rowLabel = item.getLabel();
                double rowValue = item.getData().get(i);
                lineDataset.setValue(rowValue, rowLabel, columnLabel);
            }
        }

        // 图形上是否显示数字
        boolean isShowDataLabels = false;
        CategoryPlot plot = ChartThemeTools.newDefaultCategoryPlot();

        // 折线图
        LineAndShapeRenderer lineRenderer = ChartThemeTools.newDefaultLineRenderer(isShowDataLabels, false);
        NumberAxis lineAxis = ChartThemeTools.newDefaultNumberAxis();
        FormatInfo lineFormat = parseFormatInfo(dataset.getLineUnits(), dataset.getLineRowItems());
        if (lineFormat != null && lineFormat.getFormatter() != null) {
            CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", lineFormat.formatter);
            lineRenderer.setDefaultItemLabelGenerator(generator);
            lineAxis.setNumberFormatOverride(lineFormat.getFormatter());
        }
        lineAxis.setLabel(concatLabel(dataset.getLineTitle(), lineFormat == null ? null : lineFormat.getUnitString()));
        // lineAxis.setUpperMargin(0.1D);
        plot.setDataset(lineDataset);
        plot.setDomainAxis(ChartThemeTools.newDefaultCategoryAxis());
        plot.setRangeAxis(lineAxis);
        plot.setRenderer(lineRenderer);

        JFreeChart chart = ChartThemeTools.newDefaultChart(plot, dataset.getChartTitle());
        chart.setTitle(concatLabel(dataset.getChartTitle(), lineFormat == null ? null : lineFormat.getUnitString()));
        return chart;
    }

    /**
     * 创建柱状图
     * 
     * @param dataset 数据集
     * @param width 图片宽度
     * @param height 图片高度
     */
    public static BufferedImage createBarImage1(BarDataset dataset, int width, int height) {
        JFreeChart chart = createBarChart1(dataset, width, height);
        return chart.createBufferedImage(width, height, null);
    }

    /**
     * 创建柱状图
     * 
     * @param dataset 数据集
     * @param output 输出流
     * @param width 图片宽度
     * @param height 图片高度
     * @throws IOException IO异常
     */
    public static void createBarChart1(BarDataset dataset, OutputStream output, int width, int height)
            throws IOException {
        JFreeChart chart = createBarChart1(dataset, width, height);
        ChartUtils.writeChartAsPNG(output, chart, width, height);
    }

    /**
     * 创建柱状图
     * 
     * @param dataset 数据集
     * @param width 图片宽度
     * @param height 图片高度
     */
    public static JFreeChart createBarChart1(BarDataset dataset, int width, int height) {
        List<String> columnLabels = dataset.getColumnLabels();
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        List<RowItem> barRowItems = dataset.getBarRowItems();
        for (RowItem item : barRowItems) {
            for (int i = 0, z = item.getData().size(); i < z; i++) {
                String columnLabel = getListItem(columnLabels, i);
                String rowLabel = item.getLabel();
                double rowValue = item.getData().get(i);
                barDataset.setValue(rowValue, rowLabel, columnLabel);
            }
        }

        // 图形上是否显示数字
        boolean isShowDataLabels = false;
        CategoryPlot plot = ChartThemeTools.newDefaultCategoryPlot();

        // 柱状图
        BarRenderer barRenderer = ChartThemeTools.newDefaultBarRenderer(isShowDataLabels, false);
        NumberAxis barAxis = ChartThemeTools.newDefaultNumberAxis();
        FormatInfo barFormat = parseFormatInfo(dataset.getBarUnits(), dataset.getBarRowItems());
        if (barFormat != null && barFormat.formatter != null) {
            CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", barFormat.formatter);
            barRenderer.setDefaultItemLabelGenerator(generator);
            barAxis.setNumberFormatOverride(barFormat.formatter);
        }
        barAxis.setLabel(concatLabel(dataset.getBarTitle(), barFormat == null ? null : barFormat.getUnitString()));
        plot.setDataset(barDataset);
        plot.setDomainAxis(ChartThemeTools.newDefaultCategoryAxis());
        plot.setRangeAxis(barAxis);
        plot.setRenderer(barRenderer);

        JFreeChart chart = ChartThemeTools.newDefaultChart(plot, dataset.getChartTitle());
        chart.setTitle(concatLabel(dataset.getChartTitle(), barFormat == null ? null : barFormat.getUnitString()));
        return chart;
    }

    /**
     * 创建柱状图+折线图
     * 
     * @param dataset 数据集
     * @param width 图片宽度
     * @param height 图片高度
     */
    public static BufferedImage createBarAndLineImage1(BarAndLineDataset dataset, int width, int height) {
        JFreeChart chart = createBarAndLineChart1(dataset, width, height);
        return chart.createBufferedImage(width, height, null);
    }

    /**
     * 创建柱状图+折线图
     * 
     * @param dataset 数据集
     * @param output 输出流
     * @param width 图片宽度
     * @param height 图片高度
     * @throws IOException IO异常
     */
    public static void createBarAndLineChart1(BarAndLineDataset dataset, OutputStream output, int width, int height)
            throws IOException {
        JFreeChart chart = createBarAndLineChart1(dataset, width, height);
        ChartUtils.writeChartAsPNG(output, chart, width, height);
    }

    /**
     * 创建柱状图+折线图
     * 
     * @param dataset 数据集
     * @param width 图片宽度
     * @param height 图片高度
     */
    public static JFreeChart createBarAndLineChart1(BarAndLineDataset dataset, int width, int height) {
        List<String> columnLabels = dataset.getColumnLabels();
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        List<RowItem> barRowItems = dataset.getBarRowItems();
        for (RowItem item : barRowItems) {
            for (int i = 0, z = item.getData().size(); i < z; i++) {
                String columnLabel = getListItem(columnLabels, i);
                String rowLabel = item.getLabel();
                double rowValue = item.getData().get(i);
                barDataset.setValue(rowValue, rowLabel, columnLabel);
            }
        }

        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        List<RowItem> lineRowItems = dataset.getLineRowItems();
        for (RowItem item : lineRowItems) {
            for (int i = 0, z = item.getData().size(); i < z; i++) {
                String columnLabel = getListItem(columnLabels, i);
                String rowLabel = item.getLabel();
                double rowValue = item.getData().get(i);
                lineDataset.setValue(rowValue, rowLabel, columnLabel);
            }
        }

        // 图形上是否显示数字
        boolean isShowDataLabels = false;
        CategoryPlot plot = ChartThemeTools.newDefaultCategoryPlot();

        // 柱状图
        BarRenderer barRenderer = ChartThemeTools.newDefaultBarRenderer(isShowDataLabels, false);
        NumberAxis barAxis = ChartThemeTools.newDefaultNumberAxis();
        FormatInfo barFormat = parseFormatInfo(dataset.getBarUnits(), dataset.getBarRowItems());
        if (barFormat != null && barFormat.formatter != null) {
            CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", barFormat.formatter);
            barRenderer.setDefaultItemLabelGenerator(generator);
            barAxis.setNumberFormatOverride(barFormat.formatter);
        }
        barAxis.setLabel(concatLabel(dataset.getBarTitle(), barFormat == null ? null : barFormat.getUnitString()));
        plot.setDataset(0, barDataset);
        plot.setDomainAxis(0, ChartThemeTools.newDefaultCategoryAxis());
        plot.setRangeAxis(0, barAxis);
        plot.setRenderer(0, barRenderer);

        // 折线图
        LineAndShapeRenderer lineRenderer = ChartThemeTools.newDefaultLineRenderer(isShowDataLabels, false);
        NumberAxis lineAxis = ChartThemeTools.newDefaultNumberAxis();
        FormatInfo lineFormat = parseFormatInfo(dataset.getLineUnits(), dataset.getLineRowItems());
        if (lineFormat != null && lineFormat.getFormatter() != null) {
            CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", lineFormat.formatter);
            lineRenderer.setDefaultItemLabelGenerator(generator);
            lineAxis.setNumberFormatOverride(lineFormat.getFormatter());
        }
        lineAxis.setLabel(concatLabel(dataset.getLineTitle(), lineFormat == null ? null : lineFormat.getUnitString()));
        lineRenderer.setDefaultItemLabelPaint(Color.RED);
        // lineAxis.setUpperMargin(0.1D);
        plot.setDataset(1, lineDataset);
        plot.setDomainAxis(1, ChartThemeTools.newDefaultCategoryAxis(null, false));
        plot.setRangeAxis(1, lineAxis);
        plot.setRenderer(1, lineRenderer);
        plot.mapDatasetToRangeAxis(1, 1);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        // 设置线条颜色
        ChartThemeTools.setRendererSeriesColors(lineRenderer, barRowItems.size(), lineRowItems.size());

        JFreeChart chart = ChartThemeTools.newDefaultChart(plot, dataset.getChartTitle());
        chart.setTitle(concatLabel(dataset.getChartTitle(), barFormat == null ? null : barFormat.getUnitString()));
        return chart;
    }

    private static String concatLabel(String title, String unitString) {
        StringBuilder buffer = new StringBuilder();
        if (title != null) {
            buffer.append(title);
        }
        if (unitString != null && unitString.length() > 0) {
            buffer.append('(').append(unitString).append(')');
        }
        return buffer.length() == 0 ? null : buffer.toString();
    }

    /**
     * units如果是%, 就返回百分数格式化对象<br>
     * 否则, 统计数字的大小决定用哪种缩放: 元,万元,亿元
     * 
     * @param units 单位: units="%" 或 units="元,万元,亿元"
     * @param data 数据
     * @return 格式化对象
     */
    private static FormatInfo parseFormatInfo(String units, List<RowItem> data) {
        if (units == null) {
            return null;
        }
        if ("%".equals(units)) {
            return new FormatInfo(units, new RateNumberFormat(0.01, "#"));
        }
        if (units.indexOf(',') < 0) {
            return new FormatInfo(units);
        }
        // 根据数字的个数
        int allCount = 0;
        int wanCount = 0; // 万以上的个数
        int yiCount = 0; // 亿以上的个数
        for (RowItem item : data) {
            for (double value : item.getData()) {
                allCount++;
                if (value >= 10000) {
                    wanCount++;
                }
                if (value >= 100000000) {
                    yiCount++;
                }
            }
        }
        int unitType = 1;
        if (1.0 * yiCount / allCount >= 0.75) { // 超过3/4的数据达到1亿
            unitType = 3;
        } else if (1.0 * wanCount / allCount >= 0.75) { // 超过3/4的数据达到1万
            unitType = 2;
        }
        if (unitType == 1) {
            return new FormatInfo(StringTools.removeSuffixAt(units, ','));
        }
        String[] unitArray = StringTools.split(units, ',');
        if (unitType >= unitArray.length) {
            unitType = unitArray.length - 1;
        }
        FormatInfo format = new FormatInfo();
        format.setUnitString(unitArray[unitType - 1]);
        format.setFormatter(new RateNumberFormat(Math.pow(10000, unitType - 1), "#.#"));
        return format;
    }

    private static String getListItem(List<String> columnLabels, int index) {
        return index >= columnLabels.size() ? null : columnLabels.get(index);
    }

    private static class RateNumberFormat extends NumberFormat {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;
        private double unitRate;
        private DecimalFormat decimalFormat;

        public RateNumberFormat(double unitRate, String pattern) {
            this(unitRate, new DecimalFormat(pattern));
        }

        public RateNumberFormat(double unitRate, DecimalFormat decimalFormat) {
            this.unitRate = unitRate;
            this.decimalFormat = decimalFormat;
        }

        @Override
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
            return decimalFormat.format(number / unitRate, toAppendTo, pos);
        }

        @Override
        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
            return decimalFormat.format(number / unitRate, toAppendTo, pos);
        }

        @Override
        public Number parse(String source, ParsePosition parsePosition) {
            Number number = decimalFormat.parse(source, parsePosition);
            if (number instanceof BigDecimal) {
                return ((BigDecimal) number).multiply(new BigDecimal(unitRate));
            } else {
                return number.doubleValue() * unitRate;
            }
        }

    }

    private static class FormatInfo {

        private String unitString;
        private NumberFormat formatter;

        public FormatInfo() {
        }

        public FormatInfo(String unitString) {
            this.setUnitString(unitString);
        }

        public FormatInfo(String unitString, NumberFormat formatter) {
            this.setUnitString(unitString);
            this.setFormatter(formatter);
        }

        public String getUnitString() {
            return unitString;
        }

        public void setUnitString(String unitString) {
            this.unitString = unitString;
        }

        public NumberFormat getFormatter() {
            return formatter;
        }

        public void setFormatter(NumberFormat formatter) {
            this.formatter = formatter;
        }

    }
}
