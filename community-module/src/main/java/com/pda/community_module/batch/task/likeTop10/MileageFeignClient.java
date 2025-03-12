package com.pda.community_module.batch.task.likeTop10;


import com.pda.community_module.config.MileageFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import feign.Headers;

@FeignClient(name = "mileage-service", url = "${feign.piggyBank-url}"
        ,configuration = MileageFeignConfig.class)
public interface MileageFeignClient {
    @PostMapping(value = "/mileage/add", consumes = "application/json")
    @Headers("Content-Type: application/json")  // JSON 요청으로 명시
    void addMileage(@RequestBody MileageRequest request);
}

