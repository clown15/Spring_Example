package com.example.demo;

import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.thymeleaf.TemplateEngine;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import reactor.blockhound.BlockHound;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		// 스택트레이스가 스레드 경계를 넘어 전달가능하게끔 하는 메소드
		Hooks.onOperatorDebug();
		// 블록하운드 예외추가
		BlockHound.builder()
			.allowBlockingCallsInside(
				TemplateEngine.class.getCanonicalName(), "process").install();
		SpringApplication.run(DemoApplication.class, args);
	}

	// HttpTraceRepository traceRepository() { // <2>
	// 	return new InMemoryHttpTraceRepository(); // <3>
	// }
	
	// @Bean
	// HttpTraceRepository springDataTraceRepository(HttpTraceWrapperRepository repository) {
	// 	return new SpringDataHttpTraceRepository(repository);
	// }

	// static Converter<Document, HttpTraceWrapper> CONVERTER = //
	// 		new Converter<Document, HttpTraceWrapper>() { //
	// 			@Override
	// 			public HttpTraceWrapper convert(Document document) {
	// 				Document httpTrace = document.get("httpTrace", Document.class);
	// 				Document request = httpTrace.get("request", Document.class);
	// 				Document response = httpTrace.get("response", Document.class);

	// 				return new HttpTraceWrapper(new HttpTrace( //
	// 						new HttpTrace.Request( //
	// 								request.getString("method"), //
	// 								URI.create(request.getString("uri")), //
	// 								request.get("headers", Map.class), //
	// 								null),
	// 						new HttpTrace.Response( //
	// 								response.getInteger("status"), //
	// 								response.get("headers", Map.class)),
	// 						httpTrace.getDate("timestamp").toInstant(), //
	// 						null, //
	// 						null, //
	// 						httpTrace.getLong("timeTaken")));
	// 			}
	// 		};
	// // end::custom-1[]

	// // tag::custom-2[]
	// @Bean
	// public MappingMongoConverter mappingMongoConverter(MongoMappingContext context) {

	// 	MappingMongoConverter mappingConverter = //
	// 			new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context); // <1>

	// 	mappingConverter.setCustomConversions( // <2>
	// 			new MongoCustomConversions(Collections.singletonList(CONVERTER))); // <3>

	// 	return mappingConverter;
	// }

}
