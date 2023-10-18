package guru.learningjournal.examples.kafka.avroposfanout.services;

import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class ServiceAgreementStore {

    private final InteractiveQueryService interactiveQueryService;
    public Flux<ServiceAgreementDataV2> getPositions(){
        return Flux.<ServiceAgreementDataV2>create(emitter -> {
                    ReadOnlyKeyValueStore<String, ServiceAgreementDataV2> queryableStoreType = interactiveQueryService.getQueryableStore(NotificationProcessorService.SA_STORE, QueryableStoreTypes.keyValueStore());
                    queryableStoreType.all().forEachRemaining( k -> emitter.next(k.value));
                    emitter.complete();
                })
                .share();
    }
}