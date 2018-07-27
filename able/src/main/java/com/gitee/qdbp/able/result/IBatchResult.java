package com.gitee.zhaohuihua.core.result;

import java.util.List;

/**
 * 批量操作结果
 *
 * @author zhaohuihua
 * @version 150305
 */
public interface IBatchResult {

    /**
     * 失败记录
     *
     * @author zhaohuihua
     * @version 160305
     */
    interface Failed extends IResultMessage {

        /**
         * 获取序号
         *
         * @return 序号
         */
        Integer getIndex();
    }

    /**
     * 获取总数
     *
     * @return 总数
     */
    Integer getTotal();

    /**
     * 获取失败列表
     *
     * @return 失败列表
     */
    List<Failed> getFailed();
}
