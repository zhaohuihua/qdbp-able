package com.gitee.qdbp.tools.sync;


/**
 * 异步执行方法
 *
 * @author zhaohuihua
 * @version 170526
 */
public class SyncRunner<T> extends SyncInvoker<T, Object> {

    public SyncRunner(T target, String method, SyncArgs args) {
        super(target, method, args, new SyncCallback.Ignore<Object>());
    }
}
