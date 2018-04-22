package com.gitee.zhaohuihua.tools.cache;

/**
 * 强类型缓存KEY
 *
 * <pre>
public interface CacheKeys {
    enum SKV implements ICacheKeys.KeyValue&lt;String&gt; {
        CLIENT_CODE,
        BUSINESS_CODE;
        public Class&lt;String&gt; type() { return String.class; }
    }
    enum IKV implements ISessionKey.KeyValue&lt;Integer&gt; {
        DATA_VALUE;
        public Class&lt;Integer&gt; type() { return Integer.class; }
    }
    enum ResourceList implements ICacheKeys.KeyList&lt;ResourceBean&gt; {
        PERMISSION;
        public Class&lt;ResourceBean&gt; type() { return ResourceBean.class; }
    }
}
cacheService.set(CacheKeys.SKV.CLIENT_CODE, 0); // 编译报错
cacheService.set(CacheKeys.IKV.DATA_VALUE, "000"); // 编译报错
String businessCode = cacheService.get(CacheKeys.SKV.BUSINESS_CODE); // 类型自动转换
List&lt;ResourceBean&gt; resources = cacheService.get(CacheKeys.ResourceList.PERMISSION); // 类型自动转换
 * </pre>
 *
 * @author zhaohuihua
 * @version 170527
 * @since v1.0
 */
public interface ICacheKeys<T> {

    String name();

    Class<T> type();

    /** 只允许缓存指定类型的值的KEY **/
    interface KeyValue<T> extends ICacheKeys<T> {
    }

    /** 只允许缓存指定类型的数组的KEY **/
    interface KeyList<T> extends ICacheKeys<T> {
    }

    /** 只允许缓存指定类型的值的动态KEY **/
    interface FieldValue<T> extends ICacheKeys<T> {
    }

    /** 只允许缓存指定类型的数组的动态KEY **/
    interface FieldList<T> extends ICacheKeys<T> {
    }

    /** 具有明确过期时间的KEY **/
    interface ExpireFixed {

        /** 过期时间 **/
        long time();
    }
}
