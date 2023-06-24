package fun.easycode.snail.boot.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class EnumeratorDeserializer extends JsonDeserializer<Enumerator<?>> implements ContextualDeserializer {

    private JavaType type;

    public EnumeratorDeserializer() {
    }

    public EnumeratorDeserializer(JavaType type) {
        this.type = type;
    }


    @Override
    public Enumerator<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Object val = jsonParser.readValueAs(Object.class);
        Class<Enumerator<?>> enumeratorClass = (Class<Enumerator<?>>) type.getRawClass();
        return Arrays.stream(enumeratorClass.getEnumConstants())
                .filter(enumerator -> Objects.equals(enumerator.getValue(), val)).findFirst().orElse(null);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        //beanProperty is null when the type to deserialize is the top-level type or a generic type, not a type of a bean property
        JavaType type = deserializationContext.getContextualType() != null
                ? deserializationContext.getContextualType()
                : beanProperty.getMember().getType();
        return new EnumeratorDeserializer(type);
    }
}
