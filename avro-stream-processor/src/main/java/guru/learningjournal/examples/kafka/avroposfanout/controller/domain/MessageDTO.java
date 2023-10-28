package guru.learningjournal.examples.kafka.avroposfanout.controller.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDTO {
    String message;
}
