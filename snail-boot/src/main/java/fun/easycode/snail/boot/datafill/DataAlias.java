package fun.easycode.snail.boot.datafill;

import java.lang.annotation.*;

/**
 * @author xuzhe
 */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataAlias {
    /**
     * 实体的主键名, 默认id
     */
    String value();
}
