package fun.easycode.snail.boot.datafill;

import java.lang.annotation.*;

/**
 * 减少不必要的对象扫描
 * @author xuzhe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnableDataFill {
}
