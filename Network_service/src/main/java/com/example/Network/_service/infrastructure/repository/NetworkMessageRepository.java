package com.example.Network._service.infrastructure.repository;

import com.example.Network._service.domain.enums.MessageType;
import com.example.Network._service.domain.model.NetworkMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NetworkMessageRepository extends JpaRepository<NetworkMessage, Long> {

    List<NetworkMessage> findBySourceNodeId(String sourceNodeId);

    List<NetworkMessage> findByTargetNodeId(String targetNodeId);

    List<NetworkMessage> findByMessageType(MessageType messageType);

    List<NetworkMessage> findByStatus(String status);

    @Query("SELECT m FROM NetworkMessage m WHERE m.status = 'PENDING' AND m.retryCount < :maxRetries")
    List<NetworkMessage> findPendingMessages(@Param("maxRetries") Integer maxRetries);

    @Query("SELECT m FROM NetworkMessage m WHERE m.createdAt < :threshold AND m.status = 'PENDING'")
    List<NetworkMessage> findOldPendingMessages(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT COUNT(m) FROM NetworkMessage m WHERE m.sourceNodeId = :nodeId AND m.createdAt > :since")
    long countMessagesByNodeSince(@Param("nodeId") String nodeId, @Param("since") LocalDateTime since);
}
