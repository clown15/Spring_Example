package com.example.demo;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
class InventoryService {

	private ItemRepository repository;
	private ReactiveFluentMongoOperations fluentOperations;

	InventoryService(ItemRepository repository, ReactiveFluentMongoOperations fluentOperations) {
		this.repository = repository;
		this.fluentOperations = fluentOperations;
	}

	Flux<Item> getItems() {
		return Flux.empty();
	}

    Flux<Item> searchByExample(String name, String description, boolean useAnd) {
		Item item = new Item(name, description, 0.0); // <1>

		ExampleMatcher matcher = (useAnd // <2>
				? ExampleMatcher.matchingAll() //
				: ExampleMatcher.matchingAny()) //
						.withStringMatcher(StringMatcher.CONTAINING) // <3>
						.withIgnoreCase() // <4>
						.withIgnorePaths("price"); // <5>

		Example<Item> probe = Example.of(item, matcher); // <6>

		return repository.findAll(probe); // <7>
	}
}
