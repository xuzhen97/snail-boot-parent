package fun.easycode.snail.boot.autoconfigure;

import fun.easycode.snail.boot.core.*;
import fun.easycode.snail.boot.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * web应用才生效
 * @author xuzhen97
 */
@ConditionalOnWebApplication
@Configuration
@Slf4j
public class SnailBootWebAutoConfiguration implements WebMvcConfigurer, Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new QueryHandlerMethodArgumentResolver());
    }

    /**
     * mvc枚举转换器
     * @return EnumeratorConvertFactory
     */
    @Bean
    public EnumeratorConvertFactory enumeratorConvertFactory(){
        return new EnumeratorConvertFactory();
    }

    /**
     * mvc LocalDateTime转换器 前台时间戳直接转LocalDateTime
     * @return LocalDateTimeConvertFactory
     */
    @Bean
    public LocalDateTimeConvertFactory localDateTimeConvertFactory(){
        return new LocalDateTimeConvertFactory();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(enumeratorConvertFactory());
        registry.addConverterFactory(localDateTimeConvertFactory());
    }

    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        // 配置枚举自定义序列化规则，必须继承Enumerator接口
        // 反序列化需要自己使用注解，或者指定全局的具体类型，这里配置不好使, 然后就是这里的配置不好使，只能用下面的方法
        // jacksonObjectMapperBuilder.deserializerByType(Enumerator.class, new EnumeratorDeserializer());
        ClassUtil.getInterfaceImpls(Enumerator.class).forEach(clazz -> {
            jacksonObjectMapperBuilder.deserializerByType(clazz, new EnumeratorDeserializer());
        });
        jacksonObjectMapperBuilder.serializerByType(Enumerator.class, new EnumeratorSerializer());
        // 配置LocalDateTime规则，时间戳
        jacksonObjectMapperBuilder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer());
        jacksonObjectMapperBuilder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer());


    }
    
}
