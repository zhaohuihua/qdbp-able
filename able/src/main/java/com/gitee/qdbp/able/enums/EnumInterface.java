package com.gitee.qdbp.able.enums;

/**
 * 枚举接口<br>
 * 某些枚举, 其枚举值随着项目的不同而有差异<br>
 * 但基础项目定义的类不能因业务项目的差异化而修改代码<br>
 * 因此在基础项目中将枚举改为接口, 由各业务项目提供实际的枚举类
 *
 * @author zhaohuihua
 * @version 20200202
 */
public interface EnumInterface {

    int ordinal();

    String name();
}
