package com.git.hui.offer.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author YiHui
 * @date 2025/7/14
 */
public class JsonUtil {
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.findAndRegisterModules();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(IntBaseEnum.class, new CustomerIntegerEnumDeserializer());
        module.addSerializer(IntBaseEnum.class, new CustomerIntegerEnumSerializer());
        module.addDeserializer(StringBaseEnum.class, new CustomerStringEnumDeserializer());
        module.addSerializer(StringBaseEnum.class, new CustomerStringEnumSerializer());
        mapper.registerModule(module);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    }

    public static class CustomerIntegerEnumSerializer<E extends Enum<?> & IntBaseEnum> extends JsonSerializer<E> {
        @Override
        public void serialize(E value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
            jsonGenerator.writeNumber(value.getValue());
        }
    }

    public static class CustomerIntegerEnumDeserializer<E extends Enum<?> & IntBaseEnum> extends JsonDeserializer<E> {
        @SuppressWarnings("unchecked")
        @Override
        public E deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            Class type = BeanUtils.findPropertyType(jsonParser.getCurrentName(), jsonParser.getCurrentValue().getClass());
            if (!StringUtils.isEmpty(node.asText())) {
                int code = node.asInt();
                return (E) IntBaseEnum.getEnumByCode(type, code);
            } else {
                return null;
            }
        }
    }

    public static class CustomerStringEnumSerializer<E extends Enum<?> & StringBaseEnum> extends JsonSerializer<E> {
        @SuppressWarnings("unchecked")
        @Override
        public void serialize(E value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
            jsonGenerator.writeString(value.getValue());
        }
    }

    public static class CustomerStringEnumDeserializer<E extends Enum<?> & StringBaseEnum> extends JsonDeserializer<E> {
        @SuppressWarnings("unchecked")
        @Override
        public E deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            Class type = BeanUtils.findPropertyType(jsonParser.getCurrentName(), jsonParser.getCurrentValue().getClass());
            String code = node.asText();
            return (E) StringBaseEnum.getEnumByCode(type, code);
        }
    }

    /**
     * 对象转字符串
     *
     * @param o 对象
     * @return 字符串
     */
    public static String toStr(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObj(String s, Class<T> clazz) {
        try {
            return (T) mapper.readValue(s, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T toObj(String s, Type type) {
        try {
            return (T) mapper.readValue(s, new TypeReference<>() {
                @Override
                public Type getType() {
                    return type;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObj(String s, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(s, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
