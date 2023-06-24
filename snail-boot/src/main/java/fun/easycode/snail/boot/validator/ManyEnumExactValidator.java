package fun.easycode.snail.boot.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ManyEnumExactValidator implements ConstraintValidator<ManyEnumExact, String> {

    private Class<? extends Enum> enClass = null;

    @Override
    public void initialize(ManyEnumExact constraintAnnotation) {
        enClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String manyEnumStr, ConstraintValidatorContext constraintValidatorContext) {
        //不验证为null的情况
        if (manyEnumStr == null || enClass == null) {
            return true;
        }

        try {
            String[] enumStrs = manyEnumStr.split(",");
            Method method = enClass.getDeclaredMethod("values");
            Enum[] elements = (Enum[]) method.invoke(null, null);

            for (String enumStr : enumStrs) {
                Optional<Enum> element = Arrays.stream(elements)
                        .filter(el -> Objects.equals(el.toString(), enumStr)).findFirst();
                if (!element.isPresent()) {
                    return false;
                }
            }
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return false;
    }
}
