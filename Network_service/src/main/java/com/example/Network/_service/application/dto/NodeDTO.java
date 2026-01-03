package com.example.Network._service.application.dto;

import com.example.Network._service.domain.enums.NodeStatus;
import com.example.Network._service.domain.enums.NodeType;

import java.time.LocalDateTime;

public class NodeDTO {

    private Long id;
    private String nodeId;
    private String nodeName;
    private NodeType nodeType;
    private String ipAddress;
    private Integer port;
    private String serviceUrl;
    private NodeStatus status;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime registeredAt;
    private Long latencyMs;
    private String version;
    private String region;
    private Boolean isValidator;
    private Double successRate;

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

    public Boolean getIsValidator() { return isValidator; }
    public void setIsValidator(Boolean isValidator) { this.isValidator = isValidator; }

    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
}
