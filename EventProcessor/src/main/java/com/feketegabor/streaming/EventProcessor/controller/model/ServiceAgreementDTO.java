package com.feketegabor.streaming.EventProcessor.controller.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ServiceAgreementDTO {

    private Context context;
    private ServiceAgreementDtoV2 serviceAgreementDtoV2;
    private OffsetDateTime modifiedAt;
    private String modifiedBy;

    @Data
    @Builder
    public static class Context {
        private UUID businessRelationshipUid;
        private UUID partnerUid;
    }

    @Data
    @Builder
    public static class ServiceAgreementDtoV2 {
        private UUID serviceAgreementId;
        private UUID serviceId;
        private String serviceAgreementStatus;
        private String serviceName;
        private boolean isFeeAuthentic;
        private boolean isCollateralLoans;
        private boolean isAuthPledge;
        private List<Agreement> agreements;
    }

    @Builder
    @Data
    public static class Agreement {
        private UUID agreementId;
        private String documentType;
    }
}
