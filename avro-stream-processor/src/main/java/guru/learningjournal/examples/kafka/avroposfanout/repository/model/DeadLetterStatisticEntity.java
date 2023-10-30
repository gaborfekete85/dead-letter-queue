package guru.learningjournal.examples.kafka.avroposfanout.repository.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Entity
@Data
public class DeadLetterStatisticEntity extends DeadLetterEntity {
    private int numberOfEvents;
}
