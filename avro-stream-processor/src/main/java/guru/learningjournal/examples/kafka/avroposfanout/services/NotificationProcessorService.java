package guru.learningjournal.examples.kafka.avroposfanout.services;

import com.feketegabor.streaming.avro.model.DeadLetter;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import guru.learningjournal.examples.kafka.avroposfanout.bindings.PosListenerBinding;
import guru.learningjournal.examples.kafka.avroposfanout.model.Notification;
import guru.learningjournal.examples.kafka.avroposfanout.services.listener.DeadLetterEventHandler;
import guru.learningjournal.examples.kafka.model.PosInvoice;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
@EnableBinding(PosListenerBinding.class)
@RequiredArgsConstructor
public class NotificationProcessorService {

    public static final String SA_STORE = "saStore";
//    public static final String DLT_STORE = "dlt-input-store";

    private final RecordBuilder recordBuilder;
    private final DeadLetterEventHandler deadLetterEventHandler;
    private final DeadLetterService deadLetterService;

    @Value("${spring.kafka.consumer.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Value("${application.configs.persistence.enabled}")
    private boolean persistenceEnabled;

    @StreamListener("dlt-input-channel")
    @SendTo("dlt-output-channel")
    public KStream<String, DeadLetter> processDlt(KTable<String, DeadLetter> input) {
        log.info("Starting DLT Stream ... ");
        SpecificAvroSerde avroSerde = new SpecificAvroSerde<>();
        Map<String, ?> serdeConfig = Map.of(
                "schema.registry.url", schemaRegistryUrl,
                "specific.avro.reader", "true");
        avroSerde.configure(serdeConfig, false); // Configure the Serde with necessary settings
        KStream<String, DeadLetter> outgoingStream = input
                .toStream()
                .peek((k,v) -> deadLetterEventHandler.handle(k, v))
                .peek((k,v) -> { if(persistenceEnabled) deadLetterService.persistDlt(k, v); } )
                .peek((k,v) -> log.info("[ CONSUMED ] [ DEAD_LETTER ] {}: {}", k, v));
        return outgoingStream;
    }

//    //                .filter( (k,v) -> !v.equals(null))
////                .peek( (k, v) -> deadLetterEventHandler.handle(v))
////                .mapValues( x -> x )
//                .filter((k, v) -> Objects.nonNull(v))
////                .toTable(Materialized.as(storeSupplier))
//            .groupByKey()
//
////                .windowedBy(SlidingWindows.withTimeDifferenceAndGrace(Duration.ofSeconds(1), Duration.ofSeconds(1)))
//                .reduce((a, b) -> a, Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as(DLT_STORE)
//                        .withKeySerde(Serdes.String())
//            .withValueSerde(avroSerde))
//            .filter((k, v) -> Objects.nonNull(v))
//
//            .toStream()
//                .peek((k,v) -> log.info("[ CONSUMED ] [ DEAD_LETTER ] {}: {}", k, v));

    @StreamListener("sa-input-channel")
    @SendTo("sa-output-channel")
    public KStream<String, ServiceAgreementDataV2> process(KStream<String, ServiceAgreementDataV2> input) {
        log.info("Event received ... ");
//        KStream<String, ServiceAgreementDataV2> notificationKStream = input
//                .groupByKey()
//                .reduce((a, b) -> b, Materialized.as(SA_STORE)
//                .withValueSerde(new JsonSerde(ServiceAgreementDataV2.class)))
//                .toStream();

        SpecificAvroSerde avroSerde = new SpecificAvroSerde<>();

        Map<String, ?> serdeConfig = Map.of("schema.registry.url", "http://localhost:8081",
                "specific.avro.reader", "true");
        avroSerde.configure(serdeConfig, false); // Configure the Serde with necessary settings

        KStream<String, ServiceAgreementDataV2> notificationKStream = input
                .peek( (k,v) -> log.info("[ CONSUMED ] [ SERVICE_AGREEMENT_V2 ] {}: {}", k, v))
                .mapValues( x -> x )
                .groupByKey()
//                .windowedBy(SlidingWindows.withTimeDifferenceAndGrace(Duration.ofSeconds(1), Duration.ofSeconds(1)))
                .reduce((a, b) -> b, Materialized.as(SA_STORE)
                .withValueSerde(avroSerde))
                .toStream();

//        input.foreach((k, v) -> {
//            try {
//                UUID serviceAgreementId = v.getServiceAgreement().getServiceAgreementId();
////                if(Character.isDigit(serviceAgreementId.toString().charAt(0))) {
////                    throw new RuntimeException("Key Starts with Digit");
////                }
//                log.info(String.format("ServiceAgreement:- Key: %s, Value: %s", k, v));
//            } catch(Throwable e) {
//                input.to("pcmproser_pcoevents_serviceagreement_v2_dlt");
//            }
//        });
        return notificationKStream;
    }

    private Notification getNotification() {
        return Notification.builder()
                .EarnedLoyaltyPoints(5D)
                .CustomerCardNo("CarNo")
                .InvoiceNumber("Invoid")
                .TotalAmount(3d)
                .build();
    }

//    @StreamListener("notification-input-channel")
//    @SendTo("notification-output-channel")
//    @RetryableTopic(kafkaTemplate = "kafkaTemplate",
//            attempts = "4",
//            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000)
//    )
    public KStream<String, Notification> process2(KStream<String, PosInvoice> input) {
        log.info("Event received ... ");
        KStream<String, Notification> notificationKStream = input
                .filter((k, v) -> "PRIME".equalsIgnoreCase(v.getCustomerType().toString()))
                .mapValues(v -> recordBuilder.getNotification(v));

        notificationKStream.foreach((k, v) -> log.info(String.format("Notification:- Key: %s, Value: %s", k, v)));

        return notificationKStream;
    }

    @DltHandler
    public void processMessage(PosInvoice message) {
        log.error("DltHandler processMessage = {}", message);
    }
}
