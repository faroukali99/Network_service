package com.example.Network._service.application.service;

import com.example.Network._service.application.dto.NetworkMessageDTO;
import com.example.Network._service.application.dto.NodeDTO;
import com.example.Network._service.application.dto.NodeRegistrationRequest;
import com.example.Network._service.domain.enums.MessageType;
import com.example.Network._service.domain.enums.NodeStatus;
import com.example.Network._service.domain.enums.NodeType;
import com.example.Network._service.domain.model.NetworkMessage;
import com.example.Network._service.domain.model.NetworkNode;
import com.example.Network._service.infrastructure.repository.NetworkMessageRepository;
import com.example.Network._service.infrastructure.repository.NetworkNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NetworkService {

    private final NetworkNodeRepository nodeRepository;
    private final NetworkMessageRepository messageRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public NetworkService(
            NetworkNodeRepository nodeRepository,
            NetworkMessageRepository messageRepository,
            WebClient.Builder webClientBuilder) {
        this.nodeRepository = nodeRepository;
        this.messageRepository = messageRepository;
        this.webClientBuilder = webClientBuilder;
    }

    // Enregistrement d'un nœud
    public NodeDTO registerNode(NodeRegistrationRequest request) {
        String nodeId = UUID.randomUUID().toString();

        NetworkNode node = new NetworkNode(
                nodeId,
                request.getNodeName(),
                request.getNodeType(),
                request.getIpAddress(),
                request.getPort()
        );

        node.setVersion(request.getVersion());
        node.setRegion(request.getRegion());
        node.setPublicKey(request.getPublicKey());
        node.setIsValidator(request.getIsValidator());
        node.setLastHeartbeat(LocalDateTime.now());

        NetworkNode savedNode = nodeRepository.save(node);
        return toNodeDTO(savedNode);
    }

    // Mise à jour du heartbeat
    public void updateHeartbeat(String nodeId) {
        NetworkNode node = nodeRepository.findByNodeId(nodeId)
                .orElseThrow(() -> new RuntimeException("Nœud non trouvé: " + nodeId));

        node.setLastHeartbeat(LocalDateTime.now());
        node.setStatus(NodeStatus.ACTIVE);
        nodeRepository.save(node);
    }

    // Désenregistrement d'un nœud
    public void deregisterNode(String nodeId) {
        NetworkNode node = nodeRepository.findByNodeId(nodeId)
                .orElseThrow(() -> new RuntimeException("Nœud non trouvé: " + nodeId));

        node.setStatus(NodeStatus.OFFLINE);
        nodeRepository.save(node);
    }

    // Récupérer tous les nœuds actifs
    public List<NodeDTO> getActiveNodes() {
        return nodeRepository.findByStatus(NodeStatus.ACTIVE)
                .stream()
                .map(this::toNodeDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les nœuds par type
    public List<NodeDTO> getNodesByType(NodeType nodeType) {
        return nodeRepository.findByNodeType(nodeType)
                .stream()
                .map(this::toNodeDTO)
                .collect(Collectors.toList());
    }

    // Envoi d'un message à un nœud spécifique
    public Mono<String> sendMessageToNode(NetworkMessageDTO messageDTO) {
        NetworkNode targetNode = nodeRepository.findByNodeId(messageDTO.getTargetNodeId())
                .orElseThrow(() -> new RuntimeException("Nœud cible non trouvé"));

        // Enregistrer le message
        NetworkMessage message = new NetworkMessage(
                messageDTO.getMessageId() != null ? messageDTO.getMessageId() : UUID.randomUUID().toString(),
                messageDTO.getMessageType(),
                messageDTO.getSourceNodeId()
        );
        message.setTargetNodeId(messageDTO.getTargetNodeId());
        message.setPayload(messageDTO.getPayload());
        message.setPriority(messageDTO.getPriority());
        messageRepository.save(message);

        // Envoyer via WebClient
        return webClientBuilder.build()
                .post()
                .uri(targetNode.getServiceUrl() + "/api/network/receive")
                .bodyValue(messageDTO)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> {
                    message.setStatus("DELIVERED");
                    message.setProcessedAt(LocalDateTime.now());
                    messageRepository.save(message);
                    targetNode.incrementTotalRequests();
                    nodeRepository.save(targetNode);
                })
                .doOnError(error -> {
                    message.setStatus("FAILED");
                    message.setErrorMessage(error.getMessage());
                    messageRepository.save(message);
                    targetNode.incrementFailedRequests();
                    nodeRepository.save(targetNode);
                });
    }

    // Broadcast d'un message à tous les nœuds d'un type
    public void broadcastMessage(MessageType messageType, String payload, NodeType targetType) {
        List<NetworkNode> targetNodes = nodeRepository.findByStatusAndType(NodeStatus.ACTIVE, targetType);

        for (NetworkNode node : targetNodes) {
            NetworkMessageDTO messageDTO = new NetworkMessageDTO();
            messageDTO.setMessageId(UUID.randomUUID().toString());
            messageDTO.setMessageType(messageType);
            messageDTO.setSourceNodeId("NETWORK_SERVICE");
            messageDTO.setTargetNodeId(node.getNodeId());
            messageDTO.setPayload(payload);
            messageDTO.setIsBroadcast(true);

            sendMessageToNode(messageDTO).subscribe();
        }
    }

    // Vérification de la santé des nœuds
    public void checkNodesHealth() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<NetworkNode> staleNodes = nodeRepository.findStaleNodes(threshold);

        for (NetworkNode node : staleNodes) {
            node.setStatus(NodeStatus.OFFLINE);
            nodeRepository.save(node);
        }
    }

    // Récupérer les validateurs actifs
    public List<NodeDTO> getActiveValidators() {
        return nodeRepository.findActiveValidators()
                .stream()
                .map(this::toNodeDTO)
                .collect(Collectors.toList());
    }

    // Statistiques du réseau
    public NetworkStats getNetworkStats() {
        long totalNodes = nodeRepository.count();
        long activeNodes = nodeRepository.countActiveNodes();
        long totalMessages = messageRepository.count();

        NetworkStats stats = new NetworkStats();
        stats.setTotalNodes(totalNodes);
        stats.setActiveNodes(activeNodes);
        stats.setTotalMessages(totalMessages);
        return stats;
    }

    // Conversion en DTO
    private NodeDTO toNodeDTO(NetworkNode node) {
        NodeDTO dto = new NodeDTO();
        dto.setId(node.getId());
        dto.setNodeId(node.getNodeId());
        dto.setNodeName(node.getNodeName());
        dto.setNodeType(node.getNodeType());
        dto.setIpAddress(node.getIpAddress());
        dto.setPort(node.getPort());
        dto.setServiceUrl(node.getServiceUrl());
        dto.setStatus(node.getStatus());
        dto.setLastHeartbeat(node.getLastHeartbeat());
        dto.setRegisteredAt(node.getRegisteredAt());
        dto.setLatencyMs(node.getLatencyMs());
        dto.setVersion(node.getVersion());
        dto.setRegion(node.getRegion());
        dto.setIsValidator(node.getIsValidator());
        dto.setSuccessRate(node.getSuccessRate());
        return dto;
    }

    // Classe interne pour les statistiques
    public static class NetworkStats {
        private long totalNodes;
        private long activeNodes;
        private long totalMessages;

        // Getters et Setters
        public long getTotalNodes() { return totalNodes; }
        public void setTotalNodes(long totalNodes) { this.totalNodes = totalNodes; }

        public long getActiveNodes() { return activeNodes; }
        public void setActiveNodes(long activeNodes) { this.activeNodes = activeNodes; }

        public long getTotalMessages() { return totalMessages; }
        public void setTotalMessages(long totalMessages) { this.totalMessages = totalMessages; }
    }
}