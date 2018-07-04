package com.gitee.zhaohuihua.common.specialized;

import java.io.IOException;
import java.io.StringReader;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;

// autoLoad参数影响分词结果
// autoLoad = true的运行结果：
// 罗志高/nr;兴奋/v;极了/u;因为/c;老吴/nr;送了/v;他/r;一台/m;笔记本/n;。/w
// autoLoad = false的运行结果：
// 罗;志;高;兴奋;极了;因为;老;吴;送了;他;一台;笔记本;。/w
public class JcsegTest2 {

    public static void main(String[] args) throws JcsegException, IOException {
        run(true);
        run(false);
    }

    public static void run(boolean autoLoad) throws JcsegException, IOException {
        JcsegTaskConfig config = new JcsegTaskConfig(autoLoad);

        ADictionary dic = DictionaryFactory.createDefaultDictionary(config, true);

        String str = "罗志高兴奋极了因为老吴送了他一台笔记本。";
        ISegment seg = SegmentFactory.createJcseg(JcsegTaskConfig.NLP_MODE,
            new Object[] { new StringReader(str), config, dic });

        StringBuffer sb = new StringBuffer();
        for (IWord word = null; (word = seg.next()) != null;) {
            if (sb.length() > 0) {
                sb.append(';').append(' ');
            }
            sb.append(word.getValue());
            if (word.getPartSpeech() != null) {
                sb.append('/').append(word.getPartSpeech()[0]);
            }
        }
        System.out.println(sb.toString());
    }
}
