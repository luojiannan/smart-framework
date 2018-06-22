package org.kaluote.framework.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ljn
 * @date 2018/6/6.
 * json工具类
 */
public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将对象转Json
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String toJson(T obj) {
        String json;
        try{
            json = OBJECT_MAPPER.writeValueAsString(obj);
        }catch (Exception e){
            logger.error("convert entity to json failure",e);
            throw new RuntimeException(e);
        }
        return json;
    }

    public static <T> T fromJson(String json,Class<T> type) {
        T pojo;
        try{
            pojo = OBJECT_MAPPER.readValue(json, type);
        }catch (Exception e){
            logger.error("convert json to entity failure",e);
            throw new RuntimeException(e);
        }
        return pojo;
    }





}
