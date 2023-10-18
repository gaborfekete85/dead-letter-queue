package guru.learningjournal.examples.kafka.avroposfanout.controller;

import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.ServiceAgreementDTO;
import guru.learningjournal.examples.kafka.avroposfanout.services.ServiceAgreementStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ServiceAgreementController {

    private final ServiceAgreementStore saStore;

    @GetMapping(path = "/sa", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Flux<ServiceAgreementDTO> get() {
//        return saStore.getPositions().log();
        return Flux.fromStream(saStore.getPositions().toStream().map(this::mapToSaDto)).log();
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
