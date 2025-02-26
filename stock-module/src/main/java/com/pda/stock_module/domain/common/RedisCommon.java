package com.pda.stock_module.domain.common;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCommon {
    private final RedisTemplate<String, String> template;
    private final Gson gson;

    public String getFromHash(String key, String field) {
        Object result = template.opsForHash().get(key, field);

        if (result != null) {

            return result.toString();
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
