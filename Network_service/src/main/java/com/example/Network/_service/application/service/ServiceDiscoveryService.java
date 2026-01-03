// ServiceDiscoveryService.java
package com.example.Network._service.application.service;

import com.example.Network._service.infrastructure.feign.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceDiscoveryService {

    private final UserServiceClient userServiceClient;
    private final WalletServiceClient walletServiceClient;
    private final BlockchainServiceClient blockchainServiceClient;
    private final GovernanceServiceClient governanceServiceClient;

    @Autowired
    public ServiceDiscoveryService(
            UserServiceClient userServiceClient,
            WalletServiceClient walletServiceClient,
            BlockchainServiceClient blockchainServiceClient,
            GovernanceServiceClient governanceServiceClient) {
        this.userServiceClient = userServiceClient;
        this.walletServiceClient = walletServiceClient;
        this.blockchainServiceClient = blockchainServiceClient;
        this.governanceServiceClient = governanceServiceClient;
    }

    public Map<String, Object> checkAllServicesHealth() {
        Map<String, Object> healthStatuses = new HashMap<>();

        // User Service
        try {
            Object userHealth = userServiceClient.healthCheck();
            healthStatuses.put("user-service", Map.of("status", "UP", "details", userHealth));
        } catch (Exception e) {
            healthStatuses.put("user-service", Map.of("status", "DOWN", "error", e.getMessage()));
        }

        // Wallet Service
        try {
            Object walletHealth = walletServiceClient.healthCheck();
            healthStatuses.put("wallet-service", Map.of("status", "UP", "details", walletHealth));
        } catch (Exception e) {
            healthStatuses.put("wallet-service", Map.of("status", "DOWN", "error", e.getMessage()));
        }

        // Blockchain Service
        try {
            Object blockchainHealth = blockchainServiceClient.healthCheck();
            healthStatuses.put("blockchain-service", Map.of("status", "UP", "details", blockchainHealth));
        } catch (Exception e) {
            healthStatuses.put("blockchain-service", Map.of("status", "DOWN", "error", e.getMessage()));
        }

        // Governance Service
        try {
            Object governanceHealth = governanceServiceClient.healthCheck();
            healthStatuses.put("governance-service", Map.of("status", "UP", "details", governanceHealth));
        } catch (Exception e) {
            healthStatuses.put("governance-service", Map.of("status", "DOWN", "error", e.getMessage()));
        }

        return healthStatuses;
    }
}