package com.bnq.api.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bnq.api.entity.Message;
import com.bnq.api.entity.MessageError;
import com.bnq.api.repository.MessageErrorRepository;
import com.bnq.api.repository.MessageRepository;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class BatchMessageWriter {

    private final BlockingQueue<Message> messageQueue;
    private final BlockingQueue<Message> fallbackQueue;
    private final MessageRepository messageRepository;
    private final MessageErrorRepository errorRepository;
    private final MeterRegistry meterRegistry;
    private final int batchSize;

    public BatchMessageWriter(
            MessageRepository messageRepository,
            MessageErrorRepository errorRepository,
            MeterRegistry meterRegistry,
            @Value("${app.batch.size:100}") int batchSize
    ) {
        this.messageRepository = messageRepository;
        this.errorRepository = errorRepository;
        this.meterRegistry = meterRegistry;
        this.batchSize = batchSize;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.fallbackQueue = new LinkedBlockingQueue<>();
        
        // Register queue size gauge
        meterRegistry.gauge("mq.buffer.size", messageQueue, BlockingQueue::size);
    }

    public void enqueue(Message message) {
        try {
            boolean offered = messageQueue.offer(message, 5, TimeUnit.SECONDS);
            if (!offered) {
                log.warn("Failed to enqueue message {} - queue full or timeout", message.getMessageId());
                fallbackQueue.offer(message);
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while enqueueing message {}", message.getMessageId(), e);
            Thread.currentThread().interrupt();
        }
    }

    @Scheduled(fixedDelayString = "${app.batch.delay:5000}")
    @Timed(value = "mq.flush.time", description = "Time taken to flush messages")
    @Transactional
    public void flush() {
        List<Message> batch = new ArrayList<>();
        messageQueue.drainTo(batch, batchSize);

        if (batch.isEmpty()) {
            return;
        }

        log.debug("Flushing {} messages", batch.size());

        try {
            messageRepository.saveAll(batch);
            log.info("Successfully flushed {} messages", batch.size());
        } catch (Exception e) {
            log.error("Error flushing batch of {} messages", batch.size(), e);
            handleBatchError(batch, e);
        }

        // Process fallback queue if main queue is processed
        processFallbackQueue();
    }

    public void saveErrorMessage(String idMessage, Exception error) {
        try {
            MessageError messageError = new MessageError();
            messageError.setMessageId(idMessage);
            messageError.setErrorMessage(error.getMessage());
            messageError.setStackTrace(getStackTrace(error));
            errorRepository.save(messageError);
            log.info("Saved error record for message {}", messageError.getMessageId());
        } catch (Exception e) {
            log.error("Failed to save error record for message {}", idMessage, e);
        }
    }

    private void handleBatchError(List<Message> failedBatch, Exception error) {
        for (Message message : failedBatch) {
            try {
                saveErrorMessage(message.getMessageId(), error);
                fallbackQueue.offer(message);
            } catch (Exception e) {
                log.error("Failed to save error record for message {}", message.getMessageId(), e);
            }
        }
    }

    private void processFallbackQueue() {
        List<Message> fallbackBatch = new ArrayList<>();
        fallbackQueue.drainTo(fallbackBatch, batchSize);

        if (!fallbackBatch.isEmpty()) {
            try {
                messageRepository.saveAll(fallbackBatch);
                log.info("Successfully processed {} messages from fallback queue", fallbackBatch.size());
            } catch (Exception e) {
                log.error("Error processing fallback queue batch", e);
                // Messages will remain in error table
            }
        }
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}