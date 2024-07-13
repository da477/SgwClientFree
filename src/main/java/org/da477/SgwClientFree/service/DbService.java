package org.da477.SgwClientFree.service;

import lombok.extern.slf4j.Slf4j;
import org.da477.SgwClientFree.model.MessageDTO;
import org.da477.SgwClientFree.model.MessageEntity;
import org.da477.SgwClientFree.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.da477.SgwClientFree.utils.FileHelper.CAPACITY;

/**
 * DbService class
 * <p>
 * Service class for handling database operations related to messages.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
@Service
@Slf4j
public class DbService {

    private final MessageRepository repo;

    @Autowired
    public DbService(MessageRepository repo) {
        this.repo = repo;
    }

    public void saveUpdateMessage(MessageDTO msgDTO) {
        MessageEntity existMsg = repo.findByRequestId(msgDTO.getRequestId())
                .orElse(dtoToEntity(msgDTO));

        existMsg.setCode(msgDTO.getCode());
        existMsg.setStatus(msgDTO.getStatus());

        if (msgDTO.getTrackingId() != null && !msgDTO.getTrackingId().isEmpty())
            existMsg.setTrackingId(msgDTO.getTrackingId());

        if (msgDTO.getFilePath() != null && !msgDTO.getFilePath().isEmpty()) {
            existMsg.setFilePath(msgDTO.getFilePath());
        }
        repo.save(existMsg);
    }

    public Optional<MessageEntity> findStmtByTrackingId(String id) {
        return repo.findStmtByTrackingId(id);
    }

    public MessageEntity dtoToEntity(MessageDTO messageDTO) {
        return new MessageEntity(messageDTO);
    }

    public List<MessageEntity> topByLastUpdate() {
        return repo.findAll(
                        PageRequest.of(0, CAPACITY,
                                Sort.Direction.DESC,
                                "dateTime"))
                .getContent();
    }

}
