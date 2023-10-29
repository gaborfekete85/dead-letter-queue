package guru.learningjournal.examples.kafka.avroposfanout.services;


import com.feketegabor.streaming.avro.model.DeadLetter;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
public class KafkaProducerService {
    @Value("${application.configs.topic.serviceAgreementRetry}")
    private String RETRY_SERVICE_AGRREMENT_TOPIC_NAME;

    @Autowired
    private KafkaTemplate<String, ServiceAgreementDataV2> serviceAgreementV2Template;

    @Autowired
    private KafkaTemplate<String, DeadLetter> deadLetterTemplate;

    public void tombstoneDlt(String eventKey, String topic) {
        ListenableFuture<SendResult<String, DeadLetter>> future = deadLetterTemplate.send(topic, eventKey, null);
        if (Objects.nonNull(future)) {
            future.addCallback(new ListenableFutureCallback<SendResult<String, DeadLetter>>() {
                @Override
                public void onSuccess(SendResult<String, DeadLetter> result) {
                    log.info(String.format("[ TOMBSTONED ] [ SERVICE_AGREEMENT_V2 ] Key: %s Topic: %s, Partition: %s, Offset: %s, Key: %s, Value: %s", eventKey, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), Objects.isNull(result.getProducerRecord().value()) ? Strings.EMPTY : result.getProducerRecord().key(), result.getProducerRecord().value()));
                }
                @Override
                public void onFailure(Throwable ex) {
                    throw new RuntimeException("Event sending failed !");
                }
            });
        }
    }

    public void produceServiceAgreementV2(ServiceAgreementDataV2 serviceAgreementDataV2, String topic, Map<String, String> eventHeaders) {

        RecordHeaders headers = new RecordHeaders();
        eventHeaders.forEach( (k,v) -> {
            headers.add(new RecordHeader(k, v.getBytes()));
        });
        ProducerRecord<String, ServiceAgreementDataV2> producerRecord = new ProducerRecord<>(topic, null, serviceAgreementDataV2.getServiceAgreement().getServiceAgreementId().toString(), serviceAgreementDataV2, headers);
        ListenableFuture<SendResult<String, ServiceAgreementDataV2>> future = serviceAgreementV2Template.send(producerRecord);

        if (Objects.nonNull(future)) {
            future.addCallback(new ListenableFutureCallback<SendResult<String, ServiceAgreementDataV2>>() {
                @Override
                public void onSuccess(SendResult<String, ServiceAgreementDataV2> result) {
                    log.info(String.format("[ PRODUCED ] [ SERVICE_AGREEMENT_V2 ] Topic: %s, Partition: %s, Offset: %s, Key: %s, Value: %s", RETRY_SERVICE_AGRREMENT_TOPIC_NAME, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), result.getProducerRecord().value().getServiceAgreement().getServiceAgreementId(), result.getProducerRecord().value()));
                }
                @Override
                public void onFailure(Throwable ex) {
                    throw new RuntimeException("Event sending failed !");
                }
            });
        }
    }

    public void produceServiceAgreementV2(ServiceAgreementDataV2 serviceAgreementDataV2, String topic) {
        produceServiceAgreementV2(serviceAgreementDataV2, topic, Map.of());
    }

    public void produceServiceAgreementV2(ServiceAgreementDataV2 serviceAgreementDataV2) {
        produceServiceAgreementV2(serviceAgreementDataV2, RETRY_SERVICE_AGRREMENT_TOPIC_NAME);
    }
}
