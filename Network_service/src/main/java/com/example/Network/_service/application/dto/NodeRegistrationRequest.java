// NodeRegistrationRequest.java
package com.example.Network._service.application.dto;

import com.example.Network._service.domain.enums.MessageType;
import com.example.Network._service.domain.enums.NodeStatus;
import com.example.Network._service.domain.enums.NodeType;
import com.example.Network._service.domain.enums.RequestPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class NodeRegistrationRequest {

    @NotBlank(message = "Le nom du nœud est obligatoire")
    private String nodeName;

    @NotNull(message = "Le type de nœud est obligatoire")
    private NodeType nodeType;

    @NotBlank(message = "L'adresse IP est obligatoire")
    private String ipAddress;

    @NotNull(message = "Le port est obligatoire")
    private Integer port;

    private String version;
    private String region;
    private String publicKey;
    private Boolean isValidator = false;

    // Getters et Setters
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    public NodeType getNodeType() { return nodeType; }
    public void setNodeType(NodeType nodeType) { this.nodeType = nodeType; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public Boolean getIsValidator() { return isValidator; }
    public void setIsValidator(Boolean isValidator) { this.isValidator = isValidator; }
}



