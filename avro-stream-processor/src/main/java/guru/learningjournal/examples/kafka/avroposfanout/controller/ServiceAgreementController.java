package guru.learningjournal.examples.kafka.avroposfanout.controller;

import com.feketegabor.streaming.avro.model.DeadLetter;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.DeadLetterDTO;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.ServiceAgreementDTO;
import guru.learningjournal.examples.kafka.avroposfanout.services.KafkaStores;
import guru.learningjournal.examples.kafka.avroposfanout.services.listener.DeadLetterEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ServiceAgreementController {

    private final KafkaStores kafkaStores;
    private final DeadLetterEventHandler deadLetterEventHandler;

    @GetMapping(path = "/sa", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Flux<ServiceAgreementDTO> get() {
//        return saStore.getPositions().log();
        return Flux.fromStream(kafkaStores.getServiceAgreements().toStream().map(this::mapToSaDto)).log();
    }

    @GetMapping(path = "/dlt", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Flux<DeadLetterDTO> getDlt() {
        return Flux.fromStream(kafkaStores.getDeadLetters().toStream().map(DeadLetterEventHandler::mapToDto)).log();
    }

    @GetMapping(path = "/dlt/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Flux<DeadLetterDTO> getDltAsStream() {
        return deadLetterEventHandler.getEvents().log();
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
