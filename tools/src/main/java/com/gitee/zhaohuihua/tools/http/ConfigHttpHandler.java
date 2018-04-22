package com.gitee.zhaohuihua.tools.http;

import com.gitee.zhaohuihua.tools.utils.Config;

public abstract class ConfigHttpHandler implements IHttpHandler, Config.Aware {

    protected Config config;

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

}
