package com.feketegabor.streaming.EventProcessor.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerationRequestDto {
    private int numberOfEvents;
}
