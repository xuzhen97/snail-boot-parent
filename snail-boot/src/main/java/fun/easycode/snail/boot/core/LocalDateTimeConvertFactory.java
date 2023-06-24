package fun.easycode.snail.boot.core;

import cn.hutool.core.date.LocalDateTimeUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class LocalDateTimeConvertFactory  implements ConverterFactory<String, LocalDateTime> {
    @Override
    public <T extends LocalDateTime> Converter<String, T> getConverter(Class<T> aClass) {
        return val -> StringUtils.isEmpty(val)? null : (T) LocalDateTimeUtil.of(Long.valueOf(val));
    }
}
