package fun.easycode.snail.boot.validator;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

import fun.easycode.snail.boot.core.CheckException;

/**
 * dto 接口，如果参数验证失败将抛出{@link CheckException}异常
 *
 * @author xuzhen97
 */
public interface IValidate {

    Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 验证DTO参数.
     *
     * @throws ConstraintViolationException 验证不通过抛出异常
     */
    default void validate() throws ConstraintViolationException {
        Set<ConstraintViolation<IValidate>> violations = VALIDATOR.validate(this);
        if (violations != null && !violations.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();
            for (ConstraintViolation<IValidate> error : violations) {
                // 如果错误信息和模板信息一致，说明是自定义的错误信息，不需要拼接字段名
                if(Objects.equals(error.getMessage(), error.getMessageTemplate())){
                    errorMsg.append(error.getMessage())
                            .append(";");
                }else{
                    errorMsg.append(error.getPropertyPath())
                            .append(":")
                            .append(error.getMessage())
                            .append(";");
                }
            }
            throw new CheckException(errorMsg.toString());
        }
    }
}