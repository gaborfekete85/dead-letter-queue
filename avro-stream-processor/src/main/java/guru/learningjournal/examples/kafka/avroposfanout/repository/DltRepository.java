package guru.learningjournal.examples.kafka.avroposfanout.repository;

import guru.learningjournal.examples.kafka.avroposfanout.repository.model.DeadLetterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DltRepository extends JpaRepository<DeadLetterEntity, UUID> {
}
