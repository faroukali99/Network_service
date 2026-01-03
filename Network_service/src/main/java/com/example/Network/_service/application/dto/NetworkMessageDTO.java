package com.example.Network._service.application.dto;

import com.example.Network._service.domain.enums.MessageType;
import com.example.Network._service.domain.enums.RequestPriority;

public class NetworkMessageDTO {

    private String messageId;
    private MessageType messageType;
    private String sourceNodeId;
    private String targetNodeId;
    private String payload;
    private RequestPriority priority;
    private Boolean isBroadcast;

    // Constructeurs
    public NetworkMessageDTO() {}

    // Getters et Setters
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

    public Boolean getIsBroadcast() { return isBroadcast; }
    public void setIsBroadcast(Boolean isBroadcast) { this.isBroadcast = isBroadcast; }
}
