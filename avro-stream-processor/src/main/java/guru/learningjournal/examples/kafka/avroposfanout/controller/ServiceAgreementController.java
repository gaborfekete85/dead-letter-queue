package guru.learningjournal.examples.kafka.avroposfanout.controller;

import com.feketegabor.streaming.avro.model.DeadLetter;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.DeadLetterDTO;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.RetryDto;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.MessageDTO;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.ServiceAgreementDTO;
import guru.learningjournal.examples.kafka.avroposfanout.repository.model.DeadLetterStatisticEntity;
import guru.learningjournal.examples.kafka.avroposfanout.services.DeadLetterService;
import guru.learningjournal.examples.kafka.avroposfanout.services.KafkaProducerService;
import guru.learningjournal.examples.kafka.avroposfanout.services.KafkaStores;
import guru.learningjournal.examples.kafka.avroposfanout.services.listener.DeadLetterEventHandler;
import guru.learningjournal.examples.kafka.avroposfanout.services.listener.MessageEventHandler;
import guru.learningjournal.examples.kafka.avroposfanout.util.Kafkautil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ServiceAgreementController {

    private final KafkaStores kafkaStores;
    private final DeadLetterEventHandler deadLetterEventHandler;
    private final MessageEventHandler messageEventHandler;
    private final KafkaProducerService kafkaProducerService;
    private final DeadLetterService deadLetterService;

    @Value("${application.configs.topic.serviceAgreementRetry}")
    private String RETRY_SERVICE_AGRREMENT_TOPIC_NAME;

    @GetMapping(path = "/sa", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Flux<ServiceAgreementDTO> get() {
        return Flux.fromStream(kafkaStores.getServiceAgreements().toStream().map(this::mapToSaDto)).log();
    }

    @GetMapping(path = "/sa/stream", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Flux<ServiceAgreementDTO> getSaStream() {
//        return saStore.getPositions().log();
        return Flux.fromStream(kafkaStores.getServiceAgreements().toStream().map(this::mapToSaDto)).log();
    }


    @GetMapping(path = "/dlt", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<DeadLetterStatisticEntity>> getDlt(@RequestParam String serviceIds) {
        List<String> services = List.of(serviceIds.split(","));
        List<DeadLetterStatisticEntity> deadLetters = deadLetterService.getEventsByServiceId(services);

//        return Flux.fromStream(kafkaStores.getDeadLetters().toStream().map(DeadLetterEventHandler::mapToDto)).log();
//        return Flux.fromStream(kafkaStores.getDeadLetters()).log();
        return ResponseEntity.ok(deadLetters);
    }

    @GetMapping(path = "/dlt/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    @ResponseStatus(HttpStatus.ACCEPTED)
    public Flux<DeadLetterDTO> getDltAsStream() {
        return deadLetterEventHandler.getEvents();
    }

//    @PostMapping(path = "/message")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void sendMessage(@RequestBody MessageDTO messageDTO) {
//        messageEventHandler.handle(messageDTO);
////        return ResponseEntity.ok(messageDTO);
//    }

    @GetMapping(path = "/message")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendMessage(@RequestParam String message) {
        messageEventHandler.handle(MessageDTO.builder().message(message).build());
    }

    @GetMapping(path = "/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MessageDTO> getMessages() {
        return messageEventHandler.getMessages();
    }

    @PostMapping(value ="/kafka/retry", produces = "application/json")
    public ResponseEntity<String> retryEvent(@RequestBody RetryDto retryDto) throws IOException {
        DeadLetter dlt = kafkaStores.getById(retryDto.getDltTopicEventKey());
        byte[] avroBytes = Base64.getDecoder().decode(dlt.getAvro().toString());
        if(dlt.getEventType().toString().endsWith("ServiceAgreementDataV2")) {
            ServiceAgreementDataV2 sa = Kafkautil.deserializeAvro(avroBytes, ServiceAgreementDataV2.SCHEMA$, ServiceAgreementDataV2.class);
            kafkaProducerService.produceServiceAgreementV2(sa, retryDto.getTopic(), Map.of(
                    Kafkautil.DLT_TOPIC_EVENT_KEY, retryDto.getDltTopicEventKey()
            ));
        }
        return ResponseEntity.ok(String.format("%s event sent for retry to %s. ", retryDto.getDltTopicEventKey(), RETRY_SERVICE_AGRREMENT_TOPIC_NAME));
    }


//    private DeadLetterDTO mapToDto(DeadLetter deadLetter) {
//        if(Objects.nonNull(deadLetter.getEventKey())) {
//            return DeadLetterDTO.builder()
//                    .dltKey(UUID.fromString(deadLetter.getDltKey().toString()))
//                    .build();
//        }
//
////        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        OffsetDateTime dateTime = LocalDateTime.parse(deadLetter.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")).atOffset(ZoneOffset.UTC);
//
//        return DeadLetterDTO.builder()
//                .dltKey(UUID.fromString(deadLetter.getDltKey().toString()))
//                .eventKey(UUID.fromString(deadLetter.getEventKey().toString()))
//                .eventType(deadLetter.getEventType().toString())
//                .topic(deadLetter.getTopic().toString())
//                .partition(Integer.valueOf(deadLetter.getPartition().toString()))
//                .partitionOffset(Long.valueOf(deadLetter.getOffset().toString()))
//                .dataAsJson(deadLetter.getJson().toString())
//                .dataAsAvro(deadLetter.getAvro().toString())
//                .reason(deadLetter.getReason().toString())
//                .createdAt(dateTime)
//                .build();
//    }

    private ServiceAgreementDTO mapToSaDto(ServiceAgreementDataV2 sa) {
        if(Objects.isNull(sa)) {
            return null;
        }
        return ServiceAgreementDTO.builder()
                .context(
                        ServiceAgreementDTO.Context.builder()
                                .businessRelationshipUid(sa.getContext().getBusinessRelationshipUid())
                                .partnerUid(sa.getContext().getPartnerUid())
                                .build()
                )
                .serviceAgreementDtoV2(
                        ServiceAgreementDTO.ServiceAgreementDtoV2.builder()
                                .serviceName(sa.getServiceAgreement().getServiceName().toString())
                                .serviceId(sa.getServiceAgreement().getServiceId())
                                .serviceAgreementStatus(sa.getServiceAgreement().getServiceAgreementStatus().name())
                                .serviceAgreementId(sa.getServiceAgreement().getServiceAgreementId())
                                .isFeeAuthentic(sa.getServiceAgreement().getIsFeeAuthentic())
                                .isCollateralLoans(sa.getServiceAgreement().getIsCollateralLoans())
                                .isAuthPledge(sa.getServiceAgreement().getIsAuthPledge())
                                .agreements(sa.getServiceAgreement().getAgreements().stream().map(this::mapToDto).collect(Collectors.toList()))
                                .build()
                )
                .modifiedAt(OffsetDateTime.ofInstant(sa.getModifiedAt(), ZoneId.systemDefault()))
                .modifiedBy(sa.getModifiedBy().toString())
                .build();
    }

    private ServiceAgreementDTO.Agreement mapToDto(com.feketegabor.streaming.avro.model.agreement a) {
        return ServiceAgreementDTO.Agreement.builder()
                .agreementId(UUID.fromString(a.getAgreementId().toString()))
                .documentType(a.getDocumentType().toString())
                .build();
    }
}
