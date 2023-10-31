package com.feketegabor.streaming.EventProcessor.listener;

import com.feketegabor.streaming.EventProcessor.repository.TransactionDltRepository;
import com.feketegabor.streaming.EventProcessor.repository.model.DeadLetterEntity;
import com.feketegabor.streaming.EventProcessor.services.KafkaProducerService;
import com.feketegabor.streaming.EventProcessor.util.Kafkautil;
import com.feketegabor.streaming.avro.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.*;

@Component
@Configuration
@Slf4j
public class ServiceAgreementListener {

    @Autowired
    ConsumerFactory consumerFactory;

    @Value("${application.configs.topic.serviceAgreement}")
    private String saTopic;

    @Value("${application.configs.topic.serviceAgreementDlt}")
    private String dltTopic;

    @Value("${application.configs.simulateError}")
    private boolean simulateError;

    @Value("${application.configs.topic.transactionV2Dlt}")
    private String TRANASCTION_V2_DLT;

    @Value("${spring.application.name}")
    private String applicationId;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private TransactionDltRepository transactionDltRepository;

    @Autowired
    private TransactionEventHandler transactionEventHandler;

    @Autowired
    private ServiceAgreementEventHandler serviceAgreementEventHandler;

    @Autowired
    private DeadLetterHandler deadLetterHandler;

    private static final Random random = new Random();

    @KafkaListener(topics = "#{'${application.configs.topic.serviceAgreementRetry}'}")
    public void serviceAgreementRetry(ConsumerRecord<String, ServiceAgreementDataV2> event) {
        serviceAgreementReceived(event);
    }

    @KafkaListener(topics = "#{'${application.configs.topic.serviceAgreement}'}")
    public void serviceAgreementReceived(ConsumerRecord<String, ServiceAgreementDataV2> event) {
        try {
            String key = event.key();
            String dltTopicKey = getHeader(Kafkautil.DLT_TOPIC_EVENT_KEY, event);
//             && Character.isDigit(key.toString().charAt(0))
            if(simulateError && Objects.isNull(dltTopicKey)) {
                throw new RuntimeException("Key Starts with Digit");
            } else {
                log.info("[ CONSUMED ] Service Agreement received on topic {}, Partition: {}, Offset: {}, Event: {}", event.topic(), event.partition(), event.offset(), event.value());
                if(Objects.nonNull(dltTopicKey)) {
                    kafkaProducerService.tombstoneEvent(dltTopicKey, dltTopic + "_avro");
//                    kafkaProducerService.tombstoneServiceAgreementV2(dltTopicKey, dltTopic);
                }

//                CompletableFuture
//                        .runAsync( () -> kafkaProducerService.tombstoneServiceAgreementV2(event.key(), dltTopic))
//                        .exceptionally(
//                                throwable -> {
//                                    final Throwable t = ExceptionUtils.getRootCause(throwable);
//                                    log.error("Unable to tombstone event: {}", event.key(), t);
//                                    return null;
//                                }
//                        );
            }
        } catch (Throwable t) {
            log.error("ERR_PCO_2001: [ KAFKA ] Unexpected error during consuming the event.", t);
            event.headers().add(Kafkautil.DLT_TOPIC_NAME_HEADER, dltTopic.getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_EVENT_KEY, event.key().getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_EVENT_TYPE, event.value().getClass().getName().getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_TOPIC, saTopic.getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_PARTITION, String.valueOf(event.partition()).getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_OFFSET, String.valueOf(event.offset()).getBytes());
            event.headers().add(Kafkautil.DLT_REASON, (t.getMessage() + ": \n" + ExceptionUtils.getStackTrace(t)).getBytes());
            event.headers().add(Kafkautil.DLT_PRIORITY, PriorityEnum.HIGH.name().getBytes());
            throw t;
        }
    }

    @KafkaListener(topics = "#{'${application.configs.topic.transaction}'}")
    public void transactionListener(ConsumerRecord<String, Transaction> event) {
        try {
            String key = event.key();
            log.info("[ CONSUMED ] Transaction received on topic {}, Partition: {}, Offset: {}, Event: {}", event.topic(), event.partition(), event.offset(), event.value());
        } catch (Throwable t) {
            log.error("ERR_PCO_2001: [ KAFKA ] Unexpected error during consuming the event.", t);
            event.headers().add(Kafkautil.DLT_TOPIC_NAME_HEADER, dltTopic.getBytes());
            throw t;
        }
    }

