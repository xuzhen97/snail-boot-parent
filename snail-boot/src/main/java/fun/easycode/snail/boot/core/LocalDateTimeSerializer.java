package fun.easycode.snail.boot.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fun.easycode.snail.boot.util.DateUtil;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(localDateTime != null){
            jsonGenerator.writeObject(DateUtil.toMilli(localDateTime));
        }else{
            jsonGenerator.writeObject(null);
        }
    }
}
