package org.da477.SgwClientFree.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.da477.SgwClientFree.model.MessageDTO;
import org.da477.SgwClientFree.model.MessageType;
import org.da477.SgwClientFree.utils.BankReqTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * HttpClientService class
 * <p>
 * Service class for handling HTTP requests to the bank API.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
@Component
@Slf4j
public class HttpClientService {

    @Value("${bank.key.client.path}")
    private String keystorePath;
    @Value("${bank.key.client.password}")
    private String keystorePassword;
    @Value("${bank.key.alias}")
    private String alias;
    @Value("${bank.api.base-url}")
    private String URL_API;
    @Value("${bank.api.client-id}")
    private String API_KEY;
    @Value("${bank.agreement.id}")
    private String AGREEMENT_ID;

    @Bean
    public CloseableHttpClient httpClient() {
        return createDefaultHttpClient();
    }

    private CloseableHttpClient createDefaultHttpClient() {

        ClassPathResource resource = new ClassPathResource(keystorePath);
        if (!resource.exists()) {
            log.error("Resource is not found: {}", keystorePath);
        }

        return HttpClients.custom()
                .evictExpiredConnections()
                .build();
    }

    private StringEntity getStringEntity(MessageDTO messageDTO, MessageType type) {
        StringEntity stringEntity = new StringEntity(BankReqTemplate.genPing(messageDTO),
                ContentType.APPLICATION_XML);
        if (type == MessageType.STATEMENT) {
            stringEntity = new StringEntity(BankReqTemplate.genStmt(messageDTO),
                    ContentType.APPLICATION_XML);
        }
        return stringEntity;
    }

    private String getUrlForCmd(MessageType messageType, String trackingID) {

        String urlCommand;
        String url = "";
        if (messageType == MessageType.PING) {
            urlCommand = "communication-tests";
            url = URL_API + "/" + urlCommand + "?client_id=" + API_KEY;
        }
        if (messageType == MessageType.STATEMENT) {
            urlCommand = "account-statements";
            url = URL_API + "/" + urlCommand + "?client_id=" + API_KEY;
        }
        if (messageType == MessageType.GET) {
            urlCommand = "messages";
            url = URL_API + "/" + urlCommand + "?client_id=" + API_KEY;
        }
        if (messageType == MessageType.DELETE) {
            url = String.format("%s/messages?trackingId=%s&client_id=%s",
                    URL_API,
                    trackingID,
                    API_KEY);
        }
        return url;
    }

    public ClassicHttpRequest createRequest(HttpMethod method,
                                            MessageDTO messageDTO, MessageType messageType) {

        ClassicHttpRequest request;
        String url = getUrlForCmd(messageType, messageDTO.getTrackingId());
        switch (method.name()) {
            case "GET" -> request = new HttpGet(url);
            case "POST" -> {
                request = new HttpPost(url);
                request.addHeader("X-Agreement-ID", AGREEMENT_ID);
                request.setEntity(getStringEntity(messageDTO, messageType));
            }
            case "DELETE" -> request = new HttpDelete(url);
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        }
        return request;
    }

}
