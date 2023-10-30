package guru.learningjournal.examples.kafka.avroposfanout.repository.rowmapper;

import guru.learningjournal.examples.kafka.avroposfanout.repository.model.DeadLetterStatisticEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

public class DeadLetterRowMapper implements RowMapper<DeadLetterStatisticEntity> {

    @Override
    public DeadLetterStatisticEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        DeadLetterStatisticEntity deadLetterStatisticEntity = new DeadLetterStatisticEntity();
        deadLetterStatisticEntity.setEventKey(UUID.fromString(resultSet.getString("event_key")));
        deadLetterStatisticEntity.setServiceId(resultSet.getString("service_id"));
        deadLetterStatisticEntity.setEventType(resultSet.getString("event_type"));
        deadLetterStatisticEntity.setTopic(resultSet.getString("topic"));
        deadLetterStatisticEntity.setPartition(Integer.valueOf(resultSet.getString("partition")));
        deadLetterStatisticEntity.setPartitionOffset(resultSet.getString("partition_offset"));
        deadLetterStatisticEntity.setDataAsJson(resultSet.getString("data_as_json"));
        deadLetterStatisticEntity.setDataAsAvro(resultSet.getString("data_as_avro"));
        deadLetterStatisticEntity.setReason(resultSet.getString("reason"));
        deadLetterStatisticEntity.setCreatedAt(LocalDateTime.ofInstant(resultSet.getTimestamp("created_at").toInstant(), ZoneOffset.UTC));
        deadLetterStatisticEntity.setEventPriority(resultSet.getString("event_priority"));
        deadLetterStatisticEntity.setNumberOfEvents(resultSet.getInt("events"));
        return deadLetterStatisticEntity;
    }
}
