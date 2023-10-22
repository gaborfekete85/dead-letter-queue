package com.feketegabor.streaming.EventProcessor.controller.model;

import lombok.Data;

@Data
public class TombstoneRequest {
    private String dltTopicEventKey;
}
