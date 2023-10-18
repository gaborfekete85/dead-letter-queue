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
    public Flux<ServiceAgreementDataV2> findAll(){
        return Flux.<ServiceAgreementDataV2>create(emitter -> {
                    ReadOnlyKeyValueStore<String, ServiceAgreementDataV2> queryableStoreType = interactiveQueryService.getQueryableStore(NotificationProcessorService.SA_STORE, QueryableStoreTypes.keyValueStore());
                    queryableStoreType.all().forEachRemaining( k -> emitter.next(k.value));
                    emitter.complete();
                })
                .share();
    }
}
//
//import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.StoreQueryParameters;
//import org.apache.kafka.streams.state.QueryableStoreTypes;
//import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.annotation.EnableKafkaStreams;
//import org.springframework.kafka.config.StreamsBuilderFactoryBean;
//import org.springframework.stereotype.Component;
//
//import reactor.core.publisher.Flux;
//
//@Component
////@EnableKafka
////@EnableKafkaStreams
//public class ServiceAgreementStore {
////    @Autowired
////    private InteractiveQueryService interactiveQueryService;
//
//    @Autowired
//    private StreamsBuilderFactoryBean factoryBean;
//
//    public Flux<ServiceAgreementDataV2> findAll(){
//        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
//        ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams.store(
//                StoreQueryParameters.fromNameAndType(NotificationProcessorService.SA_STORE, QueryableStoreTypes.keyValueStore())
//        );
//        return Flux.empty();
//
////        return Flux.<ServiceAgreementDataV2>create(emitter -> {
////                    ReadOnlyKeyValueStore<String, ServiceAgreementDataV2> queryableStoreType = interactiveQueryService.getQueryableStore(NotificationProcessorService.SA_STORE, QueryableStoreTypes.keyValueStore());
////                    queryableStoreType.all().forEachRemaining( k -> emitter.next(k.value));
////                    emitter.complete();
////                })
////                .share();
//    }
//
//}