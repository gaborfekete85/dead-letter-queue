package com.feketegabor.streaming.EventProcessor.controller.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDTO {
    String transactionId;
    String name;
    Long amount;
}
