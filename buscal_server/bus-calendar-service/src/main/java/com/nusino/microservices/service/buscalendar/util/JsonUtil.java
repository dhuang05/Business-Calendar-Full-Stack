/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ThreadLocal<ObjectMapper> omHolder = new ThreadLocal<ObjectMapper>() {
        @Override
        protected ObjectMapper initialValue() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper;
        }
    };

    public static <T> T fromJson(String jsonStr, Class<T> type) {
        if (jsonStr == null || type == null) {
            return null;
        }
        try {
            return omHolder.get().readValue(jsonStr, type);
        } catch (Exception ex) {
            final String msg = String.format("fail to convert back to %s from %s", type.getName(), jsonStr);
            throw new RuntimeException(msg, ex);
        }
    }


    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return omHolder.get().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception ex) {
            final String msg = String.format("fail to write %s as json string", obj.getClass().getName());
            throw new RuntimeException(msg, ex);
        }
    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return omHolder.get().writeValueAsString(obj);
        } catch (Exception ex) {
            final String msg = String.format("fail to write %s as json string", obj.getClass().getName());
            throw new RuntimeException(msg, ex);
        }
    }

    public static <T> T clone(T t) {
        if (t == null) {
            return t;
        }
        return (T) fromJson(toJson(t), t.getClass());
    }
    public static <T, R> R clone(T t, Class<R> rClass) {
        if (t == null) {
            return null;
        }
        return (R) fromJson(toJson(t), rClass);
    }

}