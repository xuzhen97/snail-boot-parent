package fun.easycode.snail.boot.core;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Objects;

/**
 * enumerator spring 的枚举转换器
 * 通过传入的value对比枚举中实例的value, 返回对应的枚举。
 * @author xuzhe
 */
@SuppressWarnings("all")
public class EnumeratorConvertFactory implements ConverterFactory<String, Enumerator> {
    @Override
    public <T extends Enumerator> Converter<String, T> getConverter(Class<T> targetType) {
        return new ObjectToEnum<>(targetType);
    }
    @SuppressWarnings("all")
    private static class ObjectToEnum<T extends Enumerator> implements Converter<String, T>{

        private Class<T> targetType;

        public ObjectToEnum(Class<T> targetType){
            this.targetType = targetType;
        }

        @Override
        public T convert(String source) {
            return (T) EnumeratorConvertFactory.getIEnum(this.targetType, source);
        }
    }

    public static <T extends Enumerator> Object getIEnum(Class<T> targerType, String source) {
        for (T enumObj : targerType.getEnumConstants()) {
            if (Objects.equals(source, String.valueOf(enumObj.getValue()))) {
                return enumObj;
            }
        }
        return null;
    }
}
