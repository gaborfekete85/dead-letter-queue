package guru.learningjournal.examples.kafka.avroposfanout.controller.domain;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class DeadLetterDTO {
//    private UUID id;
    private UUID dltKey;
//    private String message;
    private UUID eventKey;
    private String eventType;
    private String topic;
    private int partition;
    private long partitionOffset;
    private String dataAsJson;
    private String dataAsAvro;
    private String reason;
    private OffsetDateTime createdAt;
}
