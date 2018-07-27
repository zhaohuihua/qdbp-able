package com.gitee.zhaohuihua.tools.specialized;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;
import com.gitee.zhaohuihua.core.utils.StringTools;
import com.gitee.zhaohuihua.core.utils.VerifyTools;
import com.gitee.zhaohuihua.tools.utils.ConvertTools;

/**
 * 关键字收集
 *
 * @author zhaohuihua
 * @version 180626
 */
public class KeywordHandler {

    private Set<String> container = new TreeSet<>();

    private static JcsegTaskConfig CONFIG = new JcsegTaskConfig(true);
    private static ADictionary DICTIONARY = DictionaryFactory.createDefaultDictionary(CONFIG, true);

    protected KeywordHandler() {
    }

    public static KeywordHandler newInstance() {
        return new KeywordHandler();
    }

    /** 增加普通关键字(原文,不分词) **/
    public void addPlain(String... keywords) {
        if (VerifyTools.isBlank(keywords)) {
            return;
        }
        for (String string : keywords) {
            if (VerifyTools.isNotBlank(string)) {
                container.add(string);
            }
        }
    }

    /** 增加文本关键字(中文分词) **/
    public void addText(String... keywords) {
        if (VerifyTools.isBlank(keywords)) {
            return;
        }
        for (String string : keywords) {
            if (VerifyTools.isNotBlank(string)) {
                container.addAll(doSegmentate(string));
            }
        }
    }

    /** 增加已经分词的关键字 **/
    public void addSegment(String... keywords) {
        if (VerifyTools.isBlank(keywords)) {
            return;
        }
        for (String string : keywords) {
            if (VerifyTools.isNotBlank(string)) {
                String[] segments = StringTools.split(string, '\t', ' ', '\r', '\n');
                for (String segment : segments) {
                    if (VerifyTools.isNotBlank(segment)) {
                        container.add(segment);
                    }
                }
            }
        }
    }

    private static ISegment newSegment(String string) {
        ISegment segment;
        try {
            int mode = JcsegTaskConfig.NLP_MODE;
            Reader reader = new StringReader(string);
            segment = SegmentFactory.createJcseg(mode, new Object[] { reader, CONFIG, DICTIONARY });
        } catch (JcsegException e) {
            throw new IllegalStateException("JcsegSegmentInitError", e);
        }

        return segment;
    }

    private static List<String> doSegmentate(String string) {
        if (VerifyTools.isBlank(string)) {
            return null;
        }

        ISegment segment = newSegment(string);

        List<String> buffer = new ArrayList<>();
        try {
            for (IWord word = null; (word = segment.next()) != null;) {
                if (word.getType() != IWord.T_PUNCTUATION) {
                    buffer.add(word.getValue());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("JcsegSegmentReadError", e);
        }

        return buffer.isEmpty() ? null : buffer;
    }

    public static String[] segmentate(String string) {
        List<String> buffer = doSegmentate(string);
        return buffer.isEmpty() ? null : ConvertTools.toArray(buffer, String.class);
    }

    public static String format(String string) {
        List<String> list = doSegmentate(string);
        return VerifyTools.isBlank(list) ? null : ConvertTools.joinToString(list, " ");
    }

    public String toString() {
        return VerifyTools.isBlank(container) ? null : ConvertTools.joinToString(container, " ");
    }

}
