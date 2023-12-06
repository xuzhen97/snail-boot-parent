package fun.easycode.snail.boot.datafill;

import java.lang.annotation.*;

/**
 * 级联查询的时候对于id生成空对象的策略
 * @author xuzhe
 */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataGenerate {
    /**
     * 实体的主键名, 默认id
     * @return id字段名
     */
    String value();

    /**
     * 目标
     * @return
     */
    String targetField();

    /**
     * 如果是多对象模式集合下的的属性类型Class
     * @return
     */
    Class<?> listClass() default void.class;

    /**
     * 多对象模式集合下的主键分隔符默认,
     * @return
     */
    String listSeparator() default ",";

    /**
     * id生成的模式
     * @return
     */
    IDGenerateMode mode() default IDGenerateMode.DEFAULT;
}
