package org.da477.SgwClientFree.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Timestamp;

/**
 * MessageEntity class
 * Entity class for storing messages in the database.
 *
 * @author da477
 * @version 1.0
 * @since 5/18/24
 */
@Entity
@Table(name = "MESSAGES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getEffectiveClass(this) != getEffectiveClass(o)) return false;
        return id != null && id.equals(((MessageEntity) o).id);
    }

    @Override
    public int hashCode() {
        return getEffectiveClass(this).hashCode();
    }

    private static Class<?> getEffectiveClass(Object o) {
        return o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    }

}
