// BlockchainServiceClient.java
package com.example.Network._service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "blockchain-service", url = "${services.blockchain-service.url:http://localhost:8082}")
public interface BlockchainServiceClient {

    @GetMapping("/api/health")
    Object healthCheck();

    @GetMapping("/api/blockchain/blocks/{height}")
    Object getBlockByHeight(@PathVariable Long height);
}