package com.gitee.qdbp.tools.excel.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.tools.excel.exception.ResultSetMismatchException;
import com.gitee.qdbp.tools.utils.JsonTools;
import com.gitee.qdbp.tools.utils.StringTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

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
        VerifyTools.requireNotBlank(bean, "bean");
        if (this.content == null) {
            this.content = new ArrayList<>();
        }
        this.content.add(bean);
    }

    public BeanGroup findGroup(String name) {
        VerifyTools.requireNotBlank(name, "name");
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
        return JsonTools.mapToBean(map, type);
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
            temp.add(JsonTools.mapToBean(map, type));
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

    /**
     * 核对结果集
     * 
     * @param actualResult 实际结果集数据
     * @param targetName 目标结果集的名称
     * @throws ResultSetMismatchException 不匹配
     */
    public <T> void compareDatas(List<T> actualResult, String targetName) throws ResultSetMismatchException {
        this.compareDatas(actualResult, targetName, null, null);
    }

    /**
     * 核对结果集
     * 
     * @param actualResult 实际结果集数据
     * @param targetName 目标结果集的名称
     * @param resources 自定义提示消息
     * @throws ResultSetMismatchException 不匹配
     */
    public <T> void compareDatas(List<T> actualResult, String targetName, Map<String, String> resources)
            throws ResultSetMismatchException {
        this.compareDatas(actualResult, targetName, null, resources);
    }

    /**
     * 核对结果集
     * 
     * @param actualResult 实际结果集数据
     * @param targetName 目标结果集的名称
     * @param excludeFields 不需要比较的字段列表
     * @throws ResultSetMismatchException 不匹配
     */
    public <T> void compareDatas(List<T> actualResult, String targetName, List<String> excludeFields)
            throws ResultSetMismatchException {
        this.compareDatas(actualResult, targetName, excludeFields, null);
    }

    /**
     * 核对结果集
     * 
     * @param actualResult 实际结果集数据
     * @param targetName 目标结果集的名称
     * @param excludeFields 不需要比较的字段列表
     * @param resources 自定义提示消息
     * @throws ResultSetMismatchException 不匹配
     */
    public <T> void compareDatas(List<T> actualResult, String targetName, List<String> excludeFields,
            Map<String, String> resources) throws ResultSetMismatchException {
        BeanGroup bean = findGroup(targetName);
        if (bean == null) {
            String msg = newDataNotFoundMessage(targetName, resources);
            throw new ResultSetMismatchException(msg);
        }

        try {
            bean.compareDatasOf(actualResult, excludeFields, resources);
        } catch (ResultSetMismatchException e) {
            e.prependMessage("[" + getName() + "]");
            throw e;
        }
    }

    /**
     * 核对数据列表
     * 
     * @param actualResult 实际数据列表数据
     * @param targetName 目标数据列表的名称
     * @throws ResultSetMismatchException 不匹配
     */
    public <T> void compareValues(List<T> actualResult, String targetName) throws ResultSetMismatchException {
        this.compareValues(actualResult, targetName, null);
    }

    /**
     * 核对数据列表
     * 
     * @param actualResult 实际数据列表数据
     * @param targetName 目标数据列表的名称
     * @param resources 自定义提示消息
     * @throws ResultSetMismatchException 不匹配
     */
    public <T> void compareValues(List<T> actualResult, String targetName, Map<String, String> resources)
            throws ResultSetMismatchException {
        BeanGroup bean = findGroup(targetName);
        if (bean == null) {
            String msg = newDataNotFoundMessage(targetName, resources);
            throw new ResultSetMismatchException(msg);
        }

        try {
            bean.compareValuesOf(actualResult, resources);
        } catch (ResultSetMismatchException e) {
            e.prependMessage("[" + getName() + "]");
            throw e;
        }
    }

    private String newDataNotFoundMessage(String targetName, Map<String, String> resources) {
        String pattern = resources == null ? null : resources.get("data.not.found");
        if (VerifyTools.isBlank(pattern)) {
            pattern = "[{target}] not found in [{container}].";
        }
        return StringTools.format(pattern, "container", getName(), "target", targetName);
    }

}
