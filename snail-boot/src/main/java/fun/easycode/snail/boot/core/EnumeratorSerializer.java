package fun.easycode.snail.boot.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Enumerator Json Serializer
 * jackson
 * @author xuzhen97
 */
public class EnumeratorSerializer extends JsonSerializer<Enumerator<?>> {
    @Override
    public void serialize(Enumerator<?> enumerator, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//        jsonGenerator.writeStartObject();
//        jsonGenerator.writeObjectField("value", enumerator.getValue());
//        jsonGenerator.writeStringField("desc", enumerator.getDesc());
//        jsonGenerator.writeEndObject();
        jsonGenerator.writeObject(enumerator.getValue());
    }
}
