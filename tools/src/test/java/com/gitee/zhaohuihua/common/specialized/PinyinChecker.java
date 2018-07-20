package com.gitee.zhaohuihua.common.specialized;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.zhaohuihua.core.utils.StringTools;
import com.gitee.zhaohuihua.core.utils.VerifyTools;
import com.gitee.zhaohuihua.tools.files.FileTools;
import com.gitee.zhaohuihua.tools.files.PathTools;
import com.gitee.zhaohuihua.tools.http.BaseHttpHandler;
import com.gitee.zhaohuihua.tools.http.HttpException;
import com.gitee.zhaohuihua.tools.http.HttpTools;
import com.gitee.zhaohuihua.tools.http.HttpTools.HttpFormImpl;

public class PinyinChecker {

    private static Logger log = LoggerFactory.getLogger(PinyinChecker.class);

    private static Pattern SEPARATOR = Pattern.compile("/");

    public static void main(String[] args) throws IOException {
        // System.out.println(new PinyinChecker().findPinyinByBaiduHanyu("觉"));
        new PinyinChecker().check("lex-chars.lex");
    }

    public PinyinChecker() {
        this.initYinbiao();
        this.initPinyin();
    }

    public void check(String filePath) throws IOException {
        URL input = PathTools.findClassResource(PinyinChecker.class, filePath);
        // 准备输出文件
        String outputFolder = PathTools.getOutputFolder(input, "./");
        String newFileName = PathTools.replaceExtension(filePath, "new");
        String outputPath = PathTools.concat(outputFolder, newFileName);
        log.debug("output file --> {}", outputPath);
        File outputFile = new File(outputPath);
        FileTools.mkdirsIfNotExists(new File(outputFolder));

        // 逐行读取文件
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input.openStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("/") || line.trim().startsWith("#")) {
                    writeLine(writer, line);
                    continue;
                }
                String[] items = SEPARATOR.split(line);
                if (items.length < 3) {
                    writeLine(writer, line);
                    continue;
                }
                String word = items[0]; // 汉字
                String pinyin = items[2]; // 拼音
                if (VerifyTools.isBlank(pinyin)) {
                    writeLine(writer, line);
                    continue;
                }
                String realPinyin;
                if (this.pinyin.containsKey(word)) {
                    realPinyin = this.pinyin.get(word);
                } else {
                    realPinyin = findPinyinByBaiduHanyu(word); // 查询正确的拼音
                    realPinyin = replaceYinBiao(realPinyin);
                }

