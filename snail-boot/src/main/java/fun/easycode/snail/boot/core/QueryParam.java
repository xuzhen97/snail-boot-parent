package fun.easycode.snail.boot.core;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryParam {
    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name") String value() default "";
    /**
     * The name of the request parameter to bind to.
     *
     * @since 4.2
     */
    @AliasFor("value") String name() default "";
    Class root();
}