    @KafkaListener(topics = "#{'${application.configs.topic.transactionV2}'}")
    public void transactionListenerV2(ConsumerRecord<String, TransactionV2> event) {
        try {
            String key = event.key();
            log.info("[ CONSUMED ] Transaction (V2) received on topic {}, Partition: {}, Offset: {}, Event: {}", event.topic(), event.partition(), event.offset(), event.value());
            throw new RuntimeException("Unextecped Exception occured");
        } catch (Throwable t) {
            log.error("ERR_PCO_2001: [ KAFKA ] Unexpected error during consuming the event.", t);
            event.headers().add(Kafkautil.DLT_TOPIC_NAME_HEADER, TRANASCTION_V2_DLT.getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_EVENT_KEY, event.key().getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_EVENT_TYPE, event.value().getClass().getName().getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_TOPIC, saTopic.getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_PARTITION, String.valueOf(event.partition()).getBytes());
            event.headers().add(Kafkautil.DLT_ORIGINAL_OFFSET, String.valueOf(event.offset()).getBytes());
            event.headers().add(Kafkautil.DLT_REASON, (t.getMessage() + ": \n" + ExceptionUtils.getStackTrace(t)).getBytes());
            throw t;
        }
    }

    @KafkaListener(topics = "#{'${application.configs.topic.serviceAgreementDlt}'}")
    public void serviceAgreementV2DeadLetterListener(ConsumerRecord<String, ServiceAgreementDataV2> event) {
        if(Objects.isNull(event) || Objects.isNull(event.value())) {
            return;
        }
        try {
            serviceAgreementEventHandler.handle(event.value());
            byte[] avroBytes = Kafkautil.serializeAvro(event, event.value().getSchema());
            String serialized = new String(avroBytes, StandardCharsets.UTF_8);
            ServiceAgreementDataV2 sa = Kafkautil.deserializeAvro(avroBytes, event.value().getSchema(), ServiceAgreementDataV2.class);
            DeadLetterEntity dle = persistDlt(event, event.value().getSchema(), ServiceAgreementDataV2.class);
            deadLetterHandler.handle(this.mapToAvro(dle));
//            deadLetterHandler.handle(dle);
        } catch (Throwable t) {
            log.error("ERR_PCO_2001: [ KAFKA ] Unexpected error during consuming the event.", t);
            event.headers().add(Kafkautil.DLT_TOPIC_NAME_HEADER, TRANASCTION_V2_DLT.getBytes());
        }
    }

