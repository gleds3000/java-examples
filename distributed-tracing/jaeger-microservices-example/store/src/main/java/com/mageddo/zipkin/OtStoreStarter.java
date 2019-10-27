package com.mageddo.zipkin;

import com.mageddo.kafka.MessageSender;
import com.mageddo.tracing.Tracing;
import io.opentracing.util.GlobalTracer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@Import(MessageSender.class)
public class OtStoreStarter {

	public static void main(String[] args) {
		GlobalTracer.registerIfAbsent(Tracing.createTracer("chairland"));
		SpringApplication.run(OtStoreStarter.class, args);

	}

}
