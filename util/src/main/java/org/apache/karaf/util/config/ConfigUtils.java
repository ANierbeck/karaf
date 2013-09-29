package org.apache.karaf.util.config;

import java.util.Map;

public class ConfigUtils {


    public static <T> String readString(Map<String, T> properties, String key) {
        if (properties.containsKey(key)) {
            T value = properties.get(key);
            if (value instanceof String) {
                return (String) value;
            } else {
                return String.valueOf(value);
            }
        } else {
            return null;
        }
    }

    public static <T> Boolean readBoolean(Map<String, T> properties, String key) {
        if (properties.containsKey(key)) {
            T value = properties.get(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
        }
        throw new IllegalStateException("Property "+key+ " is not Boolean or String");
    }

    public static <T> Long readLong(Map<String, T> properties, String key) {
        if (properties.containsKey(key)) {
            T value = properties.get(key);
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof String) {
                return Long.parseLong((String) value);
            }
        }
        throw new IllegalStateException("Property "+key+ " is not Long or String");
    }

    public static <T> Integer readInt(Map<String, T> properties, String key) {
        if (properties.containsKey(key)) {
            T value = properties.get(key);
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
        }
        throw new IllegalStateException("Property "+key+ " is not Integer or String");
    }
}
