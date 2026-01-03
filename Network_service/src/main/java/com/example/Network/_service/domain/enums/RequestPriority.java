package com.example.Network._service.domain.enums;

public enum RequestPriority {
    LOW(1),
    NORMAL(2),
    HIGH(3),
    CRITICAL(4);

    private final int level;

    RequestPriority(int level) {
        this.level = level;
    }

    public int getLevel() { return level; }
}
