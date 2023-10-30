package mapper;

import com.feketegabor.streaming.EventProcessor.repository.model.DeadLetterEntity;
import com.feketegabor.streaming.EventProcessor.util.Kafkautil;
import com.feketegabor.streaming.avro.model.DeadLetter;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class DeadLetterEntityMapper {
    public static DeadLetter mapToAvro(DeadLetterEntity deadLetterEntity) {
        return DeadLetter.newBuilder()
                .setEventKey(deadLetterEntity.getEventKey().toString())
                .setEventType(deadLetterEntity.getEventType())
                .setTopic(deadLetterEntity.getTopic())
                .setPartition(String.valueOf(deadLetterEntity.getPartition()))
                .setOffset(String.valueOf(deadLetterEntity.getPartitionOffset()))
                .setJson(deadLetterEntity.getDataAsJson())
                .setAvro(deadLetterEntity.getDataAsAvro())
                .setReason(deadLetterEntity.getReason())
                .setCreatedAt(Objects.isNull(deadLetterEntity.getCreatedAt()) ? "" : deadLetterEntity.getCreatedAt().toInstant(ZoneOffset.UTC).toString())
                .build();
    }

    public static <T> DeadLetter mapToAvro(T event, org.apache.avro.Schema schema, Class<T> cls, Map<String, String> params, String serviceId) {
        byte[] serializedAvro = Kafkautil.serializeAvro(event, schema);

        return DeadLetter.newBuilder()
                .setServiceId(serviceId)
                .setEventKey(params.get(Kafkautil.DLT_ORIGINAL_EVENT_KEY))
                .setEventType(cls.getName())
                .setTopic(params.get(Kafkautil.DLT_ORIGINAL_TOPIC))
                .setPartition(params.get(Kafkautil.DLT_ORIGINAL_PARTITION))
                .setOffset(params.get(Kafkautil.DLT_ORIGINAL_OFFSET))
                .setJson(event.toString())
                .setAvro(Base64.getEncoder().encodeToString(serializedAvro))
                .setReason(params.get(Kafkautil.DLT_REASON))
                .setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toString())
                .build();
    }
}
