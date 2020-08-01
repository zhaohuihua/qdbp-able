package com.gitee.qdbp.able.jdbc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 默认值注解<br>
 * &#064;ColumnDefault("1.00");<br>
 * &#064;ColumnDefault("CURRENT_TIMESTAMP");<br>
 * &#064;ColumnDefault("'N/A'"); // 字段串要用单引号括起来<br>
 *
 * @author zhaohuihua
 * @version 20200708
 * @since 5.0
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnDefault {

    /** 默认值(字段串要用单引号括起来) **/
    String value();
}