                if (realPinyin == null || pinyin.equals(realPinyin)) {
                    writeLine(writer, line);
                } else {
                    items[2] = realPinyin;
                    String string = StringTools.concat('/', items);
                    writeLine(writer, string);
                    if (!pinyin.equals("%")) {
                        log.debug("{}: {} -- {}", word, pinyin, realPinyin);
                    }
                }
            }

        }
    }

    private void writeLine(BufferedWriter writer, String string) throws IOException {
        if (string.length() > 0) {
            writer.write(string);
        }
        writer.newLine();
    }

    private String replaceYinBiao(String pinyin) {
        if (VerifyTools.isBlank(pinyin)) {
            return pinyin;
        }
        char[] chars = pinyin.toCharArray();
        boolean changed = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String key = String.valueOf(c);
            if (yinbiao.containsKey(key)) {
                chars[i] = yinbiao.get(key).charAt(0);
                changed = true;
            }
        }
        return changed ? new String(chars) : pinyin;
    }

    // <script>s5f("lü4");</script>
    private static Pattern FINDER_ZIDIAN = Pattern.compile("<script>s5f\\(\"([^\"]+?)\\d*\"\\);</script>");

    // zidian.51240.com: 此网站某些多音字常用的拼音未排在最前面
    protected String findPinyinByZidian(String word) {
        String code;
        try {
            code = URLEncoder.encode(word, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        String html;
        try {
            // https://zidian.51240.com/e5b1a1__zidianchaxun/
            code = code.replaceAll("%", "").toLowerCase();
            html = HttpTools.form.get("https://zidian.51240.com/" + code + "__zidianchaxun/");
        } catch (HttpException e) {
            log.error("Failed to find pinyin --> {}", word, e);
            return null;
        }
        Matcher matcher = FINDER_ZIDIAN.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        } else {
            return null;
        }
    }

    // <span>[lǚ]</span>
    private static Pattern FINDER_ICIBA = Pattern.compile("<span>\\[([^\\[\\]]+)\\]</span>");

    // www.iciba.com: 金山词霸, 此网站很多字没有拼音
    protected String findPinyinByIciba(String word) {
        String code;
        try {
            code = URLEncoder.encode(word, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        String html;
        try {
            // http://www.iciba.com/%E5%B1%A1
            html = HttpTools.form.get("http://www.iciba.com/" + code);
        } catch (HttpException e) {
            log.error("Failed to find pinyin --> {}", word, e);
            return null;
        }
        Matcher matcher = FINDER_ICIBA.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        } else {
            return null;
        }
    }

    // <div class="pronounce" id="pinyin">
    //     <span> <b>jué</b> <a herf="#" url="http://appcdn.fanyi.baidu.com/zhdict/mp3/jue2.mp3" class="mp3-play">&nbsp;</a> </span>
    //     <span> <b>jiào</b> <a herf="#" url="http://appcdn.fanyi.baidu.com/zhdict/mp3/jiao4.mp3" class="mp3-play">&nbsp;</a> </span>
    // </div>
    private static Pattern FINDER_BAIDU_HANYU =
            Pattern.compile("<div[^<>]+id=\"pinyin\">\\s*<span>\\s*<b>([^<>]+)</b>");
    private static HttpTools BAIDU_HTTP_TOOLS = new HttpFormImpl(new BaiduHttpHandler());

    // hanyu.baidu.com: 百度汉语
    protected String findPinyinByBaiduHanyu(String word) {
        // String code = Integer.toString(word.charAt(0), 16).toUpperCase();
        // System.out.println(code);
        String code;
        try {
            code = URLEncoder.encode(word, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        String html;
        try {
            // https://hanyu.baidu.com/s?device=pc&from=home&wd=%E5%B1%A1
            html = BAIDU_HTTP_TOOLS.get("https://hanyu.baidu.com/s?device=pc&from=home&wd=" + code);
        } catch (HttpException e) {
            log.error("Failed to find pinyin --> {}", word, e);
            return null;
        }
        Matcher matcher = FINDER_BAIDU_HANYU.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        } else {
            return null;
        }
    }

    private Map<String, String> yinbiao = new HashMap<>();
    private Map<String, String> pinyin = new HashMap<>();

    private void initYinbiao() {
        yinbiao.put("ḿ", "m"); // 呣
        yinbiao.put("ń", "n"); // 嗯
        yinbiao.put("ň", "n");
        yinbiao.put("ǹ", "n");
        yinbiao.put("ā", "a");
        yinbiao.put("á", "a");
        yinbiao.put("ǎ", "a");
        yinbiao.put("à", "a");
        yinbiao.put("ē", "e");
        yinbiao.put("é", "e");
        yinbiao.put("ě", "e");
        yinbiao.put("è", "e");
        yinbiao.put("ê", "e");
        yinbiao.put("ī", "i");
        yinbiao.put("í", "i");
        yinbiao.put("ǐ", "i");
        yinbiao.put("ì", "i");
        yinbiao.put("ō", "o");
        yinbiao.put("ó", "o");
        yinbiao.put("ǒ", "o");
        yinbiao.put("ò", "o");
        yinbiao.put("ū", "u");
        yinbiao.put("ú", "u");
        yinbiao.put("ǔ", "u");
        yinbiao.put("ù", "u");
        yinbiao.put("ü", "u:");
        yinbiao.put("ǖ", "u:");
        yinbiao.put("ǘ", "u:");
        yinbiao.put("ǚ", "u:");
        yinbiao.put("ǜ", "u:");
    }

    private void initPinyin() {
        // 这是hanyu.baidu.com上拼音不合理的名单

        // 常用字拼音优先
        pinyin.put("查", "cha"); // zha
        pinyin.put("爪", "zhua"); // zhao
        pinyin.put("胳", "ge"); // ga
        pinyin.put("无", "wu"); // mo
        pinyin.put("什", "shen"); // shi
        pinyin.put("瀑", "pu"); // bao
        pinyin.put("育", "yu"); // yo
        pinyin.put("膀", "pang"); // bang
        pinyin.put("脯", "pu"); // fu
        pinyin.put("铛", "dang"); // cheng
        pinyin.put("莘", "xin"); // shen
        pinyin.put("牟", "mou"); // mu
        pinyin.put("落", "luo"); // la
        pinyin.put("戌", "xu"); // qu
        pinyin.put("折", "zhe"); // she
        pinyin.put("挟", "xie"); // jia
        pinyin.put("挲", "suo"); // sa
        pinyin.put("呔", "tai"); // dai
        pinyin.put("呗", "bei"); // bai
        pinyin.put("囤", "tun"); // dun
        pinyin.put("谜", "mi"); // mei
        pinyin.put("埏", "yan"); // shan
        pinyin.put("谥", "yi"); // shi
        pinyin.put("樘", "tang"); // cheng
        pinyin.put("辟", "pi"); // bi
        pinyin.put("伯", "bo"); // bai
        pinyin.put("侥", "jiao"); // yao
        pinyin.put("玟", "wen"); // min
        pinyin.put("歙", "she"); // xi

        // 多个常用拼音
        pinyin.put("调", "tiao"); // diao
        pinyin.put("扒", "pa"); // ba
        pinyin.put("粘", "zhan"); // nian

        // 不常用字
        pinyin.put("峇", "ke"); // ba
        pinyin.put("梣", "chen"); // qin
        pinyin.put("阏", "yan"); // e
        pinyin.put("圩", "wei"); // xu
        pinyin.put("妳", "ni"); // nai
        pinyin.put("玢", "fen"); // bin
    }

    private static class BaiduHttpHandler extends BaseHttpHandler {

        public BaiduHttpHandler() {
            // 百度有限制UserAgent, Java默认的UserAgent总是报页面不存在
            this.addHeader("Referer", "https://hanyu.baidu.com/");
            this.addHeader("User-Agent", "Mozilla/5.0 AppleWebKit/537.36 Chrome/59.0.3071.86 Safari/537.36");
        }
    }
}
