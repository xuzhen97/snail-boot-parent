package fun.easycode.snail.boot.datafill;

import java.lang.annotation.*;

/**
 * 用来标识feign请求返回对象中哪个字段是id
 * @author xuzhe
 */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignId {
}
