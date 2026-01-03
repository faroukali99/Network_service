package com.example.Network._service.application.apitest;

import com.example.Network._service.application.dto.NetworkMessageDTO;
import com.example.Network._service.application.dto.NodeDTO;
import com.example.Network._service.application.dto.NodeRegistrationRequest;
import com.example.Network._service.application.service.NetworkService;
import com.example.Network._service.application.service.ServiceDiscoveryService;
import com.example.Network._service.domain.enums.MessageType;
import com.example.Network._service.domain.enums.NodeType;
import com.example.Network._service.domain.enums.RequestPriority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Contrôleur de tests pour faciliter les tests API du Network Service
 */
@RestController
@RequestMapping("/api/network/test")
@CrossOrigin(origins = "*")
public class NetworkApiTestController {

    private final NetworkService networkService;
    private final ServiceDiscoveryService serviceDiscoveryService;

    @Autowired
    public NetworkApiTestController(
            NetworkService networkService,
            ServiceDiscoveryService serviceDiscoveryService) {
        this.networkService = networkService;
        this.serviceDiscoveryService = serviceDiscoveryService;
    }

    /**
     * Test 1: Enregistrer plusieurs nœuds de test
     */
    @PostMapping("/setup/nodes")
    public ResponseEntity<?> setupTestNodes() {
        List<NodeDTO> registeredNodes = new ArrayList<>();

        // User Service Node
        NodeRegistrationRequest userNode = new NodeRegistrationRequest();
        userNode.setNodeName("Test User Service");
        userNode.setNodeType(NodeType.USER_SERVICE);
        userNode.setIpAddress("localhost");
        userNode.setPort(8081);
        userNode.setVersion("1.0.0");
        userNode.setRegion("test");
        registeredNodes.add(networkService.registerNode(userNode));

        // Wallet Service Node
        NodeRegistrationRequest walletNode = new NodeRegistrationRequest();
        walletNode.setNodeName("Test Wallet Service");
        walletNode.setNodeType(NodeType.WALLET_SERVICE);
        walletNode.setIpAddress("localhost");
        walletNode.setPort(8084);
        walletNode.setVersion("1.0.0");
        walletNode.setRegion("test");
        registeredNodes.add(networkService.registerNode(walletNode));

        // Blockchain Service Node (Validator)
        NodeRegistrationRequest blockchainNode = new NodeRegistrationRequest();
        blockchainNode.setNodeName("Test Blockchain Service");
        blockchainNode.setNodeType(NodeType.BLOCKCHAIN_SERVICE);
        blockchainNode.setIpAddress("localhost");
        blockchainNode.setPort(8082);
        blockchainNode.setVersion("1.0.0");
        blockchainNode.setRegion("test");
        blockchainNode.setIsValidator(true);
        registeredNodes.add(networkService.registerNode(blockchainNode));

        // Validator Node
        NodeRegistrationRequest validatorNode = new NodeRegistrationRequest();
        validatorNode.setNodeName("Test Validator Node");
        validatorNode.setNodeType(NodeType.VALIDATOR_NODE);
        validatorNode.setIpAddress("192.168.1.100");
        validatorNode.setPort(9090);
        validatorNode.setVersion("1.0.0");
        validatorNode.setRegion("test");
        validatorNode.setIsValidator(true);
        registeredNodes.add(networkService.registerNode(validatorNode));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Nœuds de test créés avec succès");
        response.put("nodes", registeredNodes);
        response.put("count", registeredNodes.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Test 2: Vérifier tous les services externes
     */
    @GetMapping("/check/all-services")
    public ResponseEntity<?> checkAllServices() {
        Map<String, Object> healthStatuses = serviceDiscoveryService.checkAllServicesHealth();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("timestamp", new Date());
        response.put("services", healthStatuses);

        return ResponseEntity.ok(response);
    }

    /**
     * Test 3: Scénario complet de messagerie
     */
    @PostMapping("/scenario/messaging")
    public ResponseEntity<?> testMessagingScenario() {
        List<Map<String, Object>> results = new ArrayList<>();

        // Récupérer les nœuds actifs
        List<NodeDTO> activeNodes = networkService.getActiveNodes();

        if (activeNodes.size() < 2) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Pas assez de nœuds actifs pour tester la messagerie"
            ));
        }

        // Test 1: Message normal
        NetworkMessageDTO normalMsg = new NetworkMessageDTO();
        normalMsg.setMessageType(MessageType.SERVICE_REQUEST);
        normalMsg.setSourceNodeId("test-source");
        normalMsg.setTargetNodeId(activeNodes.get(0).getNodeId());
        normalMsg.setPayload("{\"action\":\"test\",\"data\":\"normal priority\"}");
        normalMsg.setPriority(RequestPriority.NORMAL);
        results.add(Map.of("test", "Normal Message", "targetNode", activeNodes.get(0).getNodeName()));

        // Test 2: Message haute priorité
        NetworkMessageDTO highPriorityMsg = new NetworkMessageDTO();
        highPriorityMsg.setMessageType(MessageType.CONSENSUS_REQUEST);
        highPriorityMsg.setSourceNodeId("test-source");
        highPriorityMsg.setTargetNodeId(activeNodes.get(0).getNodeId());
        highPriorityMsg.setPayload("{\"action\":\"consensus\",\"blockHeight\":123}");
        highPriorityMsg.setPriority(RequestPriority.HIGH);
        results.add(Map.of("test", "High Priority Message", "targetNode", activeNodes.get(0).getNodeName()));

