package guru.learningjournal.examples.kafka.avroposfanout.config;

import lombok.extern.slf4j.Slf4j;
//import narif.poc.springkstreampoc.exceptions.InvalidCreditCardException;
//import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

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
                factoryBean.setUncaughtExceptionHandler(getStreamsUncaughtExceptionHandler());
            }
            @Override
            public int getOrder() {
                return Integer.MAX_VALUE - 10000;
            }
        };
    }

    private Thread.UncaughtExceptionHandler getStreamsUncaughtExceptionHandler() {
        return (thread, exception) -> {
            Throwable cause = exception.getCause();
            log.error("UNCAUGHT_EXCEPTION_OCCURED");
            if (cause.getClass().equals(RuntimeException.class)) {
                log.info("Uncaught exception occured");
                log.error(cause.getMessage());
//                return UncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD;
            }
//            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;
        };
    }

}