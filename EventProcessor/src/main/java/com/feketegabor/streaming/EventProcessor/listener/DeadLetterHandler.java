package com.feketegabor.streaming.EventProcessor.listener;

import com.feketegabor.streaming.EventProcessor.repository.model.DeadLetterEntity;
import com.feketegabor.streaming.avro.model.DeadLetter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Component
public class DeadLetterHandler {
	 
	EmitterProcessor<DeadLetter> processor = EmitterProcessor.create();
	FluxSink<DeadLetter> fluxSink = processor.sink(FluxSink.OverflowStrategy.LATEST);
	Flux<DeadLetter> hotFlux = processor.publish().autoConnect();
     
	 public void handle (DeadLetter event) {
		 fluxSink.next(event);
	 }
     
     public Flux<DeadLetter> getEvents () {
      return 
    	 Flux.<DeadLetter>create(sink -> {
    		 hotFlux.subscribe(event -> sink.next(event));
    	 })
    	 .share();
     }
}
