package com.midasdev.mybg.global.util.default_value_mapper;

import java.lang.reflect.Field;

public class DefaultValueResolver {

    public static <T> void applyDefaults(T dto) {
        for (Field field : dto.getClass().getDeclaredFields()) {
            Default defaultAnnotation = field.getAnnotation(Default.class);
            if (defaultAnnotation == null) {
                continue;
            }

            field.setAccessible(true);
            try {
                if (field.get(dto) == null) {
                    Object value = convert(defaultAnnotation.value(), field.getType());
                    field.set(dto, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "Cannot apply default value '"
                                + defaultAnnotation.value()
                                + "' to field '"
                                + field.getName()
                                + "'",
                        e);
            }
        }
    }

    private static Object convert(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        }
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }
        if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }
        if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }
        throw new IllegalArgumentException(
                "Unsupported type: "
                        + type.getName()
                        + ". Supported types are: String, Integer, Long, Boolean, Double, Float.");
    }
}
