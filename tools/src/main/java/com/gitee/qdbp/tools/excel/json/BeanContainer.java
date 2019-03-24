package com.gitee.qdbp.tools.excel.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.utils.VerifyTools;

/**
 * Bean容器
 *
 * @author zhaohuihua
 * @version 190316
 */
public class BeanContainer implements Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 容器名称 **/
    private String name;
    /** 容器内容 **/
    private List<BeanGroup> content;

    /** 获取容器名称 **/
    public String getName() {
        return name;
    }

    /** 设置容器名称 **/
    public void setName(String name) {
        this.name = name;
    }

    /** 获取容器内容 **/
    public List<BeanGroup> getContent() {
        return this.content != null ? this.content : new ArrayList<BeanGroup>();
    }

    public void addItem(BeanGroup bean) {
        Objects.requireNonNull(bean, "bean");
        if (this.content == null) {
            this.content = new ArrayList<>();
        }
        this.content.add(bean);
    }

    public BeanGroup findGroup(String name) {
        Objects.requireNonNull(name, "name");
        if (this.content == null) {
            return null;
        }
        for (BeanGroup bean : content) {
            if (name.equals(bean.getName()) || name.equals(bean.getAlias())) {
                return bean;
            }
        }
        return null;
    }

    public Map<String, Object> getData(String name) {
        BeanGroup bean = findGroup(name);
        if (bean == null) {
            return null;
        } else {
            return bean.findFistData();
        }
    }

    public List<Map<String, Object>> getDatas(String name) {
        BeanGroup bean = findGroup(name);
        if (bean == null) {
            return null;
        } else {
            return bean.getDatas();
        }
    }

    public <T> T getBean(String name, Class<T> type) {
        BeanGroup bean = findGroup(name);
        if (bean == null) {
            return null;
        }

        Map<String, Object> map = bean.findFistData();
        return TypeUtils.castToJavaBean(map, type);
    }

    public <T> List<T> getBeans(String name, Class<T> type) {
        BeanGroup bean = findGroup(name);
        if (bean == null) {
            return null;
        }

        List<Map<String, Object>> list = bean.getDatas();
        if (VerifyTools.isBlank(list)) {
            return null;
        }

        List<T> temp = new ArrayList<>();
        for (Map<String, Object> map : list) {
            temp.add(TypeUtils.castToJavaBean(map, type));
        }
        return temp;
    }

    public List<Object> getValues(String name) {
        BeanGroup bean = findGroup(name);
        if (bean == null) {
            return null;
        } else {
            return bean.getValues();
        }
    }

    public <T> List<T> getValues(String name, Class<T> type) {
        BeanGroup bean = findGroup(name);
        if (bean == null) {
            return null;
        }

        List<Object> list = bean.getValues();
        if (VerifyTools.isBlank(list)) {
            return null;
        }

        List<T> temp = new ArrayList<>();
        for (Object object : list) {
            temp.add(TypeUtils.castToJavaBean(object, type));
        }
        return temp;
    }

}
