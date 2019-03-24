package com.gitee.qdbp.tools.base;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.uncommons.reportng.HTMLReporter;

/**
 * 解决ReportNG输出HTML报告乱码的问题
 *
 * @author zhaohuihua
 * @version 190324
 */
public class Utf8HtmlReporter extends HTMLReporter {

    private static final String ENCODING = "UTF-8";
    private static final String TEMPLATES_PATH = "org/uncommons/reportng/templates/html/";

    /** Generate the specified output file by merging the specified Velocity template with the supplied context. */
    protected void generateFile(File file, String templateName, VelocityContext context) throws Exception {
        OutputStream out = null;
        Writer writer = null;
        try {
            out = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(out, ENCODING));
            Velocity.mergeTemplate(TEMPLATES_PATH + templateName, ENCODING, context, writer);
            writer.flush();
        } finally {
            close(out);
            close(writer);
        }
    }

    protected void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }
}
