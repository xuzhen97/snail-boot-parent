package fun.easycode.snail.boot.datafill;

import java.lang.annotation.*;

/**
 * @author xuzhe
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.ANNOTATION_TYPE})
public @interface DataParam {
    /**
     * 方法的参数名称
     */
    String name();

    /**
     * 方法参数传参值，支持spEl表达式
     */
    String value();
}
