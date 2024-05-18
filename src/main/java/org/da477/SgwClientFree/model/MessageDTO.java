package org.da477.SgwClientFree.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.sql.Timestamp;

/**
 * MessageDTO class
 *
 * DTO for transferring message data between processes.
 *
 * @author da477
 * @version 1.0
 * @since 5/18/24
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private String iban;
    private String _date;
    private String messageId;
    private String creDtTm;

    private String fromDate;
    private String fromTime;

    private String toDate;
    private String toTime;

    private Timestamp dateTime;
    private String requestId;
    private String trackingId;
    private String filePath;
    private Integer code;
    private MessageStatus status;
    private MessageType type;
    private String desc;

    public void setFilePath(Path filePath) {
        this.filePath = filePath.getFileName().toString();
        log.info("The response has been saved to: {}", filePath.toAbsolutePath());
    }

    @Override
    public String toString() {
        return (desc != null ? desc + ", " : "") +
                "IBAN: " + iban +
                ", messageId='" + messageId + '\'' +
                ", creationDateTime='" + creDtTm + '\'' +
                ", fromDateTime=" + fromDate + " " + fromTime +
                ", toDateTime=" + toDate + " " + toTime +
                ", requestId='" + requestId + '\'' +
                ", trackingId='" + trackingId + '\'' +
                (filePath != null ? ", filePath='" + filePath + '\'' : "") +
                ", code=" + code +
                ", status='" + status + '\'' +
                ", type='" + type + '\'';
    }

}
