package com.feketegabor.streaming.EventProcessor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dead_letters")
@Data
@Builder
@AllArgsConstructor
public class DeadLetterEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    
    @Column(name = "event_key")
    private UUID eventKey;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "topic")
    private String topic;

    @Column(name = "partition")
    private int partition;

    @Column(name = "partition_offset")
    private String partitionOffset;

    @Column(name = "data_as_json")
    private String dataAsJson;

    @Column(name = "data_as_avro")
    private String dataAsAvro;

    @Column(name = "data_as_avro_byte")
    private String dataAsAvroByte;

    @Column(name = "reason")
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public DeadLetterEntity() {
    }
}