    public DeadLetter mapToAvro(DeadLetterEntity deadLetterEntity) {
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

    private <T> DeadLetterEntity persistDlt(ConsumerRecord<String, T> event, org.apache.avro.Schema schema, Class<T> cls) {
        byte[] serializedAvro = Kafkautil.serializeAvro(event, schema);
//        String serializedAvroAsString = new String(serializedAvro);
        String reason = getDltException(event);
        DeadLetterEntity entity = DeadLetterEntity.builder()
                .eventKey(UUID.fromString(event.key()))
                .eventType(cls.getName())
                .topic(getHeader(Kafkautil.DLT_ORIGINAL_TOPIC, event))
                .partition(Integer.valueOf(getHeader(Kafkautil.DLT_ORIGINAL_PARTITION, event)))
                .partitionOffset(getHeader(Kafkautil.DLT_ORIGINAL_OFFSET, event))
                .dataAsJson(event.value().toString())
                .dataAsAvro(Base64.getEncoder().encodeToString(serializedAvro))
//                .dataAsAvroByte(serializedAvroAsString)
                .reason(reason)
                .build();

        return transactionDltRepository.saveAndFlush(entity);
    }

    @KafkaListener(topics = "#{'${application.configs.topic.transactionV2Dlt}'}")
    public void transactionDeadLetterListenerV2(ConsumerRecord<String, TransactionV2> event) {
        try {
            transactionEventHandler.handle(event.value());
            String key = event.key();
            TransactionV2 value = event.value();
            String exception = getDltException(event);
            String toPersist = event.value().toString();

            byte[] result = serializeAvroHttpRequestJSON(event.value());
            String serialized = new String(result, StandardCharsets.UTF_8);
            TransactionV2 transactionV2Deserialized = deserialize(serialized.getBytes());
            System.out.println("result: " + transactionV2Deserialized);

            byte[] serializedAvro = serializeAvro(event.value());
            String serializedAvroAsString = new String(serializedAvro, StandardCharsets.UTF_8);
            System.out.println(serializedAvroAsString);

            TransactionV2 newEvent = deserializeAvro(serializedAvro);
            System.out.println("Deserialized: " + newEvent.toString());

            persist(event);

//            String schema = getSchemaAsString("TransactionV2.avsc");
//            DatumWriter<TransactionV2> transactionDatumWriter = new SpecificDatumWriter<TransactionV2>(TransactionV2.class);
//
//            DataFileWriter<TransactionV2> dataFileWriter = new DataFileWriter<TransactionV2>(transactionDatumWriter);
//
//            dataFileWriter.create(event.value().getSchema(), new File("users.avro"));
//            dataFileWriter.append(event.value());
//            dataFileWriter.close();

//            String schema = getSchemaAsString("TransactionV2.avsc");
//            byte[] avroToPersist = Kafkautil.jsonToAvro(toPersist, schema);
//            System.out.println("avroToPersist: " + new String(avroToPersist, StandardCharsets.UTF_8));
        } catch (Throwable t) {
            log.error("ERR_PCO_2001: [ KAFKA ] Unexpected error during consuming the event.", t);
            event.headers().add(Kafkautil.DLT_TOPIC_NAME_HEADER, TRANASCTION_V2_DLT.getBytes());
        }
    }

    private DeadLetterEntity persist(ConsumerRecord<String, TransactionV2> event) throws IOException {
        byte[] serializedAvro = serializeAvro(event.value());
        String serializedAvroAsString = new String(serializedAvro, StandardCharsets.UTF_8);
        String reason = getDltException(event);
        DeadLetterEntity entity = DeadLetterEntity.builder()
                .eventKey(UUID.fromString(event.key()))
                .dataAsJson(event.value().toString())
//                .dataAsAvro(Base64.getEncoder().encodeToString(serializedAvro))
//                Caused by: org.postgresql.util.PSQLException: ERROR: invalid byte sequence for encoding "UTF8": 0x00
//                Where: unnamed portal parameter $3
//                .dataAsAvroByte(serializedAvroAsString)
                .reason(reason)
                .build();

        return transactionDltRepository.saveAndFlush(entity);
    }

    private byte[] serializeAvro(TransactionV2 transactionV2) throws IOException {
        DatumWriter<TransactionV2> writer = new SpecificDatumWriter<>(TransactionV2.getClassSchema());
//        DatumWriter<TransactionV2> writer = new SpecificDatumWriter<TransactionV2>(TransactionV2.class);

        // 3. Create a ByteArrayOutputStream to hold the serialized data
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 4. Create a BinaryEncoder to write the data to the ByteArrayOutputStream
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

        // 5. Use the SpecificDatumWriter to serialize your Avro object into the ByteArrayOutputStream
        writer.write(transactionV2, encoder);
        encoder.flush();

        // Now, your Avro object is serialized in memory, and you can access the serialized data as a byte array
        byte[] serializedData = byteArrayOutputStream.toByteArray();
        return serializedData;
    }

    public TransactionV2 deserializeAvro(byte[] data) throws IOException {
        TransactionV2 deserializedRecord = new TransactionV2();
        DatumReader<TransactionV2> reader = new SpecificDatumReader<>(TransactionV2.getClassSchema());
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        reader.read(deserializedRecord, decoder);
        return deserializedRecord;
    }

    public byte[] serializeAvroHttpRequestJSON(
            TransactionV2 request) {

        DatumWriter<TransactionV2> writer = new SpecificDatumWriter<>(
                TransactionV2.class);
        byte[] data = new byte[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Encoder jsonEncoder = null;
        try {
            jsonEncoder = EncoderFactory.get().jsonEncoder(
                    TransactionV2.getClassSchema(), stream);
            writer.write(request, jsonEncoder);
            jsonEncoder.flush();
            data = stream.toByteArray();
        } catch (IOException e) {
            log.error("Serialization error:" + e.getMessage());
        }
        return data;
    }

    public TransactionV2 deserialize(byte[] data) {
        DatumReader<TransactionV2> reader
                = new SpecificDatumReader<>(TransactionV2.class);
        Decoder decoder = null;
        try {
            decoder = DecoderFactory.get().jsonDecoder(TransactionV2.getClassSchema(), new String(data));
            return reader.read(null, decoder);
        } catch (IOException e) {
            log.error("Deserialization error:" + e.getMessage());
        }
        return null;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

//        factory.setCommonErrorHandler(new CommonErrorHandler() {
//            @Override
//            public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
//                log.error("Unexpected error occured while stream processing. ");
//            }
//        });

//        event.headers().add(Kafkautil.DLT_TOPIC_NAME_HEADER, TRANASCTION_V2_DLT.getBytes());
//        event.headers().add(Kafkautil.DLT_ORIGINAL_EVENT_KEY, event.key().getBytes());
//        event.headers().add(Kafkautil.DLT_ORIGINAL_EVENT_TYPE, event.value().getClass().getName().getBytes());
//        event.headers().add(Kafkautil.DLT_ORIGINAL_TOPIC, saTopic.getBytes());
//        event.headers().add(Kafkautil.DLT_ORIGINAL_PARTITION, String.valueOf(event.partition()).getBytes());
//        event.headers().add(Kafkautil.DLT_ORIGINAL_OFFSET, String.valueOf(event.offset()).getBytes());
//        event.headers().add(Kafkautil.DLT_REASON, (t.getMessage() + ": \n" + ExceptionUtils.getStackTrace(t)).getBytes());


        configurer.configure(factory, kafkaConsumerFactory.getIfAvailable());
        factory.setErrorHandler(((exception, event) -> {
            log.error("Error in process with Exception {} and the record is {}", exception, event);
            String dltTopicTo = getDltTopicName(event);
            Map<String, String> headers = Map.of(
                    Kafkautil.DLT_ORIGINAL_EVENT_KEY, event.key().toString(),
                    Kafkautil.DLT_ORIGINAL_EVENT_TYPE, event.getClass().getName(),
                    Kafkautil.DLT_ORIGINAL_TOPIC, randomVersion(event.topic()),
                    Kafkautil.DLT_ORIGINAL_PARTITION, String.valueOf(event.partition()),
                    Kafkautil.DLT_ORIGINAL_OFFSET, String.valueOf(event.offset()),
                    Kafkautil.DLT_REASON, getDltException(event),
                    Kafkautil.DLT_PRIORITY, getHeader(Kafkautil.DLT_PRIORITY, event)
            );
            if(Objects.isNull(dltTopicTo)) {
                log.warn("[ ERROR PRODUCE ] Unable to send event to dead letter queue as topic could not be identified. Event: {}", event.key());
                return;
            }
            // Direct to Dead Letter Queue
            if(event.value() instanceof ServiceAgreementDataV2) {
                kafkaProducerService.produceDeadLetterBySa((ServiceAgreementDataV2) event.value(), dltTopicTo + "_avro", headers);
//                kafkaProducerService.produceServiceAgreementV2((ServiceAgreementDataV2) event.value(), dltTopicTo, headers);
            }
            if(event.value() instanceof TransactionV2) {
                kafkaProducerService.sendTransactionV2((TransactionV2) event.value(), dltTopicTo, headers);
            }
        }));

        return factory;
    }

    private String randomVersion(String input) {
//        return String.format("%s%s", input.substring(0, input.length() - 1), String.valueOf(4));
        return String.format("%s%s", input.substring(0, input.length() - 1), String.valueOf(random.nextInt(5) + 1));
    }

    private String getDltTopicName(ConsumerRecord<?, ?> r) {
        return getHeader(Kafkautil.DLT_TOPIC_NAME_HEADER, r);
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

//    @Bean
//    public <T> ConcurrentKafkaListenerContainerFactory<UUID, T> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<UUID, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        factory.setMissingTopicsFatal(false);
//        factory.setCommonErrorHandler(new CommonErrorHandler() {
//            @Override
//            public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
//                log.error("Unexpected error occured while stream processing. ");
//            }
//        });
//        return factory;
//    }

    private String getSchemaAsString(String avroFileName) {
        try {
            Path file = new DefaultResourceLoader().getResource(String.format("classpath:avro/%s", avroFileName)).getFile().toPath();
            return Files.readString(file, java.nio.charset.Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
