package guru.learningjournal.examples.kafka.avroposfanout.controller.domain;

import lombok.Data;

@Data
public class RetryDto {
    private String topic;
    private String dltTopicEventKey;
}
