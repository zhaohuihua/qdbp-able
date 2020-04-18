package com.gitee.qdbp.tools.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import com.gitee.qdbp.tools.chart.HeitiFont.PlainHeiti;

/**
 * JFreeChart风格工具类<br>
 * 解决中文乱码问题<br>
 * 用来对柱状图、折线图、饼图、堆积柱状图、时间序列图的样式进行渲染<br>
 * 设置X-Y坐标轴样式<br>
 * 
 * @author chenchangwen
 * @since:2014-2-18
 */
public class ChartThemeTools {

    private static boolean DEFAULT_THEME_INITED = false;
    private static String NO_DATA_MSG = "数据加载失败";
    private static Color DEFAULT_LINE_COLOR = Color.decode("0xCCCCCC");
    private static Color[] CHART_COLORS = UiStyleTools.parseHexColors(
        "2EC7C9,9A7FD1,5AB1EF,07A2A4,DC69AA,97B552,F5994E,C9AB00,FFB980,D87A80,6BE6C1,7EB00A,E5CF0D,95706D,588DD5,B6A2DE,C05050,8D98B3,59678C,6F5553,C14089");

    public static Color getDefaultColor(int index) {
        return CHART_COLORS[index % CHART_COLORS.length];
    }

    /** 中文主题样式 解决乱码 **/
    // 参考自: https://my.oschina.net/abian/blog/278448
    public static void setDefaultChartTheme() {
        if (DEFAULT_THEME_INITED == true) {
            return;
        }
        DEFAULT_THEME_INITED = true;

        // 设置中文主题样式 解决乱码
        StandardChartTheme cnTheme = new StandardChartTheme("CN");
        // 设置标题字体
        cnTheme.setExtraLargeFont(new PlainHeiti(16));
        // 设置图例的字体
        cnTheme.setRegularFont(new PlainHeiti(12));
        // 设置轴向的字体
        cnTheme.setLargeFont(new PlainHeiti(12));
        cnTheme.setSmallFont(new PlainHeiti(12));
        cnTheme.setTitlePaint(new Color(51, 51, 51));
        cnTheme.setSubtitlePaint(new Color(85, 85, 85));

        cnTheme.setLegendBackgroundPaint(Color.WHITE); // 设置标注
        cnTheme.setLegendItemPaint(Color.BLACK); //
        cnTheme.setChartBackgroundPaint(Color.WHITE);

        // 绘制颜色绘制颜色.轮廓供应商
        // paintSequence,outlinePaintSequence,strokeSequence,outlineStrokeSequence,shapeSequence
        Paint[] outlinePaintSequence = { Color.WHITE };
        // 绘制器颜色源
        DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(CHART_COLORS, CHART_COLORS,
                outlinePaintSequence, DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        cnTheme.setDrawingSupplier(drawingSupplier);

        cnTheme.setPlotBackgroundPaint(Color.WHITE); // 绘制区域
        cnTheme.setPlotOutlinePaint(Color.WHITE); // 绘制区域外边框
        cnTheme.setLabelLinkPaint(new Color(8, 55, 114)); // 链接标签颜色
        cnTheme.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);

        cnTheme.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
        cnTheme.setDomainGridlinePaint(DEFAULT_LINE_COLOR); // X坐标轴垂直网格颜色
        cnTheme.setRangeGridlinePaint(DEFAULT_LINE_COLOR); // Y坐标轴水平网格颜色

        cnTheme.setBaselinePaint(Color.WHITE);
        cnTheme.setCrosshairPaint(Color.BLUE); // 不确定含义
        cnTheme.setAxisLabelPaint(new Color(51, 51, 51)); // 坐标轴标题文字颜色
        cnTheme.setTickLabelPaint(new Color(67, 67, 72)); // 刻度数字
        cnTheme.setBarPainter(new StandardBarPainter()); // 设置柱状图渲染
        cnTheme.setXYBarPainter(new StandardXYBarPainter()); // XYBar 渲染

        cnTheme.setItemLabelPaint(Color.BLACK);
        cnTheme.setThermometerPaint(Color.WHITE); // 温度计

        ChartFactory.setChartTheme(cnTheme);
    }

    public static void setRendererSeriesColors(AbstractRenderer renderer, int startIndex, int total) {
        for (int i = 0; i < total; i++) {
            renderer.setSeriesPaint(i, getDefaultColor(startIndex + i));
        }
    }

