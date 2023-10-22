package guru.learningjournal.examples.kafka.avroposfanout.services;

import com.feketegabor.streaming.avro.model.DeadLetter;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class KafkaStores {

    private final InteractiveQueryService interactiveQueryService;

    @Value("${spring.cloud.stream.kafka.streams.bindings.dlt-input-channel.consumer.materializedAs}")
    private String dltStore;

    public Flux<ServiceAgreementDataV2> getServiceAgreements() {
        return Flux.<ServiceAgreementDataV2>create(emitter -> {
                    ReadOnlyKeyValueStore<String, ServiceAgreementDataV2> queryableStoreType = interactiveQueryService.getQueryableStore(NotificationProcessorService.SA_STORE, QueryableStoreTypes.keyValueStore());
                    queryableStoreType.all().forEachRemaining( k -> emitter.next(k.value));
                    emitter.complete();
                })
                .share();
    }

    public Flux<DeadLetter> getDeadLetters() {
        return Flux.<DeadLetter>create(emitter -> {
                    ReadOnlyKeyValueStore<String, DeadLetter> queryableStoreType = interactiveQueryService.getQueryableStore(dltStore, QueryableStoreTypes.keyValueStore());
                    queryableStoreType.all().forEachRemaining( k ->
                            {
                                if(k.value != null) {
                                    k.value.setDltKey(k.key);
                                    emitter.next(k.value);
                                }
                            });
                    emitter.complete();
                })
                .share();
    }

    public Flux<DeadLetter> getDeadLetterById(String key) {
        return Flux.<DeadLetter>create(emitter -> {
                    ReadOnlyKeyValueStore<String, DeadLetter> queryableStoreType = interactiveQueryService.getQueryableStore(dltStore, QueryableStoreTypes.keyValueStore());
//                    emitter.next(queryableStoreType.get(key));
                    queryableStoreType.all().forEachRemaining( k ->
                    {
                        if(key.equals(k.key)) {
                            k.value.setDltKey(k.key);
                            emitter.next(k.value);
                        }
                    });
                    emitter.complete();
                })
                .share();
    }
}