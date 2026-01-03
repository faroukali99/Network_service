package com.example.Network._service.infrastructure.config;

import com.example.Network._service.domain.enums.NodeType;
import com.example.Network._service.domain.model.NetworkNode;
import com.example.Network._service.infrastructure.repository.NetworkNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final NetworkNodeRepository nodeRepository;

    @Autowired
    public DataLoader(NetworkNodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (nodeRepository.count() == 0) {
            createDefaultNodes();
        }
    }

    private void createDefaultNodes() {
        // Enregistrer le service réseau lui-même
        NetworkNode networkService = new NetworkNode(
                UUID.randomUUID().toString(),
                "Network Service",
                NodeType.NETWORK_SERVICE,
                "localhost",
                8083
        );
        networkService.setVersion("1.0.0");
        networkService.setRegion("local");
        networkService.setLastHeartbeat(LocalDateTime.now());
        nodeRepository.save(networkService);

        System.out.println("=================================================");
        System.out.println("Service réseau initialisé avec succès");
        System.out.println("=================================================");
    }
}
