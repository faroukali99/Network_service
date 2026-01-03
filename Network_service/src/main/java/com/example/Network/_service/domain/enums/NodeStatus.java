package com.example.Network._service.domain.enums;

public enum NodeStatus {
    ACTIVE("Actif", "Le nœud est actif et opérationnel"),
    INACTIVE("Inactif", "Le nœud est inactif"),
    MAINTENANCE("Maintenance", "Le nœud est en maintenance"),
    OFFLINE("Hors ligne", "Le nœud est hors ligne"),
    SYNCING("Synchronisation", "Le nœud est en synchronisation"),
    ERROR("Erreur", "Le nœud rencontre des erreurs");

    private final String displayName;
    private final String description;

    NodeStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
