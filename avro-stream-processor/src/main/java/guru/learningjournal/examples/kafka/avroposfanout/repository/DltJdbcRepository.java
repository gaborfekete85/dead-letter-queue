package guru.learningjournal.examples.kafka.avroposfanout.repository;

import guru.learningjournal.examples.kafka.avroposfanout.repository.model.DeadLetterEntity;
import guru.learningjournal.examples.kafka.avroposfanout.repository.model.DeadLetterStatisticEntity;
import guru.learningjournal.examples.kafka.avroposfanout.repository.rowmapper.DeadLetterRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DltJdbcRepository {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private static final DeadLetterRowMapper DEAD_LETTER_ROW_MAPPER = new DeadLetterRowMapper();

    public List<DeadLetterStatisticEntity> getEventCountByService(List<String> serviceIds) {
        String sqlStatement = "select latest_events.events, dle.* from dead_letter_events dle, (\n" +
                "select service_id, MAX(created_at) as latest, count(*) as events \n" +
                "  from dead_letter_events dle\n" +
                "  where created_at between '2023-10-29 13:15:10.567' and '2023-10-31 13:15:10.567' and event_priority in ( 'MAJOR', 'CRITICAL' ) and service_id in (:serviceIds)\n" +
                "  group by service_id, topic\n" +
                ") latest_events\n" +
                "where dle.service_id = latest_events.service_id and dle.created_at = latest_events.latest order by events desc;";
        log.info("Statement: {}, Params: {}", sqlStatement, serviceIds);

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("serviceIds", serviceIds);
        return namedJdbcTemplate.query(sqlStatement, parameters, DEAD_LETTER_ROW_MAPPER);
    }
}
