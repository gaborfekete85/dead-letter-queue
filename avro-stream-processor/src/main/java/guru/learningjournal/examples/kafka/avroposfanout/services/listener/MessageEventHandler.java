package guru.learningjournal.examples.kafka.avroposfanout.services.listener;

import com.feketegabor.streaming.avro.model.DeadLetter;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.DeadLetterDTO;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.MessageDTO;
import guru.learningjournal.examples.kafka.avroposfanout.services.KafkaStores;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Component
public class MessageEventHandler {
	 
	EmitterProcessor<MessageDTO> processor = EmitterProcessor.create();
	FluxSink<MessageDTO> fluxSink = processor.sink(FluxSink.OverflowStrategy.BUFFER);
	Flux<MessageDTO> hotFlux = processor.publish().autoConnect();

	@Autowired
	private KafkaStores kafkaStores;

	public void handle (MessageDTO event) {
		 fluxSink.next(event);
	 }

     public Flux<MessageDTO> getMessages () {
      return 
    	 Flux.<MessageDTO>create(sink -> {
    		 hotFlux.subscribe(event -> sink.next(event));
    	 })
    	 .share(); 
     }
}
