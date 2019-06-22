package com.gitee.qdbp.tools.instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.tools.sync.SyncArgs;
import com.gitee.qdbp.tools.sync.SyncCallback;
import com.gitee.qdbp.tools.utils.ReflectTools;

/**
 * 只记录警告日志的回调处理类
 *
 * @author zhaohuihua
 * @version 190622
 */
public class SyncLogCallback<T> implements SyncCallback<T> {

    @Override
    public void done(T result) {
    }

    @Override
    public void fail(Object object, String method, SyncArgs args, Throwable e) {
        Logger log = LoggerFactory.getLogger(object.getClass());
        String signature = ReflectTools.getMethodLogSignature(object.getClass(), method, args.types());
        log.warn("Sync invoke {} error. {}:{}", signature, e.getClass().getSimpleName(), e.getMessage(), e);
    }
}
