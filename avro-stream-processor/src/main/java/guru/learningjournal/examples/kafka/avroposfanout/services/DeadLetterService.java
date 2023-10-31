package guru.learningjournal.examples.kafka.avroposfanout.services;

import com.feketegabor.streaming.avro.model.DeadLetter;
import guru.learningjournal.examples.kafka.avroposfanout.repository.DltJdbcRepository;
import guru.learningjournal.examples.kafka.avroposfanout.repository.DltRepository;
import guru.learningjournal.examples.kafka.avroposfanout.repository.model.DeadLetterEntity;
import guru.learningjournal.examples.kafka.avroposfanout.repository.model.DeadLetterStatisticEntity;
import guru.learningjournal.examples.kafka.avroposfanout.util.Kafkautil;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class DeadLetterService {

    private final DltRepository deadLetterRepository;
    private final DltJdbcRepository dltJdbcRepository;

    public List<DeadLetterStatisticEntity> getEventsByServiceId(List<String> serviceIds, List<String> priorities, LocalDateTime from, LocalDateTime to) {
        return dltJdbcRepository.getEventCountByService(serviceIds, priorities, from, to);
    }

    public DeadLetterEntity persistDlt(String key, DeadLetter value) {
        DeadLetterEntity entity = DeadLetterEntity.builder()
                .eventKey(UUID.fromString(key))
                .serviceId(value.getServiceId().toString())
                .eventType(value.getEventType().toString())
                .topic(value.getTopic().toString())
                .partition(Integer.valueOf(value.getPartition().toString()))
                .partitionOffset(value.getOffset().toString())
                .dataAsJson(value.getJson().toString())
                .dataAsAvro(value.getAvro().toString())
                .reason(value.getReason().toString())
                .eventPriority(value.getPriority().name())
                .build();

        return deadLetterRepository.saveAndFlush(entity);
    }


    private <T> DeadLetterEntity persistDlt(ConsumerRecord<String, T> event, org.apache.avro.Schema schema, Class<T> cls) {
        byte[] serializedAvro = Kafkautil.serializeAvro(event, schema);
//        String serializedAvroAsString = new String(serializedAvro);
        String reason = getDltException(event);
        DeadLetterEntity entity = DeadLetterEntity.builder()
                .eventKey(UUID.fromString(event.key()))
                .serviceId(getHeader(Kafkautil.DLT_SERVICE_ID, event))
                .eventType(cls.getName())
                .topic(getHeader(Kafkautil.DLT_ORIGINAL_TOPIC, event))
                .partition(Integer.valueOf(getHeader(Kafkautil.DLT_ORIGINAL_PARTITION, event)))
                .partitionOffset(getHeader(Kafkautil.DLT_ORIGINAL_OFFSET, event))
                .dataAsJson(event.value().toString())
                .dataAsAvro(Base64.getEncoder().encodeToString(serializedAvro))
//                .dataAsAvroByte(serializedAvroAsString)
                .reason(reason)
                .build();

        return deadLetterRepository.saveAndFlush(entity);
    }

    private String getDltException(ConsumerRecord<?, ?> r) {
        return getHeader(Kafkautil.DLT_REASON, r);
    }

    private String getHeader(String key, ConsumerRecord<?, ?> r) {
        Iterator<Header> iterator = r.headers().iterator();
        while(iterator.hasNext()) {
            Header current = iterator.next();
            if(key.equals(current.key())) {
                return new String(current.value(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
