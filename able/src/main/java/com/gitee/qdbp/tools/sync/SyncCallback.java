package com.gitee.qdbp.tools.sync;

/**
 * 回调接口<br>
 * <pre>
 * public static interface SyncIgnoreCallback {
 *    public static Callback&lt;Void&gt; Void = new Callback.Ignore&lt;&gt;();
 *    public static Callback&lt;Integer&gt; Integer = new Callback.Ignore&lt;&gt;();
 *    public static Callback&lt;String&gt; String = new Callback.Ignore&lt;&gt;();
 *    public static Callback&lt;XxxData&gt; XxxData = new Callback.Ignore&lt;&gt;();
 *    public static Callback&lt;List&lt;XxxData&gt;&gt; XxxList = new Callback.Ignore&lt;&gt;();
 * }
 * </pre> <pre>
 * import com.gitee.qdbp.tools.defaults.SyncLogCallback;
 * public static interface SyncLogHandler {
 *    public static Callback&lt;Void&gt; Void = new SyncLogCallback&lt;&gt;();
 *    public static Callback&lt;Integer&gt; Integer = new SyncLogCallback&lt;&gt;();
 *    public static Callback&lt;String&gt; String = new SyncLogCallback&lt;&gt;();
 *    public static Callback&lt;XxxData&gt; XxxData = new SyncLogCallback&lt;&gt;();
 *    public static Callback&lt;List&lt;XxxData&gt;&gt; XxxList = new SyncLogCallback&lt;&gt;();
 * }
 * </pre>
 *
 * @author zhaohuihua
 * @version 170918
 */
public interface SyncCallback<T> {

    void done(T result);

    void fail(Object object, String method, SyncArgs args, Throwable e);

    public static class Ignore<T> implements SyncCallback<T> {

        @Override
        public void done(T result) {
        }

        @Override
        public void fail(Object object, String method, SyncArgs args, Throwable e) {
        }
    }
}
