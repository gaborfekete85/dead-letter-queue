package com.feketegabor.streaming.EventProcessor.services.datagenerator;


import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;

public interface ServiceAgreementGenerator {
    ServiceAgreementDataV2 getNext();
}
