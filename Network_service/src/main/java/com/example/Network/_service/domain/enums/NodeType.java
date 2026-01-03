// NodeType.java
package com.example.Network._service.domain.enums;

public enum NodeType {
    USER_SERVICE("user-service", "Service de gestion des utilisateurs"),
    WALLET_SERVICE("wallet-service", "Service de gestion des portefeuilles"),
    BLOCKCHAIN_SERVICE("blockchain-service", "Service blockchain"),
    GOVERNANCE_SERVICE("governance-service", "Service de gouvernance"),
    NETWORK_SERVICE("network-service", "Service réseau"),
    API_GATEWAY("api-gateway", "Passerelle API"),
    VALIDATOR_NODE("validator-node", "Nœud validateur");

    private final String serviceName;
    private final String description;

    NodeType(String serviceName, String description) {
        this.serviceName = serviceName;
        this.description = description;
    }

    public String getServiceName() { return serviceName; }
    public String getDescription() { return description; }
}


