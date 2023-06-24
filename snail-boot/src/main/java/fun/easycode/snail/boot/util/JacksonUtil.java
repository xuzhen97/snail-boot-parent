package fun.easycode.snail.boot.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;

/**
 * Jackson工具类
 *
 * @author xuzhe
 */
public class JacksonUtil {
    public static ObjectMapper objectMapper = new ObjectMapper();

    static {

        SimpleModule localDateTimeModule = new SimpleModule();
        localDateTimeModule.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                if(localDateTime != null) {
                    long milli = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    jsonGenerator.writeObject(milli);
                }else{
                    jsonGenerator.writeObject(null);
                }
            }
        });

        localDateTimeModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                Long milli = jsonParser.readValueAs(Long.class);
                if(milli != null) {
                    return Instant.ofEpochMilli(milli).atZone(ZoneId.systemDefault()).toLocalDateTime();
                }else{
                    return null;
                }
            }
        });

        objectMapper.registerModule(localDateTimeModule);

    }

    /**
     * 使用泛型方法，把json字符串转换为相应的JavaBean对象。
     * (1)转换为普通JavaBean：readValue(json,Student.class)
     * (2)转换为List,如List<Student>,将第二个参数传递为Student
     * [].class.然后使用Arrays.asList();方法把得到的数组转换为特定类型的List
     *
     * @param jsonStr   String
     * @param valueType Class<T>
     * @return T
     */
    public static <T> T readValue(String jsonStr, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonStr, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json数组转List
     *
     * @param jsonStr      String
     * @param valueTypeRef TypeReference<T>
     * @return T
     */
    public static <T> T readValue(String jsonStr, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 把JavaBean转换为json字符串
     *
     * @param object Object
     * @return String
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据reader转换实体，reader会先转换成string
     * 如果valueType == String.class 则和asString功能一致
     *
     * @param reader    Reader
     * @param valueType Class<T>
     * @return T
     */
    public static <T> T readValue(Reader reader, Class<T> valueType) {
        String value = asString(reader);
        if (valueType == String.class) {
            return (T) value;
        } else {
            return readValue(value, valueType);
        }
    }

    /**
     * 将json字符串转换成JsonNode
     *
     * @param json String
     * @return JsonNode
     */
    public static JsonNode getNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象转换成JsonNode
     *
     * @param o Object
     * @return JsonNode
     */
    public static JsonNode getNode(Object o) {
        String json = toJson(o);
        return getNode(json);
    }

    /**
     * diff 比较老的对象json和新的对象json中value值的差别
     *
     * @param oldObjStr String
     * @param newObjStr String
     * @return String
     */
    public static String diff(String oldObjStr, String newObjStr) {
        Map<String, Object> oldObjMap = readValue(oldObjStr, new TypeReference<Map<String, Object>>(){});
        Map<String, Object> newObjMap = readValue(newObjStr, new TypeReference<Map<String, Object>>(){});
        if (oldObjMap != null && newObjMap != null) {
            StringBuilder diffBuilder = new StringBuilder();
            for (Map.Entry<String, Object> entry : oldObjMap.entrySet()) {
                Object oldValue = entry.getValue();
                Object newValue = newObjMap.get(entry.getKey());
                // 新对象中有值，但是和老对象中的值不一致，需要记录
                if (newValue != null && !Objects.equals(oldValue, newValue)) {
                    diffBuilder.append("[key=").append(entry.getKey()).append(",oldValue=")
                            .append(oldValue).append(",newValue=").append(newValue).append("];");
                }
            }
            return diffBuilder.toString();
        }
        return null;
    }

    /**
     * reader转string
     *
     * @param reader Reader
     * @return String
     */
    public static String asString(Reader reader) {
        char[] arr = new char[8 * 1024];
        StringBuilder buffer = new StringBuilder();
        int numCharsRead;
        try {
            while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
                buffer.append(arr, 0, numCharsRead);
            }
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
