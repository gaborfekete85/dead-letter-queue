package guru.learningjournal.examples.kafka.avroposfanout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan("guru.learningjournal.examples.kafka.avroposfanout")
public class AvroPosFanoutApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvroPosFanoutApplication.class, args);
	}

}
