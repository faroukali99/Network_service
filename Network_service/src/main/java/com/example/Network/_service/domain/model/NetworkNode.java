package com.example.Network._service.domain.model;

import com.example.Network._service.domain.enums.NodeStatus;
import com.example.Network._service.domain.enums.NodeType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "network_nodes")
public class NetworkNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id", nullable = false, unique = true)
    private String nodeId;

    @Column(name = "node_name", nullable = false)
    private String nodeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false)
    private NodeType nodeType;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "port", nullable = false)
    private Integer port;

    @Column(name = "service_url")
    private String serviceUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NodeStatus status = NodeStatus.ACTIVE;

    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "version")
    private String version;

    @Column(name = "region")
    private String region;

    @Column(name = "public_key", length = 1000)
    private String publicKey;

    @Column(name = "is_validator")
    private Boolean isValidator = false;

    @Column(name = "total_requests")
    private Long totalRequests = 0L;

    @Column(name = "failed_requests")
    private Long failedRequests = 0L;

    // Constructeurs
    public NetworkNode() {}

    public NetworkNode(String nodeId, String nodeName, NodeType nodeType, String ipAddress, Integer port) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.ipAddress = ipAddress;
        this.port = port;
        this.serviceUrl = "http://" + ipAddress + ":" + port;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    public NodeType getNodeType() { return nodeType; }
    public void setNodeType(NodeType nodeType) { this.nodeType = nodeType; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    public String getServiceUrl() { return serviceUrl; }
    public void setServiceUrl(String serviceUrl) { this.serviceUrl = serviceUrl; }

    public NodeStatus getStatus() { return status; }
    public void setStatus(NodeStatus status) { this.status = status; }

    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public Long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Long latencyMs) { this.latencyMs = latencyMs; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public Boolean getIsValidator() { return isValidator; }
    public void setIsValidator(Boolean isValidator) { this.isValidator = isValidator; }

    public Long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(Long totalRequests) { this.totalRequests = totalRequests; }

    public Long getFailedRequests() { return failedRequests; }
    public void setFailedRequests(Long failedRequests) { this.failedRequests = failedRequests; }

    // Méthodes utilitaires
    public void incrementTotalRequests() {
        this.totalRequests++;
    }

    public void incrementFailedRequests() {
        this.failedRequests++;
    }

    public boolean isHealthy() {
        return status == NodeStatus.ACTIVE &&
                lastHeartbeat != null &&
                lastHeartbeat.isAfter(LocalDateTime.now().minusMinutes(5));
    }

    public double getSuccessRate() {
        if (totalRequests == 0) return 100.0;
        return ((totalRequests - failedRequests) * 100.0) / totalRequests;
    }
}