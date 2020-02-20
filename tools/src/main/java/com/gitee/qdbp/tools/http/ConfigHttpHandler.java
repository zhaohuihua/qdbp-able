package com.gitee.qdbp.tools.http;

import com.gitee.qdbp.tools.property.PropertyContainer;

public abstract class ConfigHttpHandler extends BaseHttpHandler implements PropertyContainer.Aware {

    protected PropertyContainer config;

    @Override
    public void setPropertyContainer(PropertyContainer config) {
        this.config = config;
    }

}
