package com.pda.stock_module.domain.common;

import com.google.gson.Gson;
import com.pda.stock_module.web.model.StockDetailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCommon {
    private final RedisTemplate<String, String> template;
    private final Gson gson;

    public String getValueFromHash(String key, String field) {
        Object result = template.opsForHash().get(key, field);

        if (result != null) {

            return result.toString();
        }
        return null;
    }

    public <T> T getEntriesFromHash(String key, Class<T> clazz) {
        Map<Object, Object> entries = template.opsForHash().entries("stock:" + key);
        if (entries != null) {
            String jsonValue = gson.toJson(entries);
            return gson.fromJson(jsonValue, clazz);
        }
        return null;
    }

    public <T> List<T> getAllList(String key, Class<T> clazz) {
        List<String> jsonValues = template.opsForList().range(key, 0, -1);
        List<T> resultSet = new ArrayList<>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T value = gson.fromJson(jsonValue, clazz);
                resultSet.add(value);
            }
        }
        return resultSet;
    }
}
