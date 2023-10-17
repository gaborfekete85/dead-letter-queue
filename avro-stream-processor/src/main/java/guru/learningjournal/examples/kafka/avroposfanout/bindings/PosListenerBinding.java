package guru.learningjournal.examples.kafka.avroposfanout.bindings;


import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

import java.util.UUID;

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
