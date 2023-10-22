package guru.learningjournal.examples.kafka.avroposfanout.bindings;


import com.feketegabor.streaming.avro.model.DeadLetter;
import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import guru.learningjournal.examples.kafka.avroposfanout.model.HadoopRecord;
import guru.learningjournal.examples.kafka.avroposfanout.model.Notification;
import guru.learningjournal.examples.kafka.model.PosInvoice;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
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

    @Input("dlt-input-channel")
    KTable<String, DeadLetter> dltInputChannel();

    @Output("dlt-output-channel")
    KStream<UUID, DeadLetter> dltOutputChannel();

//    @Input("hadoop-input-channel")
//    KStream<String, PosInvoice> hadoopInputStream();
//
//    @Output("hadoop-output-channel")
//    KStream<String, HadoopRecord> hadoopOutputStream();

}
