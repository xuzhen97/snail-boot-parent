package fun.easycode.snail.boot.core;

import java.lang.annotation.*;

/**
 * 别名注解，在查询的时候转义和数据库字段的区别
 * @author xuzhe
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Alias {
    String value() default "";
}