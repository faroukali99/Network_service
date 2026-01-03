// NetworkNodeRepository.java
package com.example.Network._service.infrastructure.repository;

import com.example.Network._service.domain.enums.MessageType;
import com.example.Network._service.domain.enums.NodeStatus;
import com.example.Network._service.domain.enums.NodeType;
import com.example.Network._service.domain.model.NetworkMessage;
import com.example.Network._service.domain.model.NetworkNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NetworkNodeRepository extends JpaRepository<NetworkNode, Long> {

    Optional<NetworkNode> findByNodeId(String nodeId);

    List<NetworkNode> findByNodeType(NodeType nodeType);

    List<NetworkNode> findByStatus(NodeStatus status);

    @Query("SELECT n FROM NetworkNode n WHERE n.status = :status AND n.nodeType = :type")
    List<NetworkNode> findByStatusAndType(
            @Param("status") NodeStatus status,
            @Param("type") NodeType type
    );

    @Query("SELECT n FROM NetworkNode n WHERE n.isValidator = true AND n.status = 'ACTIVE'")
    List<NetworkNode> findActiveValidators();

    @Query("SELECT n FROM NetworkNode n WHERE n.lastHeartbeat < :threshold AND n.status = 'ACTIVE'")
    List<NetworkNode> findStaleNodes(@Param("threshold") LocalDateTime threshold);

    List<NetworkNode> findByRegion(String region);

    boolean existsByNodeId(String nodeId);

    @Query("SELECT COUNT(n) FROM NetworkNode n WHERE n.status = 'ACTIVE'")
    long countActiveNodes();

    @Query("SELECT n FROM NetworkNode n WHERE n.nodeType = :type ORDER BY n.latencyMs ASC")
    List<NetworkNode> findFastestNodesByType(@Param("type") NodeType type);
}



