package guru.learningjournal.examples.kafka.avroposfanout.services.listener;

import com.feketegabor.streaming.avro.model.DeadLetter;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import guru.learningjournal.examples.kafka.avroposfanout.controller.domain.DeadLetterDTO;
import guru.learningjournal.examples.kafka.avroposfanout.services.KafkaStores;
import lombok.AllArgsConstructor;
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
public class DeadLetterEventHandler {
	 
	EmitterProcessor<DeadLetter> processor = EmitterProcessor.create();
	FluxSink<DeadLetter> fluxSink = processor.sink(FluxSink.OverflowStrategy.LATEST);
	Flux<DeadLetter> hotFlux = processor.publish().autoConnect();

	@Autowired
	private KafkaStores kafkaStores;

	public void handleDelete (String key) {
		DeadLetter current = DeadLetter.newBuilder()
				.setDltKey(key)
				.setEventKey(Strings.EMPTY)
				.setEventType(Strings.EMPTY)
				.setTopic(Strings.EMPTY)
				.setPartition(Strings.EMPTY)
				.setOffset(Strings.EMPTY)
				.setJson(Strings.EMPTY)
				.setAvro(Strings.EMPTY)
				.setReason(Strings.EMPTY)
				.setCreatedAt(Strings.EMPTY)
				.build();
		fluxSink.next(current);

//		DeadLetter current = kafkaStores.getDeadLetterById(key).blockFirst();
//		if(Objects.nonNull(current)) {
//			current.setDltKey(null);
//			fluxSink.next(current);
//		}
	}

	 public void handle (DeadLetter event) {
		 fluxSink.next(event);
	 }

	public void handle (String key, DeadLetter value) {
		if(Objects.isNull(value)) {
			handleDelete(key);
		} else {
			value.setDltKey(UUID.fromString(key).toString());
			handle(value);
		}
	}

     public Flux<DeadLetterDTO> getEvents () {
      return 
    	 Flux.<DeadLetterDTO>create(sink -> {
					 hotFlux.subscribe(event -> sink.next(this.mapToDto(event))
			 );
    	 })
    	 .share(); 
     }

	public static DeadLetterDTO mapToDto(DeadLetter deadLetter) {
		if(!StringUtils.hasText(deadLetter.getEventKey())) {
			return DeadLetterDTO.builder()
					.dltKey(UUID.fromString(deadLetter.getDltKey().toString()))
					.build();
		}

		OffsetDateTime dateTime = LocalDateTime.parse(deadLetter.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")).atOffset(ZoneOffset.UTC);

		return DeadLetterDTO.builder()
				.dltKey(Objects.nonNull(deadLetter.getDltKey()) ? UUID.fromString(deadLetter.getDltKey().toString()) : null)
				.eventKey(UUID.fromString(deadLetter.getEventKey().toString()))
				.eventType(deadLetter.getEventType().toString())
				.topic(deadLetter.getTopic().toString())
				.partition(Integer.valueOf(deadLetter.getPartition().toString()))
				.partitionOffset(Long.valueOf(deadLetter.getOffset().toString()))
				.dataAsJson(deadLetter.getJson().toString())
				.dataAsAvro(deadLetter.getAvro().toString())
				.reason(deadLetter.getReason().toString())
				.createdAt(dateTime)
				.build();
	}
}
