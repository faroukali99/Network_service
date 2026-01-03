package com.example.Network._service.domain.model;

import com.example.Network._service.domain.enums.MessageType;
import com.example.Network._service.domain.enums.RequestPriority;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "network_messages")
public class NetworkMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(name = "source_node_id", nullable = false)
    private String sourceNodeId;

    @Column(name = "target_node_id")
    private String targetNodeId;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private RequestPriority priority = RequestPriority.NORMAL;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "is_broadcast")
    private Boolean isBroadcast = false;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "error_message")
    private String errorMessage;

    // Constructeurs
    public NetworkMessage() {}

    public NetworkMessage(String messageId, MessageType messageType, String sourceNodeId) {
        this.messageId = messageId;
        this.messageType = messageType;
        this.sourceNodeId = sourceNodeId;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public String getSourceNodeId() { return sourceNodeId; }
    public void setSourceNodeId(String sourceNodeId) { this.sourceNodeId = sourceNodeId; }

    public String getTargetNodeId() { return targetNodeId; }
    public void setTargetNodeId(String targetNodeId) { this.targetNodeId = targetNodeId; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public RequestPriority getPriority() { return priority; }
    public void setPriority(RequestPriority priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public Boolean getIsBroadcast() { return isBroadcast; }
    public void setIsBroadcast(Boolean isBroadcast) { this.isBroadcast = isBroadcast; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public void incrementRetryCount() {
        this.retryCount++;
    }
}