package fun.easycode.snail.boot.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 多传入枚举验证
 * @author xuzhe
 */
@Documented
@Constraint(validatedBy = ManyEnumExactValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyEnumExact {
    String message() default "{ManyEnum}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum> enumClass() default Enum.class;
}
