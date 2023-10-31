package guru.learningjournal.examples.kafka.avroposfanout.repository;

import guru.learningjournal.examples.kafka.avroposfanout.repository.model.DeadLetterEntity;
import guru.learningjournal.examples.kafka.avroposfanout.repository.model.DeadLetterStatisticEntity;
import guru.learningjournal.examples.kafka.avroposfanout.repository.rowmapper.DeadLetterRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.sql.SQLType;
import java.sql.Time;
import java.sql.Types;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DltJdbcRepository {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private static final DeadLetterRowMapper DEAD_LETTER_ROW_MAPPER = new DeadLetterRowMapper();

    public List<DeadLetterStatisticEntity> getEventCountByService(List<String> serviceIds, List<String> priorities, LocalDateTime from, LocalDateTime to) {
        String serviceIdsWhereClause = "";
        if(!serviceIds.isEmpty()) {
            serviceIdsWhereClause = " and UPPER(service_id) in (:serviceIds)";
        }
        String prioritiesWhereClause = "";
        if(!priorities.isEmpty()) {
            prioritiesWhereClause = " and UPPER(event_priority) in (:priorities)";
        }
        String dateRangeFilter = "";
        if(!Objects.isNull(from) || !Objects.isNull(to)) {
            dateRangeFilter = " and created_at between :from and :to";
        }
        String whereKeyWord = "";
        if(StringUtils.hasText(serviceIdsWhereClause) || StringUtils.hasText(prioritiesWhereClause) || StringUtils.hasText(dateRangeFilter)) {
            whereKeyWord = " where 1=1";
        }
        String sqlStatement = "select latest_events.events, dle.* from dead_letter_events dle, (\n" +
                "select service_id, MAX(created_at) as latest, count(*) as events \n" +
                "  from dead_letter_events dle\n" +
                whereKeyWord + dateRangeFilter + prioritiesWhereClause + serviceIdsWhereClause + "\n" +
                "  group by service_id, topic\n" +
                ") latest_events\n" +
                "where dle.service_id = latest_events.service_id and dle.created_at = latest_events.latest order by events desc;";
        log.info("Statement: {}, Params: {}, {}", sqlStatement, serviceIds, priorities);

//        java.sql.Date from1 = java.sql.Date.valueOf(LocalDate.now().minusDays(7));
//        java.sql.Date to1 = java.sql.Date.valueOf(LocalDate.now().plusDays(3));

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("serviceIds", serviceIds.stream().map( x -> x.toUpperCase()).collect(Collectors.toList()))
                .addValue("priorities", priorities.stream().map( x -> x.toUpperCase()).collect(Collectors.toList()))
                .addValue("from", calculateFrom(from, to), Types.DATE)
                .addValue("to", calculateTo(to), Types.DATE);
        return namedJdbcTemplate.query(sqlStatement, parameters, DEAD_LETTER_ROW_MAPPER);
    }

    @NotNull
    private static Date calculateTo(LocalDateTime to) {
        return Date.valueOf(Objects.isNull(to) ? LocalDateTime.now().plusDays(1).toLocalDate() : to.plusDays(1).toLocalDate());
    }

    private Date calculateFrom(LocalDateTime from, LocalDateTime to) {
        LocalDateTime current = LocalDateTime.now();
        if(Objects.nonNull(to)) {
            current = to;
        }
        return Date.valueOf(Objects.isNull(from) ? current.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate() : from.toLocalDate());
    }
}
