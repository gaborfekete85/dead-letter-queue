package com.feketegabor.streaming.EventProcessor.listener;

import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import com.feketegabor.streaming.avro.model.ServiceAgreementV2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Objects;

@Component
public class ServiceAgreementEventHandler {
	 
	EmitterProcessor<ServiceAgreementDataV2> processor = EmitterProcessor.create();
	FluxSink<ServiceAgreementDataV2> fluxSink = processor.sink(FluxSink.OverflowStrategy.LATEST);
	Flux<ServiceAgreementDataV2> hotFlux = processor.publish().autoConnect();
     
	 public void handle (ServiceAgreementDataV2 event) {
		 if(Objects.nonNull(event)) {
			 fluxSink.next(event);
		 }
	 }
     
     public Flux<ServiceAgreementDataV2> getEvents () {
      return 
    	 Flux.<ServiceAgreementDataV2>create(sink -> {
    		 hotFlux.subscribe(event -> sink.next(event));
    	 })
    	 .share(); 
     }
}
