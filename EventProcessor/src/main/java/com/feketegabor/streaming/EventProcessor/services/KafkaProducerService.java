package com.feketegabor.streaming.EventProcessor.services;


import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import com.feketegabor.streaming.avro.model.Transaction;
import com.feketegabor.streaming.avro.model.TransactionV2;
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
import java.util.UUID;

@Service
@Log4j2
public class KafkaProducerService {
    @Value("${application.configs.topic.serviceAgreement}")
    private String SERVICE_AGRREMENT_TOPIC_NAME;

    @Value("${application.configs.topic.transaction}")
    private String TRANSACTION_TOPIC_NAME;

    @Value("${application.configs.topic.transactionV2}")
    private String TRANSACTION_TOPIC_NAME_V2;

    @Autowired
    private KafkaTemplate<String, ServiceAgreementDataV2> serviceAgreementV2Template;

    @Autowired
    private KafkaTemplate<String, Transaction> transactionTemplate;

    @Autowired
    private KafkaTemplate<String, TransactionV2> transactionTemplateV2;

    public void tombstoneServiceAgreementV2(String eventKey, String topic) {
        ListenableFuture<SendResult<String, ServiceAgreementDataV2>> future = serviceAgreementV2Template.send(topic, eventKey, null);
        if (Objects.nonNull(future)) {
            future.addCallback(new ListenableFutureCallback<SendResult<String, ServiceAgreementDataV2>>() {
                @Override
                public void onSuccess(SendResult<String, ServiceAgreementDataV2> result) {
                    log.info(String.format("[ TOMBSTONED ] [ SERVICE_AGREEMENT_V2 ] Key: %s Topic: %s, Partition: %s, Offset: %s, Key: %s, Value: %s", eventKey, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), Objects.isNull(result.getProducerRecord().value()) ? Strings.EMPTY : result.getProducerRecord().value().getServiceAgreement().getServiceAgreementId(), result.getProducerRecord().value()));
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
                    log.info(String.format("[ PRODUCED ] [ SERVICE_AGREEMENT_V2 ] Topic: %s, Partition: %s, Offset: %s, Key: %s, Value: %s", SERVICE_AGRREMENT_TOPIC_NAME, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), result.getProducerRecord().value().getServiceAgreement().getServiceAgreementId(), result.getProducerRecord().value()));
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
        produceServiceAgreementV2(serviceAgreementDataV2, SERVICE_AGRREMENT_TOPIC_NAME);
    }


    public Transaction sendTransaction(Transaction transaction, String topic) {
//        UUID.fromString(transaction.getTransactionId().toString())
        ListenableFuture<SendResult<String, Transaction>> future = transactionTemplate.send(topic, UUID.randomUUID().toString(), transaction);
        if (Objects.nonNull(future)) {
            future.addCallback(new ListenableFutureCallback<SendResult<String, Transaction>>() {
                @Override
                public void onSuccess(SendResult<String, Transaction> result) {
                    log.info(String.format("[ PRODUCED ] [ TRANSACTION_V1 ] Topic: %s, Partition: %s, Offset: %s, Key: %s, Value: %s", SERVICE_AGRREMENT_TOPIC_NAME, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), UUID.randomUUID(), result.getProducerRecord().value()));
                }
                @Override
                public void onFailure(Throwable ex) {
                    throw new RuntimeException("[ FAILED PRODUCE ] Transaction event sending failed !");
                }
            });
        }
        return transaction;
    }

    public Transaction sendTransaction(Transaction transaction) {
        return sendTransaction(transaction, TRANSACTION_TOPIC_NAME);
    }

    public TransactionV2 sendTransactionV2(TransactionV2 transactionV2, String topic, Map<String, String> eventHeaders) {
        RecordHeaders headers = new RecordHeaders();
        eventHeaders.forEach( (k,v) -> {
            headers.add(new RecordHeader(k, v.getBytes()));
        });
        ProducerRecord<String, TransactionV2> producerRecord = new ProducerRecord<>(topic, null, UUID.randomUUID().toString(), transactionV2, headers);
        ListenableFuture<SendResult<String, TransactionV2>> future = transactionTemplateV2.send(producerRecord);

//        ListenableFuture<SendResult<String, TransactionV2>> future = transactionTemplateV2.send(topic, UUID.randomUUID().toString(), transactionV2);
        if (Objects.nonNull(future)) {
            future.addCallback(new ListenableFutureCallback<SendResult<String, TransactionV2>>() {
                @Override
                public void onSuccess(SendResult<String, TransactionV2> result) {
                    log.info(String.format("[ PRODUCED ] [ TRANSACTION_V2 ] Topic: %s, Partition: %s, Offset: %s, Key: %s, Value: %s", SERVICE_AGRREMENT_TOPIC_NAME, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), UUID.randomUUID(), result.getProducerRecord().value()));
                }
                @Override
                public void onFailure(Throwable ex) {
                    throw new RuntimeException("[ FAILED PRODUCE ] Transaction event sending failed !");
                }
            });
        }
        return transactionV2;
    }

    public TransactionV2 sendTransactionV2(TransactionV2 transactionV2, String topic) {
        ListenableFuture<SendResult<String, TransactionV2>> future = transactionTemplateV2.send(topic, UUID.randomUUID().toString(), transactionV2);
        if (Objects.nonNull(future)) {
            future.addCallback(new ListenableFutureCallback<SendResult<String, TransactionV2>>() {
                @Override
                public void onSuccess(SendResult<String, TransactionV2> result) {
                    log.info(String.format("[ PRODUCED ] [ TRANSACTION_V2 ] Topic: %s, Partition: %s, Offset: %s, Key: %s, Value: %s", SERVICE_AGRREMENT_TOPIC_NAME, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), UUID.randomUUID(), result.getProducerRecord().value()));
                }
                @Override
                public void onFailure(Throwable ex) {
                    throw new RuntimeException("[ FAILED PRODUCE ] Transaction event sending failed !");
                }
            });
        }
        return transactionV2;
    }

    public TransactionV2 sendTransactionV2(TransactionV2 transaction) {
        return sendTransactionV2(transaction, TRANSACTION_TOPIC_NAME_V2);
    }

//    private void validate(ServiceAgreementDataV2 serviceAgreementDataV2) {
//        if(Objects.isNull(serviceAgreementDataV2) || Objects.isNull(serviceAgreementDataV2.getServiceAgreement()) || Objects.isNull(serviceAgreementDataV2.getServiceAgreement().getServiceAgreementId())) {
//            throw new RuntimeException("The service Agreement ID key field is missing. Unable to produce event. ");
//        }
//    }
}
