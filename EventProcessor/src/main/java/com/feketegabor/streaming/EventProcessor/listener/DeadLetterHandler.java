package com.feketegabor.streaming.EventProcessor.listener;

import com.feketegabor.streaming.EventProcessor.repository.model.DeadLetterEntity;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Component
public class DeadLetterHandler {
	 
	EmitterProcessor<DeadLetterEntity> processor = EmitterProcessor.create();
	FluxSink<DeadLetterEntity> fluxSink = processor.sink(FluxSink.OverflowStrategy.LATEST);
	Flux<DeadLetterEntity> hotFlux = processor.publish().autoConnect();
     
	 public void handle (DeadLetterEntity event) {
		 fluxSink.next(event);
	 }
     
     public Flux<DeadLetterEntity> getEvents () {
      return 
    	 Flux.<DeadLetterEntity>create(sink -> {
    		 hotFlux.subscribe(event -> sink.next(event));
    	 })
    	 .share();
     }
}
