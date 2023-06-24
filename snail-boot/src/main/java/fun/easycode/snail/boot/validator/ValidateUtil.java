package fun.easycode.snail.boot.validator;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * 验证工具类
 * @author xuzhe
 */
@Slf4j
public final class ValidateUtil {

    /**
     * 验证对象，对象需要实现{@link IValidate}
     * 支持嵌套验证，子属性也需要实现{@link IValidate}
     * 如果属性为Collection或者Map, 且Collection集合中的对象实现了{@link IValidate},Map value实现了
     * {@link IValidate} 则会继续嵌套验证
     * @param o 需要验证的对象
     */
    public static void validate(Object o) {
        if(o instanceof IValidate){
            ((IValidate) o).validate();
            Field[] fields = o.getClass().getDeclaredFields();
            for(Field field : fields){
                Object value = null;
                field.setAccessible(true);
                try {
                    value = field.get(o);
                } catch (IllegalAccessException e) {
                    log.warn(e.getMessage());
                }
                // 如果取不到值，或者值为空则跳过
                if(value != null) {
                    if (value instanceof Collection) {
                        for (Object itemValue : (Collection) value) {
                            validate(itemValue);
                        }
                    }
                    if (value instanceof Map) {
                        for (Object itemValue : ((Map<?, ?>) value).values()) {
                            validate(itemValue);
                        }
                    } else {
                        validate(value);
                    }
                }
            }
        }
    }
}
