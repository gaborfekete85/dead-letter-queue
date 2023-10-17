package guru.learningjournal.examples.kafka.avroposfanout.controller;

import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import guru.learningjournal.examples.kafka.avroposfanout.services.NotificationProcessorService;
import guru.learningjournal.examples.kafka.avroposfanout.services.ServiceAgreementStore;
import lombok.AllArgsConstructor;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class StreamController {
    private final ServiceAgreementStore serviceAgreementStore;

    @GetMapping(path = "/service-sagreements")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Flux<ServiceAgreementDataV2> get() {
        serviceAgreementStore.findAll();
        return Flux.empty();
//        return  serviceAgreementStore.findAll();
    }
}