    //    public static void setDefaultChartTheme(JFreeChart chart) {
    //        setThemeForChart(chart);
    //        CategoryPlot plot = chart.getCategoryPlot();
    //        setThemeForCategoryPlot(plot);
    //        for (int i = 0, z = plot.getDomainAxisCount(); i < z; i++) {
    //            CategoryAxis axis = plot.getDomainAxis(i);
    //            setThemeForCategoryAxis(axis);
    //        }
    //        for (int i = 0, z = plot.getRangeAxisCount(); i < z; i++) {
    //            ValueAxis axis = plot.getRangeAxis(i);
    //            setThemeForValueAxis(axis);
    //        }
    //    }

    public static JFreeChart newDefaultChart(CategoryPlot plot) {
        return newDefaultChart(plot, null);
    }

    public static JFreeChart newDefaultChart(CategoryPlot plot, String title) {
        JFreeChart chart = new JFreeChart(title, new PlainHeiti(16), plot, true);
        chart.setPadding(new RectangleInsets(10, 0, 10, 0));
        chart.setBackgroundPaint(Color.WHITE);
        chart.setTextAntiAlias(true); // 设置文本抗锯齿
        // 图例
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(new PlainHeiti(14));
            legend.setFrame(new BlockBorder(Color.WHITE)); // 设置图例无边框(默认黑色边框)
        }
        return chart;
    }

    public static CategoryPlot newDefaultCategoryPlot() {
        CategoryPlot plot = new CategoryPlot();
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setInsets(new RectangleInsets(10, 10, 5, 10));
        // Y轴网格线条
        plot.setRangeGridlinePaint(DEFAULT_LINE_COLOR);
        plot.setRangeGridlineStroke(new BasicStroke(0.3F));
        // plot.setRangeGridlinesVisible(false);
        return plot;
    }

    public static CategoryAxis newDefaultCategoryAxis() {
        return newDefaultCategoryAxis(null, true);
    }

    public static CategoryAxis newDefaultCategoryAxis(String label) {
        return newDefaultCategoryAxis(label, true);
    }

    public static CategoryAxis newDefaultCategoryAxis(String label, boolean visible) {
        CategoryAxis axis = new CategoryAxis(label);
        axis.setAxisLinePaint(DEFAULT_LINE_COLOR); // X坐标轴颜色
        axis.setTickMarkPaint(DEFAULT_LINE_COLOR); // X坐标轴标记|竖线颜色
        axis.setTickLabelFont(new PlainHeiti(14));
        axis.setVisible(visible);
        return axis;
    }

    public static DateAxis newDefaultDateAxis() {
        return newDefaultDateAxis(null, true);
    }

    public static DateAxis newDefaultDateAxis(String label) {
        return newDefaultDateAxis(label, true);
    }

    public static DateAxis newDefaultDateAxis(String label, boolean visible) {
        DateAxis axis = new DateAxis(label);
        axis.setAxisLinePaint(DEFAULT_LINE_COLOR); // X坐标轴颜色
        axis.setTickMarkPaint(DEFAULT_LINE_COLOR); // X坐标轴标记|竖线颜色
        axis.setTickLabelFont(new PlainHeiti(14));
        axis.setVisible(visible);
        // 第二个参数是时间轴间距
        axis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1, new SimpleDateFormat("yyyy-MM")));
        axis.setAutoTickUnitSelection(false);
        return axis;
    }

    public static NumberAxis newDefaultNumberAxis() {
        return newDefaultNumberAxis(null);
    }

    public static NumberAxis newDefaultNumberAxis(String label) {
        return newDefaultValueAxis(NumberAxis.class, label);
    }

    public static <T extends ValueAxis> T newDefaultValueAxis(Class<T> clazz) {
        return newDefaultValueAxis(clazz, null);
    }

    public static <T extends ValueAxis> T newDefaultValueAxis(Class<T> clazz, String label) {
        T axis;
        try {
            axis = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to create " + clazz.getSimpleName() + " instance.", e);
        }

        axis.setAxisLinePaint(DEFAULT_LINE_COLOR); // Y坐标轴颜色
        axis.setTickMarkPaint(DEFAULT_LINE_COLOR); // Y坐标轴标记|竖线颜色
        // Y刻度
        axis.setLabel(label);
        axis.setLabelFont(new PlainHeiti(14));
        axis.setAxisLineVisible(true);
        axis.setTickMarksVisible(true);
        axis.setTickLabelFont(new PlainHeiti(13));

        axis.setUpperMargin(0.1); // 设置顶部Y坐标轴间距,防止数据无法显示
        axis.setLowerMargin(0.1); // 设置底部Y坐标轴间距
        return axis;
    }

    public static LineAndShapeRenderer newDefaultLineRenderer(boolean isShowDataLabels, boolean isShapesVisible) {
        return newDefaultLineAndShapeRenderer(isShowDataLabels, isShapesVisible);
    }

    public static LineAndShapeRenderer newDefaultShapeRenderer(boolean isShowDataLabels, boolean isShapesVisible) {
        return newDefaultLineAndShapeRenderer(isShowDataLabels, isShapesVisible);
    }

    private static LineAndShapeRenderer newDefaultLineAndShapeRenderer(boolean isShowDataLabels,
            boolean isShapesVisible) {
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setDefaultItemLabelFont(new PlainHeiti(12));
        renderer.setDefaultItemLabelPaint(Color.decode("0x999999"));
        renderer.setDefaultStroke(new BasicStroke(1.5F));
        if (isShowDataLabels) {
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                    StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, NumberFormat.getInstance()));
            renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER)); // 位置
        }
        for (int i = 0; i < CHART_COLORS.length; i++) {
            renderer.setSeriesPaint(i, CHART_COLORS[i]);
            renderer.setSeriesOutlinePaint(i, Color.WHITE);
            renderer.setSeriesOutlineStroke(1, new BasicStroke(0.3F));
        }
        renderer.setDefaultShapesVisible(isShapesVisible); // 数据点绘制形状
        return renderer;
    }

    public static BarRenderer newDefaultBarRenderer(boolean isShowDataLabels, boolean isGradient) {
        BarRenderer renderer = new BarRenderer();
        renderer.setDefaultItemLabelFont(new PlainHeiti(12));
        renderer.setDefaultItemLabelPaint(Color.decode("0x999999"));
        renderer.setMaximumBarWidth(0.075); // 设置柱子最大宽度
        renderer.setItemMargin(0.2);
        renderer.setShadowVisible(false);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        if (isGradient) {
            renderer.setBarPainter(new GradientBarPainter());
        } else {
            renderer.setBarPainter(new StandardBarPainter());
        }
        for (int i = 0; i < CHART_COLORS.length; i++) {
            renderer.setSeriesPaint(i, CHART_COLORS[i]);
            renderer.setSeriesOutlinePaint(i, Color.WHITE);
            renderer.setSeriesOutlineStroke(1, new BasicStroke(0.3F));
        }
        ItemLabelPosition position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        renderer.setDefaultPositiveItemLabelPosition(position1);
        ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        renderer.setDefaultNegativeItemLabelPosition(position2);

        renderer.setDefaultItemLabelsVisible(isShowDataLabels);
        return renderer;
    }

    public static XYLineAndShapeRenderer newDefaultTimeSeriesRender(boolean isShowData, boolean isShapesVisible) {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
        renderer.setDefaultShapesVisible(false);
        if (isShowData) {
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
            renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER));
        }
        renderer.setDefaultShapesVisible(isShapesVisible); // 数据点绘制形状

        StandardXYToolTipGenerator xyTooltipGenerator =
                new StandardXYToolTipGenerator("{1}:{2}", new SimpleDateFormat("yyyy-MM-dd"), new DecimalFormat("0"));
        renderer.setDefaultToolTipGenerator(xyTooltipGenerator);
        return renderer;
    }

    /** 时间轴 **/
    public static XYBarRenderer newDefaultTimeSeriesBarRender(boolean isShowDataLabels) {
        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());

        if (isShowDataLabels) {
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
        }

        StandardXYToolTipGenerator xyTooltipGenerator =
                new StandardXYToolTipGenerator("{1}:{2}", new SimpleDateFormat("yyyy-MM-dd"), new DecimalFormat("0"));
        renderer.setDefaultToolTipGenerator(xyTooltipGenerator);
        return renderer;
    }

    /** 饼状图 **/
    public static PiePlot newDefaultPiePlot() {
        PiePlot plot = new PiePlot();
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(0, 0, 0, 0));
        plot.setCircular(true); // 圆形

        // piePlot.setSimpleLabels(true); // 简单标签
        plot.setLabelGap(0.01);
        plot.setInteriorGap(0.05D);
        plot.setLegendItemShape(new Rectangle(10, 10)); // 图例形状
        plot.setIgnoreNullValues(true);
        plot.setLabelBackgroundPaint(null); // 去掉背景色
        plot.setLabelShadowPaint(null); // 去掉阴影
        plot.setLabelOutlinePaint(null); // 去掉边框
        plot.setShadowPaint(null);
        // 0:category 1:value:2 :percentage
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{2}")); // 显示标签数据
        return plot;
    }
}
