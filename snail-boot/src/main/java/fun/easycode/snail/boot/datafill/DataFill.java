package fun.easycode.snail.boot.datafill;

import java.lang.annotation.*;

/**
 * @author xuzhe
 */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFill {
    /**
     * 实体的主键名, 默认id
     * @return id字段名
     */
    String value() default "id";

    /**
     * 注解对应处理器
     * @return 对应的处理器
     */
    Class<?> source();

    /**
     * spring el表达式
     * 如果我们自定义表达式
     * 当DataFill所在基本数据类型的时候spEl表达式的值是最终填充的值
     * 所在属性是对象，或者是list的时候，表达式不取返回值
     * 我们可以在Mapstruct转换器中自定义处理逻辑
     * 固定会传入target对象和source对象，target对象就是当前属性的值
     * List的话就是其中的对象，source是id查出的对应实体
     * @return 我们自定义的逻辑
     */
    String spEl() default "";

    /**
     * 自定义传参
     * DataParam value支持el表达式
     * @return 参数列表
     */
    DataParam[] params() default {};

    /**
     * 自定义调用方法名，目前仅对feign client生效
     * 如果不指定方法名，那么在feign client的指定中，client只能存在一个方法
     * @return 方法名
     */
    String methodName() default "";

    /**
     * 是否自定义mybatis plus id注解
     * @Author: xuzhen97
     * @Date:  2023/03/17
     */
    boolean isCustomMyBatisPlusAnno() default false;
}
