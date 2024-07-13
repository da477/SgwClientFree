package org.da477.SgwClientFree.repository;

import org.da477.SgwClientFree.model.MessageEntity;
import org.da477.SgwClientFree.model.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * MessageRepository class
 * <p>
 * Repository interface for handling CRUD operations on Message entities.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
@Repository
@Transactional
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    Optional<MessageEntity> findByTrackingId(String trackingId);

    @Query("SELECT m FROM MessageEntity m WHERE m.trackingId LIKE CONCAT(:trackingId, '%') AND m.type = 'STATEMENT'")
    Optional<MessageEntity> findStmtByTrackingId(@Param("trackingId") String trackingId);

    Optional<MessageEntity> findByRequestId(String requestId);

    Optional<MessageEntity> findFirstByTypeOrderByDateTimeDesc(MessageType type);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM MessageEntity m WHERE m.dateTime <= :cutoffDate")
    int deleteOlderMessages(@Param("cutoffDate") Date cutoffDate);

}
