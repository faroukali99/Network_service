// GovernanceServiceClient.java
package com.example.Network._service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "governance-service", url = "${services.governance-service.url:http://localhost:8085}")
public interface GovernanceServiceClient {

    @GetMapping("/api/health")
    Object healthCheck();

    @GetMapping("/api/governance/proposals")
    Object getProposals();
}