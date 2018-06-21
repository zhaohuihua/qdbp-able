package com.gitee.zhaohuihua.tools.sync;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.gitee.zhaohuihua.tools.utils.ReflectTools;

/**
 * 异步执行方法, 带结果回调<br>
 * Callback&lt;String&gt; callback = new Callback.Log<>();<br>
 * Args args = new AnyArgs(arg1, arg2);<br>
 * new SyncInvoker&lt;XxxService, String&gt;(xxxService, "methood", args, callback).start();<br>
 *
 * @author zhaohuihua
 * @version 170526
 */
public class SyncInvoker<T, R> extends Thread {

    protected T target;
    protected String method;
    protected Args args;
    protected Callback<? super R> callback;

    public SyncInvoker(T target, String method, Args args, Callback<? super R> callback) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.callback = callback;
    }

    public void run() {
        Class<?>[] types = args.types();
        Object[] values = args.values();
        try {
            Method method = ReflectTools.findMethod(target.getClass(), this.method, types);
            Object result = method.invoke(target, values);
            if (callback != null) {
                @SuppressWarnings("unchecked")
                R r = (R) result;
                callback.done(r);
            }
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) { // method.invoke抛出未捕获的异常由该异常包装
                e = e.getCause();
            }

            if (callback != null) {
                callback.fail(target, method, args, e);
            }
        }
    }
}
