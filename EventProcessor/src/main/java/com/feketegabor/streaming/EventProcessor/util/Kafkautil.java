package com.feketegabor.streaming.EventProcessor.util;

import com.feketegabor.streaming.avro.model.ServiceAgreementDataV2;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.*;
import java.util.Properties;
import java.util.UUID;

public class Kafkautil {
    public static final String DLT_SERVICE_ID = "dltServiceId";
    public static final String DLT_TOPIC_NAME_HEADER = "dltTopicName";
    public static final String DLT_REASON = "REASON";
    public static final String DLT_ORIGINAL_EVENT_KEY = "ORIGINAL_EVENT_KEY";
    public static final String DLT_ORIGINAL_EVENT_TYPE = "ORIGINAL_EVENT_TYPE";
    public static final String DLT_ORIGINAL_TOPIC = "ORIGINAL_TOPIC";
    public static final String DLT_ORIGINAL_PARTITION = "ORIGINAL_PARTITION";
    public static final String DLT_ORIGINAL_OFFSET = "ORIGINAL_OFFSET";
    public static final String DLT_TOPIC_EVENT_KEY = "dltTopicEventKey";

    public static <K, V> KafkaConsumer getConsumer(String autoOffset) {
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "localhost:9092");
        kafkaProps.put("group.id", "my-consumer" + UUID.randomUUID().toString().replaceAll("-", ""));
        kafkaProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps.put("value.deserializer", "io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer");
        kafkaProps.put("auto.offset.reset", autoOffset);
        kafkaProps.put("schema.registry.url", "http://localhost:8081");
        KafkaConsumer<K, V> kafkaConsumer = new KafkaConsumer<>(kafkaProps);
        return kafkaConsumer;
    }

    public static <T> byte[] serializeAvro(T event, org.apache.avro.Schema schema) {
        try {
            DatumWriter<T> writer = new SpecificDatumWriter<>(schema);

            // 3. Create a ByteArrayOutputStream to hold the serialized data
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // 4. Create a BinaryEncoder to write the data to the ByteArrayOutputStream
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

            // 5. Use the SpecificDatumWriter to serialize your Avro object into the ByteArrayOutputStream

            writer.write(event, encoder);
            encoder.flush();

            // Now, your Avro object is serialized in memory, and you can access the serialized data as a byte array
            byte[] serializedData = byteArrayOutputStream.toByteArray();
            return serializedData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> byte[] serializeAvro(ConsumerRecord<?, T> event, org.apache.avro.Schema schema) {
        return serializeAvro(event.value(), schema);
    }

    public static <T> T deserializeAvro(byte[] data, org.apache.avro.Schema schema, Class<T> cls) throws IOException {
        try {
            T instance = cls.newInstance();
            DatumReader<T> reader = new SpecificDatumReader<>(schema);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            reader.read(instance, decoder);
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Convert JSON to avro binary array.
     *
     * @param json
     * @param schemaStr
     * @return
     * @throws IOException
     */
    public static byte[] jsonToAvro(String json, String schemaStr) throws IOException {
        InputStream input = null;
        GenericDatumWriter<Object> writer = null;
        Encoder encoder = null;
        ByteArrayOutputStream output = null;
        try {
            Schema schema = new Schema.Parser().parse(schemaStr);
            DatumReader<Object> reader = new GenericDatumReader<Object>(schema);
            input = new ByteArrayInputStream(json.getBytes());
            output = new ByteArrayOutputStream();
            DataInputStream din = new DataInputStream(input);
            writer = new GenericDatumWriter<Object>(schema);
            Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);
            encoder = EncoderFactory.get().binaryEncoder(output, null);
            Object datum;
            while (true) {
                try {
                    datum = reader.read(null, decoder);
                } catch (EOFException eofe) {
                    break;
                }
                writer.write(datum, encoder);
            }
            encoder.flush();
            return output.toByteArray();
        } finally {
            try {
                input.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Convert Avro binary byte array back to JSON String.
     *
     * @param avro
     * @param schemaStr
     * @return
     * @throws IOException
     */
    public static String avroToJson(byte[] avro, String schemaStr) throws IOException {
        boolean pretty = false;
        GenericDatumReader<Object> reader = null;
        JsonEncoder encoder = null;
        ByteArrayOutputStream output = null;
        try {
            Schema schema = new Schema.Parser().parse(schemaStr);
            reader = new GenericDatumReader<Object>(schema);
            InputStream input = new ByteArrayInputStream(avro);
            output = new ByteArrayOutputStream();
            DatumWriter<Object> writer = new GenericDatumWriter<Object>(schema);
            encoder = EncoderFactory.get().jsonEncoder(schema, output, pretty);
            Decoder decoder = DecoderFactory.get().binaryDecoder(input, null);
            Object datum;
            while (true) {
                try {
                    datum = reader.read(null, decoder);
                } catch (EOFException eofe) {
                    break;
                }
                writer.write(datum, encoder);
            }
            encoder.flush();
            output.flush();
            return new String(output.toByteArray());
        } finally {

        }
    }
}
