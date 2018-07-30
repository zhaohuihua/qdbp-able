package com.gitee.qdbp.tools.specialized;

import java.io.IOException;
import java.io.StringReader;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;
import org.lionsoul.jcseg.util.ArrayUtil;

public class JcsegTest {

    public static void main(String[] args) throws JcsegException, IOException {
        //创建JcsegTaskConfig分词配置实例，自动查找加载jcseg.properties配置项来初始化
        JcsegTaskConfig config = new JcsegTaskConfig(true);
        // config.setLoadCJKPinyin(true);
        // config.setAppendCJKPinyin(true);
        config.setLoadEntity(true);
        config.setCnNumToArabic(true);

        //创建默认单例词库实现，并且按照config配置加载词库
        ADictionary dic = DictionaryFactory.createDefaultDictionary(config, true);

        String str = "歧义和同义词:研究生命起源，"
                + "混合词: 做B超检查身体，x射线本质是什么，今天去奇都ktv唱卡拉ok去，哆啦a梦是一个动漫中的主角，"
                + "单位和全角: 2009年８月６日开始大学之旅，岳阳今天的气温为38.6℃, 也就是101.48℉, "
                + "中文数字/分数: 你分三十分之二, 小陈拿三十分之五,剩下的三十分之二十三全部是我的，那是一九九八年前的事了，四川麻辣烫很好吃，五四运动留下的五四精神。笔记本五折包邮亏本大甩卖。"
                + "人名识别: 我是陈鑫，也是jcseg的作者，三国时期的诸葛亮是个天才，我们一起给刘翔加油，罗志高兴奋极了因为老吴送了他一台笔记本。"
                + "冰岛时间7月1日，正在当地拍片的汤姆·克鲁斯通过发言人承认，他与第三任妻子凯蒂·赫尔墨斯（第一二任妻子分别为咪咪·罗杰斯、妮可·基德曼）的婚姻即将结束。"
                + "配对标点: 本次『畅想杯』黑客技术大赛的得主为电信09-2BF的张三，奖励C++程序设计语言一书和【畅想网络】的『PHP教程』一套。"
                + "特殊字母: 【Ⅰ】（Ⅱ），"
                + "英文数字: bug report chenxin619315@gmail.com or visit http://code.google.com/p/jcseg, we all admire the hacker spirit!"
                + "特殊数字: ① ⑩ ⑽ ㈩.";
        //依据给定的ADictionary和JcsegTaskConfig来创建ISegment
        //为了Api往后兼容，建议使用SegmentFactory来创建ISegment对象
        ISegment seg = SegmentFactory.createJcseg(JcsegTaskConfig.NLP_MODE,
            new Object[] { new StringReader(str), config, dic });

        //备注：以下代码可以反复调用，seg为非线程安全

        //设置要被分词的文本
        // seg.reset(new StringReader(str));

        //获取分词结果
        StringBuffer sb = new StringBuffer();
        IWord word = null;
        while ((word = seg.next()) != null) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(word.getValue());
            sb.append('\t').append('/');
            sb.append(word.getType());
            if (word.getPartSpeech() != null) {
                sb.append('/');
                sb.append(word.getPartSpeech()[0]);
            }
            if (word.getPinyin() != null) {
                sb.append('/');
                sb.append(word.getPinyin());
            }
            if (word.getEntity() != null) {
                sb.append('/');
                sb.append(ArrayUtil.implode("|", word.getEntity()));
            }
        }
        System.out.println(sb.toString());
    }
}
