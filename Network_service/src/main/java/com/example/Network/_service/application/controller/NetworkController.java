package com.example.Network._service.application.controller;

import com.example.Network._service.application.dto.NetworkMessageDTO;
import com.example.Network._service.application.dto.NodeDTO;
import com.example.Network._service.application.dto.NodeRegistrationRequest;
import com.example.Network._service.application.service.NetworkService;
import com.example.Network._service.domain.enums.NodeType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/network")
@CrossOrigin(origins = "*")
public class NetworkController {

    private final NetworkService networkService;

    @Autowired
    public NetworkController(NetworkService networkService) {
        this.networkService = networkService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNode(@Valid @RequestBody NodeRegistrationRequest request) {
        try {
            NodeDTO nodeDTO = networkService.registerNode(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Nœud enregistré avec succès");
            response.put("node", nodeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/heartbeat/{nodeId}")
    public ResponseEntity<?> heartbeat(@PathVariable String nodeId) {
        try {
            networkService.updateHeartbeat(nodeId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Heartbeat enregistré");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @DeleteMapping("/deregister/{nodeId}")
    public ResponseEntity<?> deregisterNode(@PathVariable String nodeId) {
        try {
            networkService.deregisterNode(nodeId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Nœud désenregistré avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/nodes/active")
    public ResponseEntity<List<NodeDTO>> getActiveNodes() {
        List<NodeDTO> nodes = networkService.getActiveNodes();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/nodes/type/{type}")
    public ResponseEntity<List<NodeDTO>> getNodesByType(@PathVariable NodeType type) {
        List<NodeDTO> nodes = networkService.getNodesByType(type);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/nodes/validators")
    public ResponseEntity<List<NodeDTO>> getActiveValidators() {
        List<NodeDTO> validators = networkService.getActiveValidators();
        return ResponseEntity.ok(validators);
    }

    @PostMapping("/message/send")
    public Mono<ResponseEntity<Map<String, Object>>> sendMessage(@Valid @RequestBody NetworkMessageDTO messageDTO) {
        return networkService.sendMessageToNode(messageDTO)
                .map(result -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Message envoyé avec succès");
                    response.put("result", result);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }

    @PostMapping("/message/receive")
    public ResponseEntity<?> receiveMessage(@RequestBody NetworkMessageDTO messageDTO) {
        // Point d'entrée pour recevoir des messages d'autres nœuds
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Message reçu");
        response.put("messageId", messageDTO.getMessageId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getNetworkStats() {
        NetworkService.NetworkStats stats = networkService.getNetworkStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/health-check")
    public ResponseEntity<?> performHealthCheck() {
        networkService.checkNodesHealth();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Vérification de santé effectuée");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "network-service");
        return ResponseEntity.ok(health);
    }
}