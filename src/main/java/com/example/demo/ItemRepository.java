package com.example.demo;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveCrudRepository<Item, String>, ReactiveQueryByExampleExecutor<Item> {
    // partialName이 이름에 포함된 상품을 반환하며 내부 기능은 스프링 데이터 리포지토리에서 자동으로 생성된다
    // 스프링에서 제공해주는 메소드 이름규칙
    Flux<Item> findByNameContaining(String partialName);
    // Description이 대소문자구분없이(IgnoreCase) 부분일치 하는것(Containing)
    Flux<Item> findByDescriptionContainingIgnoreCase(String partialName);
    // 이름 부분일치(NameContaining) And 설명 부분일치(DescriptionContaining) + 대소문자 구분없이(AllIgnoreCase)
    Flux<Item> findByNameContainingAndDescriptionContainingAllIgnoreCase(String partialName, String partialDesc);

    // 위와 같이 요구조건이 많아지면 메소드의 이름이 길어지기 때문에 다른방식을 사용한다. itemByExampleRepository,InventoryService 확인
}
