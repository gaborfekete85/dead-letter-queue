package com.feketegabor.streaming.EventProcessor.services.datagenerator;

//import com.feketegabor.avro.serviceagreement.v2.ServiceAgreementStatusEnum;
import com.feketegabor.streaming.avro.model.Context;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import com.feketegabor.streaming.avro.model.ServiceAgreementStatusEnum;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class RandomServiceAgreementGenerator implements ServiceAgreementGenerator {

    @Override
    public ServiceAgreementDataV2 getNext() {

        return ServiceAgreementDataV2.newBuilder()
                .setContext(
                        Context.newBuilder()
                                .setBusinessRelationshipUid(UUID.randomUUID())
                                .setPartnerUid(UUID.randomUUID())
                                .build()
                )
                .setServiceAgreement(
                        com.feketegabor.streaming.avro.model.ServiceAgreementV2.newBuilder()
                                .setAgreements(List.of(com.feketegabor.streaming.avro.model.agreement.newBuilder()
                                        .setAgreementId(UUID.randomUUID().toString())
                                        .setDocumentType("CIPPI2")
                                        .build(),
                                        com.feketegabor.streaming.avro.model.agreement.newBuilder()
                                                .setAgreementId(UUID.randomUUID().toString())
                                                .setDocumentType("CIPPI")
                                                .build()))
                                .setIsAuthPledge(true)
                                .setIsFeeAuthentic(false)
                                .setIsCollateralLoans(true)
                                .setServiceAgreementId(UUID.randomUUID())
                                .setServiceAgreementStatus(ServiceAgreementStatusEnum.ACTIVE)
                                .setServiceId(UUID.randomUUID())
                                .setServiceName("ServiceName")
                                .build()
                )
                .setModifiedAt(Instant.now())
                .setModifiedBy("u57844")
                .build();
    }
}
