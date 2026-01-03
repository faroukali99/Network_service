// WalletServiceClient.java
package com.example.Network._service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "wallet-service", url = "${services.wallet-service.url:http://localhost:8084}")
public interface WalletServiceClient {

    @GetMapping("/api/health")
    Object healthCheck();

    @GetMapping("/api/wallets/{id}")
    Object getWalletById(@PathVariable Long id);
}