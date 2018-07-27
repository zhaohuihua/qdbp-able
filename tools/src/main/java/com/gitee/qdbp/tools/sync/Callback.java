package com.gitee.qdbp.tools.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.tools.utils.ReflectTools;

/**
 * 回调接口<br>
 * <pre>
 * public static interface Sync {
 *    public static Callback&lt;Void&gt; Void = new Callback.Log&lt;&gt;();
 *    public static Callback&lt;Integer&gt; Integer = new Callback.Log&lt;&gt;();
 *    public static Callback&lt;String&gt; String = new Callback.Log&lt;&gt;();
 *    public static Callback&lt;XxxData&gt; XxxData = new Callback.Log&lt;&gt;();
 *    public static Callback&lt;List&lt;XxxData&gt;&gt; XxxList = new Callback.Log&lt;&gt;();
 * }
 * </pre>
 *
 * @author zhaohuihua
 * @version 170918
 */
public interface Callback<T> {

    void done(T result);

    void fail(Object object, String method, Args args, Throwable e);

    public static class Ignore<T> implements Callback<T> {

        @Override
        public void done(T result) {
        }

        @Override
        public void fail(Object object, String method, Args args, Throwable e) {
        }
    }

    public static class Log<T> implements Callback<T> {

        @Override
        public void done(T result) {
        }

        @Override
        public void fail(Object object, String method, Args args, Throwable e) {
            Logger log = LoggerFactory.getLogger(object.getClass());
            String signature = ReflectTools.getMethodLogSignature(object.getClass(), method, args.types());
            log.warn("Sync invoke {} error. {}:{}", signature, e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }
}
