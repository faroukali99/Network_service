package com.example.Network._service.infrastructure.config;

import com.example.Network._service.application.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final NetworkService networkService;

    @Autowired
    public SchedulerConfig(NetworkService networkService) {
        this.networkService = networkService;
    }

    // Vérification de la santé des nœuds toutes les minutes
    @Scheduled(fixedRate = 60000)
    public void scheduleHealthCheck() {
        networkService.checkNodesHealth();
    }
}
