package com.gitee.qdbp.tools.sync;

/**
 * 回调接口<br>
 * <pre>
 * public static interface SyncIgnoreCallback {
 *    public static Callback&lt;Void&gt; VoidCallback = new Callback.Ignore&lt;&gt;();
 *    public static Callback&lt;Integer&gt; IntegerCallback = new Callback.Ignore&lt;&gt;();
 *    public static Callback&lt;String&gt; StringCallback = new Callback.Ignore&lt;&gt;();
 *    public static Callback&lt;XxxData&gt; XxxDataCallback = new Callback.Ignore&lt;&gt;();
 *    public static Callback&lt;List&lt;XxxData&gt;&gt; XxxListCallback = new Callback.Ignore&lt;&gt;();
 * }
 * </pre> <pre>
 * import com.gitee.qdbp.tools.defaults.SyncLogCallback;
 * public static interface SyncLogHandler {
 *    public static Callback&lt;Void&gt; VoidCallback = new SyncLogCallback&lt;&gt;();
 *    public static Callback&lt;Integer&gt; IntegerCallback = new SyncLogCallback&lt;&gt;();
 *    public static Callback&lt;String&gt; StringCallback = new SyncLogCallback&lt;&gt;();
 *    public static Callback&lt;XxxData&gt; XxxDataCallback = new SyncLogCallback&lt;&gt;();
 *    public static Callback&lt;List&lt;XxxData&gt;&gt; XxxListCallback = new SyncLogCallback&lt;&gt;();
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
