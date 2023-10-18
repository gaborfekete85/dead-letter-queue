package guru.learningjournal.examples.kafka.avroposfanout.bindings;


import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import guru.learningjournal.examples.kafka.avroposfanout.model.HadoopRecord;
import guru.learningjournal.examples.kafka.avroposfanout.model.Notification;
import guru.learningjournal.examples.kafka.model.PosInvoice;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public interface PosListenerBinding {

//    @Input("notification-input-channel")
//    KStream<String, PosInvoice> notificationInputStream();
//
//    @Output("notification-output-channel")
//    KStream<String, Notification> notificationOutputStream();

    @Input("sa-input-channel")
    KStream<UUID, ServiceAgreementDataV2> saInputChannel();

    @Output("sa-output-channel")
    KStream<UUID, ServiceAgreementDataV2> saOutputChannel();

//    @Input("hadoop-input-channel")
//    KStream<String, PosInvoice> hadoopInputStream();
//
//    @Output("hadoop-output-channel")
//    KStream<String, HadoopRecord> hadoopOutputStream();

}
