package com.feketegabor.streaming.EventProcessor.listener;

import com.feketegabor.streaming.avro.model.TransactionV2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Component
public class TransactionEventHandler {
	 
	EmitterProcessor<TransactionV2> processor = EmitterProcessor.create();
	FluxSink<TransactionV2> fluxSink = processor.sink(FluxSink.OverflowStrategy.LATEST);
	Flux<TransactionV2> hotFlux = processor.publish().autoConnect();
     
	 public void handle (TransactionV2 event) {
		 fluxSink.next(event);
	 }
     
     public Flux<TransactionV2> getEvents () {
      return 
    	 Flux.<TransactionV2>create(sink -> {
    		 hotFlux.subscribe(event -> sink.next(event));
    	 })
    	 .share(); 
     }
}
