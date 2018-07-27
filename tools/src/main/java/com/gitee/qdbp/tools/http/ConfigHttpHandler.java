package com.gitee.qdbp.tools.http;

import com.gitee.qdbp.tools.utils.Config;

public abstract class ConfigHttpHandler extends BaseHttpHandler implements Config.Aware {

    protected Config config;

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

}
