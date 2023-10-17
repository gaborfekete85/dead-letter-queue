package com.feketegabor.streaming.EventProcessor.controller;

import com.feketegabor.streaming.EventProcessor.controller.model.DeadLetterEvent;
import com.feketegabor.streaming.EventProcessor.controller.model.GenerationRequestDto;
import com.feketegabor.streaming.EventProcessor.controller.model.ServiceAgreementDTO;
import com.feketegabor.streaming.EventProcessor.controller.model.TransactionDTO;
import com.feketegabor.streaming.EventProcessor.listener.DeadLetterHandler;
import com.feketegabor.streaming.EventProcessor.listener.ServiceAgreementEventHandler;
import com.feketegabor.streaming.EventProcessor.listener.TransactionEventHandler;
import com.feketegabor.streaming.EventProcessor.repository.model.DeadLetterEntity;
import com.feketegabor.streaming.EventProcessor.services.KafkaProducerService;
import com.feketegabor.streaming.EventProcessor.services.datagenerator.ServiceAgreementGenerator;
import com.feketegabor.streaming.EventProcessor.util.Kafkautil;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import com.feketegabor.streaming.avro.model.Transaction;
import com.feketegabor.streaming.avro.model.TransactionV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerController {

    @Value("${application.configs.topic.serviceAgreement}")
    private String saTopic;

    @Value("${application.configs.topic.serviceAgreementDlt}")
    private String saDltTopic;

    private final KafkaProducerService kafkaProducerService;
    private final ServiceAgreementGenerator serviceAgreementGenerator;
    private final TransactionEventHandler transactionEventHandler;
    private final ServiceAgreementEventHandler serviceAgreementEventHandler;
    private final DeadLetterHandler deadLetterHandler;

    @PostMapping("/kafka/generate")
    public String generate(@RequestBody GenerationRequestDto generationRequestDto) throws InterruptedException {
        int numberOfEvents = generationRequestDto.getNumberOfEvents() > 0 ? generationRequestDto.getNumberOfEvents() : 1;
        for (int i = 0; i < numberOfEvents; i++) {
            kafkaProducerService.produceServiceAgreementV2(serviceAgreementGenerator.getNext());
            Thread.sleep(1000);
        }
        return String.format("%s events generated", numberOfEvents);
    }

    @PostMapping(value ="/kafka/transaction", produces = "application/json")
    public ResponseEntity<String> createTranasction(@RequestBody TransactionDTO transactionDTO) {
        Transaction sent = kafkaProducerService.sendTransaction(mapTo(transactionDTO));
        return ResponseEntity.ok(sent.toString());
    }

    @PostMapping(value ="/kafka/transactionV2", produces = "application/json")
    public ResponseEntity<String> createTranasctionV2(@RequestBody TransactionDTO transactionDTO) {
        TransactionV2 sent = kafkaProducerService.sendTransactionV2(mapToV2(transactionDTO));
        return ResponseEntity.ok(sent.toString());
    }

    @GetMapping(value = "/transactions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TransactionDTO> getAsStream() {
        return transactionEventHandler.getEvents().map(this::mapToDto);
    }

    @GetMapping(value = "/v2/service-agreements/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServiceAgreementDTO> getSaAsStream() {
        return serviceAgreementEventHandler.getEvents().map(this::mapToSaDto);
    }

    @GetMapping(value = "/v2/dead-letters/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<DeadLetterEntity> getDeadLettersAsStream() {
        return deadLetterHandler.getEvents();
    }

    @GetMapping(value = "/v2/dead-letters/sa", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServiceAgreementDTO> getServiceAgreementsDeadLetters() {
        EmitterProcessor<ServiceAgreementDTO> processor = EmitterProcessor.create();
        FluxSink<ServiceAgreementDTO> fluxSink = processor.sink(FluxSink.OverflowStrategy.LATEST);
        Flux<ServiceAgreementDTO> hotFlux = processor.publish().autoConnect();

        List<ServiceAgreementDTO> toReturn = new ArrayList<>();
        ConsumerRecords<String, ServiceAgreementDataV2> records = getEvents(fluxSink, hotFlux);
        for (ConsumerRecord<String, ServiceAgreementDataV2> record : records) {
//            if(Objects.nonNull(record.value())) {
                log.info("Event {} - {}", record.key(), record.value());
                toReturn.add(this.mapToSaDto(record.value()));
//            }
        }
        log.info("Size: {}", toReturn.stream().filter(Objects::nonNull).collect(Collectors.toList()).size());
        return toReturn.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


    @GetMapping(value = "/v2/dead-letters/sa/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServiceAgreementDTO> getServiceAgreementsDeadLettersAsStram() {
        EmitterProcessor<ServiceAgreementDTO> processor = EmitterProcessor.create();
        FluxSink<ServiceAgreementDTO> fluxSink = processor.sink(FluxSink.OverflowStrategy.LATEST);
        Flux<ServiceAgreementDTO> hotFlux = processor.publish().autoConnect();

        CompletableFuture
                .runAsync( () -> this.readSaFromBeginning(fluxSink, hotFlux))
                .exceptionally(
                        throwable -> {
                            final Throwable t = ExceptionUtils.getRootCause(throwable);
                            log.error("Reading the topic failed", t.getMessage());
                            return null;
                        }
                );

        return Flux.<ServiceAgreementDTO>create(sink -> { hotFlux.subscribe(event -> sink.next(event)); }).share();
    }

    private void readSaFromBeginning(FluxSink<ServiceAgreementDTO> fluxSink, Flux<ServiceAgreementDTO> hotFlux) {
        ConsumerRecords<String, ServiceAgreementDataV2> records = getEvents(fluxSink, hotFlux);
        for (ConsumerRecord<String, ServiceAgreementDataV2> record : records) {
            if(Objects.isNull(record) || Objects.isNull(record.value())) {
                continue;
            }
            fluxSink.next(this.mapToSaDto(record.value()));
            System.out.println("Replay event: " + record.value());
            kafkaProducerService.produceServiceAgreementV2(record.value());
        }
    }

    private ConsumerRecords<String, ServiceAgreementDataV2> getEvents(FluxSink<ServiceAgreementDTO> fluxSink, Flux<ServiceAgreementDTO> hotFlux) {
        KafkaConsumer<String, ServiceAgreementDataV2> kafkaConsumer = Kafkautil.getConsumer("earliest");
        kafkaConsumer.subscribe(Collections.singletonList(saDltTopic));
        return kafkaConsumer.poll(Duration.ofSeconds(10));
    }

    private Transaction mapTo(TransactionDTO transactionDTO) {
        return Transaction.newBuilder().setValue(transactionDTO.getAmount().intValue()).build();
    }

    private TransactionV2 mapToV2(TransactionDTO transactionDTO) {
        return TransactionV2.newBuilder()
                .setTransactionId(transactionDTO.getTransactionId() == null ? UUID.randomUUID().toString() : transactionDTO.getTransactionId())
                .setName(transactionDTO.getName())
                .setAmount(transactionDTO.getAmount())
                .build();
    }

    private TransactionDTO mapToDto(TransactionV2 transactionV2) {
        return TransactionDTO.builder()
                .transactionId(transactionV2.getTransactionId() == null ? UUID.randomUUID().toString() : transactionV2.getTransactionId().toString())
                .name(transactionV2.getName().toString())
                .amount(transactionV2.getAmount())
                .build();
    }

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
