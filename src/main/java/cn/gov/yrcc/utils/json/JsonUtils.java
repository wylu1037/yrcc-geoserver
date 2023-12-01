package cn.gov.yrcc.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * json utils base on jackson.
 */
@Slf4j
public class JsonUtils {

    private JsonUtils() {
    }

    // json config
    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            .build();

    /**
     * convert object to json string
     *
     * @param object object
     * @return json string
     */
    public static String toJsonString(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("[JsonUtils] toJsonString() occurred error. ", e);
        }
        return null;
    }

    /**
     * convert json to specify bean
     *
     * @param json  json
     * @param clazz clazz
     * @param <T>   generic
     * @return T
     */
    public static <T> T toBean(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("[JsonUtils] toBean() called with Params: json = {}, clazz = {}, Occurred error ",
                    json, clazz,e);
        }
        return null;
    }

    /**
     * convert json to List
     *
     * @param json  json string
     * @param clazz clazz
     * @param <T>   generic
     * @return List
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        JavaType type = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            log.error("[JsonUtils] toList() called with Params: json = {}, clazz = {}, Occurred error ",
                    json, clazz, e);
        }
        return Collections.emptyList();
    }

    /**
     * convert json to map
     *
     * @param json       json string
     * @param keyClass   key class
     * @param valueClass value class
     * @param <K>        generic
     * @param <V>        generic
     * @return Map
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        JavaType type = MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            log.error("[JsonUtils] toMap() called with Params: json = {}, keyClass = {}, valueClass = {}, " +
                    "Occurred error ", json, keyClass, valueClass, e);
        }
        return Collections.emptyMap();
    }

	/**
	 * convert object to map
	 *
	 * @param object object
	 * @return Map
	 */
	public static Map<String, Object> objectToMap(Object object) {
		return MAPPER.convertValue(object, new TypeReference<>() {
		});
	}
}
