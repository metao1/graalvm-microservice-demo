package com.metao.http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.metao.http.utils.HttpUtil.checkNameNullability;

public class JsonObject {

    public static final JsonObject NULL = new JsonObject() {
        @Override
        public boolean equals(Object obj) {
            return obj == null || obj == this || getClass() == obj.getClass();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(null);
        }

        @Override
        public String toString() {
            return "null";
        }
    };

    final Map<String, Object> nameValuePairs;

    public JsonObject() {
        nameValuePairs = new LinkedHashMap<>();
    }

    public void put(String name, Object value) {
        nameValuePairs.putIfAbsent(checkNameNullability(name), value);
    }

    public static String numberToString(Number num) {
        return String.valueOf(num);
    }

    @Override
    public String toString() {
        JsonStringParser stringJson = new JsonStringParser();
        writeTo(stringJson);
        return stringJson.toString();
    }

    public void writeTo(JsonStringParser json) {
        json.startObject();
        nameValuePairs.forEach((name, value) -> json.key(name).value(value));
        json.endObject();
    }
}
