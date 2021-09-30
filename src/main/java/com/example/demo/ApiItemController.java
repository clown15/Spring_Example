package com.example.demo;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ApiItemController {
    
    private final ItemRepository repository;

    public ApiItemController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/items")
    Flux<Item> findAll() {  // 0개 또는 그이상의 Item객체가(Flux) JSON 구조로 직렬화돼서 응답 본문에 기록된다
        return this.repository.findAll();
    }

    @GetMapping("/api/items/{id}")
    Mono<Item> findOne(@PathVariable String id) {   //0개 또는 1개의 Item객체(Mono)가 리턴되며 @PathVariable를 통해 url에서 id값을 추출한다. 템플릿의 변수 이름과 인자 변수이름이 다르면 @PathVariable("id") String ItemId와 같이 호출하면 된다
        return this.repository.findById(id);
    }

    @PostMapping("/api/items")
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item) {  //@RequestBody애너테이션이 붙어있으면 request body가 인자로 사용된다.

        return item.flatMap(newitem -> this.repository.save(newitem))
            .map(savedItem -> ResponseEntity
                .created(URI.create("/api/items/" + savedItem.getId()))
            .body(savedItem));
    }

    @PutMapping("/api/items/{id}")
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<Item> item, @PathVariable String id) {
        return item.map(newItem -> new Item(id, newItem.getName(), newItem.getDescription(), newItem.getPrice()))
            .flatMap(this.repository::save)
            .map(ResponseEntity::ok);
    }
}