        // Test 3: Broadcast
        networkService.broadcastMessage(
                MessageType.TRANSACTION_BROADCAST,
                "{\"txId\":\"test-tx-001\",\"amount\":100}",
                NodeType.BLOCKCHAIN_SERVICE
        );
        results.add(Map.of("test", "Broadcast Message", "targetType", "BLOCKCHAIN_SERVICE"));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Scénario de messagerie exécuté");
        response.put("tests", results);
        return ResponseEntity.ok(response);
    }

    /**
     * Test 4: Simulation de heartbeats
     */
    @PostMapping("/scenario/heartbeat")
    public ResponseEntity<?> testHeartbeatScenario() {
        List<NodeDTO> activeNodes = networkService.getActiveNodes();
        List<Map<String, String>> results = new ArrayList<>();

        for (NodeDTO node : activeNodes) {
            try {
                networkService.updateHeartbeat(node.getNodeId());
                results.add(Map.of(
                        "nodeId", node.getNodeId(),
                        "nodeName", node.getNodeName(),
                        "status", "SUCCESS"
                ));
            } catch (Exception e) {
                results.add(Map.of(
                        "nodeId", node.getNodeId(),
                        "nodeName", node.getNodeName(),
                        "status", "FAILED",
                        "error", e.getMessage()
                ));
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Heartbeats envoyés");
        response.put("results", results);
        return ResponseEntity.ok(response);
    }

    /**
     * Test 5: Statistiques détaillées
     */
    @GetMapping("/stats/detailed")
    public ResponseEntity<?> getDetailedStats() {
        NetworkService.NetworkStats stats = networkService.getNetworkStats();
        List<NodeDTO> activeNodes = networkService.getActiveNodes();
        List<NodeDTO> validators = networkService.getActiveValidators();

        Map<String, Object> detailedStats = new HashMap<>();
        detailedStats.put("totalNodes", stats.getTotalNodes());
        detailedStats.put("activeNodes", stats.getActiveNodes());
        detailedStats.put("totalMessages", stats.getTotalMessages());
        detailedStats.put("validatorCount", validators.size());

        // Statistiques par type de nœud
        Map<String, Long> nodesByType = new HashMap<>();
        for (NodeType type : NodeType.values()) {
            long count = networkService.getNodesByType(type).size();
            if (count > 0) {
                nodesByType.put(type.name(), count);
            }
        }
        detailedStats.put("nodesByType", nodesByType);

        // Santé moyenne des nœuds
        double avgSuccessRate = activeNodes.stream()
                .mapToDouble(NodeDTO::getSuccessRate)
                .average()
                .orElse(0.0);
        detailedStats.put("averageSuccessRate", avgSuccessRate);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stats", detailedStats);
        return ResponseEntity.ok(response);
    }

    /**
     * Test 6: Nettoyage des nœuds de test
     */
    @DeleteMapping("/cleanup/test-nodes")
    public ResponseEntity<?> cleanupTestNodes() {
        List<NodeDTO> testNodes = networkService.getActiveNodes().stream()
                .filter(node -> "test".equals(node.getRegion()))
                .toList();

        int deletedCount = 0;
        for (NodeDTO node : testNodes) {
            try {
                networkService.deregisterNode(node.getNodeId());
                deletedCount++;
            } catch (Exception e) {
                // Continue même en cas d'erreur
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Nettoyage effectué");
        response.put("deletedCount", deletedCount);
        return ResponseEntity.ok(response);
    }

    /**
     * Test 7: Obtenir un résumé de l'état du réseau
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getNetworkSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Statistiques générales
        NetworkService.NetworkStats stats = networkService.getNetworkStats();
        summary.put("totalNodes", stats.getTotalNodes());
        summary.put("activeNodes", stats.getActiveNodes());
        summary.put("totalMessages", stats.getTotalMessages());

        // Nœuds par statut
        List<NodeDTO> activeNodes = networkService.getActiveNodes();
        summary.put("activeNodesList", activeNodes.stream()
                .map(node -> Map.of(
                        "nodeId", node.getNodeId(),
                        "nodeName", node.getNodeName(),
                        "nodeType", node.getNodeType().name(),
                        "successRate", node.getSuccessRate()
                ))
                .toList());

        // Validateurs
        List<NodeDTO> validators = networkService.getActiveValidators();
        summary.put("validatorCount", validators.size());
        summary.put("validators", validators.stream()
                .map(v -> Map.of("nodeId", v.getNodeId(), "nodeName", v.getNodeName()))
                .toList());

        return ResponseEntity.ok(summary);
    }

    /**
     * Test 8: Générer des données de test
     */
    @PostMapping("/generate/sample-data")
    public ResponseEntity<?> generateSampleData() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Utilisez d'abord /api/network/test/setup/nodes pour créer des nœuds de test");

        // Instructions
        List<String> steps = Arrays.asList(
                "1. POST /api/network/test/setup/nodes - Créer des nœuds de test",
                "2. POST /api/network/test/scenario/heartbeat - Tester les heartbeats",
                "3. POST /api/network/test/scenario/messaging - Tester la messagerie",
                "4. GET /api/network/test/stats/detailed - Voir les statistiques",
                "5. GET /api/network/test/summary - Voir le résumé",
                "6. DELETE /api/network/test/cleanup/test-nodes - Nettoyer"
        );

        response.put("steps", steps);
        return ResponseEntity.ok(response);
    }
}