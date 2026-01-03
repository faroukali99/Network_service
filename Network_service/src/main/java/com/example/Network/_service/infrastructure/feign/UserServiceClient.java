// UserServiceClient.java
package com.example.Network._service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${services.user-service.url:http://localhost:8081}")
public interface UserServiceClient {

    @GetMapping("/api/health")
    Object healthCheck();

    @GetMapping("/api/users/{id}")
    Object getUserById(@PathVariable Long id);
}