package guru.learningjournal.examples.kafka.avroposfanout.config;

import lombok.extern.slf4j.Slf4j;
//import narif.poc.springkstreampoc.exceptions.InvalidCreditCardException;
//import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
//import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;

@Configuration
@Slf4j
public class KafkaStreamsConfig {

    @Bean
    public StreamsBuilderFactoryBeanConfigurer streamsCustomizer() {
         return new StreamsBuilderFactoryBeanConfigurer() {
            @Override
            public void configure(StreamsBuilderFactoryBean factoryBean) {
                factoryBean.setStreamsUncaughtExceptionHandler(getStreamsUncaughtExceptionHandler());
            }
            @Override
            public int getOrder() {
                return Integer.MAX_VALUE - 10000;
            }
        };
    }

    private StreamsUncaughtExceptionHandler getStreamsUncaughtExceptionHandler() {
        return exception -> {
            log.error("UNCAUGHT_EXCEPTION_OCCURED");
            Throwable cause = exception.getCause();
            if (cause.getClass().equals(RuntimeException.class)) {
                log.info("Uncaught exception occured");
                log.error(cause.getMessage());
                return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD;
            }
            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;
        };
    }

}