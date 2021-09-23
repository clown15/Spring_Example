package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.TemplateEngine;

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

}
