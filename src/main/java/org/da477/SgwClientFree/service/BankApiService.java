package org.da477.SgwClientFree.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.da477.SgwClientFree.model.MessageDTO;
import org.da477.SgwClientFree.model.MessageEntity;
import org.da477.SgwClientFree.model.MessageStatus;
import org.da477.SgwClientFree.model.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * BankApiService class
 * <p>
 * Service class for handling bank API interactions such as sending requests and processing responses.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
@Service
@Slf4j
public class BankApiService {

    @Value("${schedule.cron.enabled}")
    private boolean SCHEDULE_ENABLED;
    @Value("${bank.account.iban}")
    private String IBAN;

    private final HttpClientService httpClientService;
    private final CloseableHttpClient httpClient;
    private final DbService dbService;

    @Autowired
    public BankApiService(HttpClientService httpClientService, CloseableHttpClient httpClient, DbService dbService) {
        this.httpClientService = httpClientService;
        this.httpClient = httpClient;
        this.dbService = dbService;
    }

    @Async
    @Scheduled(cron = "${schedule.cron.messages}", zone = "${spring.jackson.time-zone}")
    protected void autoGetMessages() {
        if (!SCHEDULE_ENABLED) {
            log.warn("The schedule is disabled. AutoGetMessages has not started.");
            return;
        }
        log.info("Start autoGetMessage() in thread: {}", Thread.currentThread().getName());
        try {
            getMessages();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "${schedule.cron.statement}", zone = "${spring.jackson.time-zone}")
    protected void autoRequestStatement() {
        if (!SCHEDULE_ENABLED) {
            log.warn("The schedule is disabled. autoRequestStatement() has not started.");
            return;
        }
        log.info("Start autoRequestStatement() in thread: {}", Thread.currentThread().getName());
        requestStatement(null, null);
    }

    public String requestStatement(LocalDate from, LocalDate to) {

        MessageDTO messageDTO = createMessage(MessageType.STATEMENT, from, to);

        try (CloseableHttpResponse response =
                     httpClient
                             .execute(httpClientService.createRequest(
                                     HttpMethod.POST,
                                     messageDTO,
                                     MessageType.STATEMENT))) {

            int statusCode = response.getCode();
            messageDTO.setCode(statusCode);

            switch (statusCode / 100) {
                case 2 -> {
                    messageDTO.setStatus(MessageStatus.SENT);
                    messageDTO.setTrackingId(response.getHeader("X-Tracking-ID").getValue());
                    dbService.saveUpdateMessage(messageDTO);
                }
                case 4 -> {
                    messageDTO.setStatus(MessageStatus.ERROR);
                    messageDTO.setDesc("Error sent messageDTO: " + EntityUtils.toString(response.getEntity()));
                    if (statusCode == 400) {
                        messageDTO.setDesc("Bad request (no X-Request-ID).");
                    } else if (statusCode == 403) {
                        messageDTO.setDesc("Forbidden (wrong X-Agreement-ID, expired certificates, no certificates, etc.).");
                    }
                }
                case 5 -> {
                    messageDTO.setStatus(MessageStatus.ERROR);
                    messageDTO.setDesc("Server error");
                }
                default -> {
                    messageDTO.setStatus(MessageStatus.ERROR);
                    messageDTO.setDesc("Unexpected response");
                }
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching messages: {}", e.getMessage());
        }
        dbService.saveUpdateMessage(messageDTO);
        return messageDTO.toString();
    }

    public String ping() {

        MessageDTO msgDto = createMessage(MessageType.PING);

        try (CloseableHttpResponse response =
                     httpClient
                             .execute(httpClientService.createRequest(
                                     HttpMethod.POST,
                                     msgDto,
                                     MessageType.PING))) {

            int statusCode = response.getCode();
            msgDto.setCode(statusCode);

            if (statusCode < 300 && statusCode >= 200) {
                msgDto.setStatus(MessageStatus.SENT);
                msgDto.setTrackingId(response.getHeader("X-Tracking-ID").getValue());
            }
            dbService.saveUpdateMessage(msgDto);
        } catch (Exception e) {
            log.error("An error occurred while sending PING: {}", e.getMessage());
        }
        return msgDto.toString();
    }

    public Future<String> getMessages() throws Exception {

        MessageEntity orgStmMsg = null;
        MessageDTO messageDTO = createMessage(MessageType.GET);

        try (CloseableHttpResponse response =
                     httpClient
                             .execute(httpClientService.createRequest(
                                     HttpMethod.GET,
                                     messageDTO,
                                     MessageType.GET))) {

            int statusCode = response.getCode();
            messageDTO.setCode(statusCode);

            if (statusCode < 300 && statusCode >= 200)
                messageDTO.setStatus(MessageStatus.SENT);

            Header bankHeaderID = response.getHeader("X-Tracking-ID");
            if (bankHeaderID != null) {
                Optional<MessageEntity> optOriginalMessage = dbService
                        .findStmtByTrackingId(bankHeaderID.getValue());

                // now We can do something with original Message
                if (optOriginalMessage.isPresent())
                    orgStmMsg = optOriginalMessage.get();

                messageDTO.setTrackingId(bankHeaderID.getValue());
                dbService.saveUpdateMessage(messageDTO);
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseBody = EntityUtils.toString(entity);
            } else {
                messageDTO.setDesc("There are no new messages from Bank at that moment.");
            }
            EntityUtils.consume(entity);

        } catch (IOException e) {
            messageDTO.setStatus(MessageStatus.ERROR);
            dbService.saveUpdateMessage(messageDTO);
            log.error("An error occurred while fetching messages: {}", e.getMessage());
        }
        return new AsyncResult<>(messageDTO.toString());
    }

    private void deleteMessageById(MessageDTO dto) {

        if (dto.getTrackingId() == null || dto.getTrackingId().isEmpty())
            return;

        try (CloseableHttpResponse response =
                     httpClient
                             .execute(httpClientService.createRequest(
                                     HttpMethod.DELETE,
                                     dto,
                                     MessageType.DELETE))) {

            int statusCode = response.getCode();
            dto.setCode(statusCode);

            switch (statusCode / 100) {
                case 2 -> {
                    dto.setStatus(MessageStatus.DELETED);
                    dto.setDesc("Message successfully deleted with ID: " + dto.getTrackingId());
                }
                case 4 -> {
                    dto.setStatus(MessageStatus.ERROR);
                    dto.setDesc("Error deleting messageDTO: " + EntityUtils.toString(response.getEntity()));
                    if (statusCode == 400) {
                        dto.setDesc("Bad request (no X-Request-ID).");
                    } else if (statusCode == 403) {
                        dto.setDesc("Forbidden (wrong X-Agreement-ID, expired certificates, no certificates, etc.).");
                    }
                }
                case 5 -> {
                    dto.setStatus(MessageStatus.ERROR);
                    dto.setDesc("Server error");
                }
                default -> {
                    dto.setStatus(MessageStatus.ERROR);
                    dto.setDesc("Unexpected response");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        dbService.saveUpdateMessage(dto);
    }

    public MessageDTO createMessage(MessageType type) {
        return createMessage(type, null, null);
    }

    public MessageDTO createMessage(MessageType type, LocalDate fromDate, LocalDate toDate) {
        return new MessageDTO(type, MessageStatus.NEW, IBAN, fromDate, toDate);
    }

}
