package org.da477.SgwClientFree.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

/**
 * MessageEntity class
 *
 * Entity class for storing messages in the database.
 *
 * @author da477
 * @version 1.0
 * @since 5/18/24
 */
@Slf4j
@Entity
@Table(name = "MESSAGES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DATETIME")
    private Timestamp dateTime;

    @Column(name = "REQUESTID")
    private String requestId;

    @Column(name = "TRACKINGID")
    private String trackingId;

    @Column(name = "FILEPATH")
    private String filePath;

    @Column(name = "CODE")
    private Integer code;

    @Enumerated(value = EnumType.STRING)
    private MessageStatus status;

    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    public MessageEntity(MessageDTO messageDTO) {
        this.dateTime = messageDTO.getDateTime();
        this.requestId = messageDTO.getRequestId();
        this.trackingId = messageDTO.getTrackingId();
        this.code = messageDTO.getCode();
        this.status = messageDTO.getStatus();
        this.type = messageDTO.getType();
        this.filePath = messageDTO.getFilePath();
    }

    public boolean isNew() {
        return this.getId() == null || this.getId() == 0;
    }
}
