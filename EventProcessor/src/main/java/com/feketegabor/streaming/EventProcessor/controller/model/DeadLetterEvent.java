package com.feketegabor.streaming.EventProcessor.controller.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class DeadLetterEvent {
    private UUID id;
    private UUID eventKey;
    private UUID eventType;
    private String topic;
    private int partition;
    private long partitionOffset;
    private String dataAsJson;
    private String dataAsAvro;
    private String reason;
    private OffsetDateTime createdAt;
}
