package com.example.demo;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.stream.Collectors;

@Service
class InventoryService {

	private ItemRepository itemRepository;
	private CartRepository cartRepository;

	InventoryService(ItemRepository itemRepository, CartRepository cartRepository) {
		this.itemRepository = itemRepository;
		this.cartRepository = cartRepository;
	}

	public Flux<Cart> getAllCarts() {
        return this.cartRepository.findAll();
    }

    public Mono<Cart> newCart() {
        return this.cartRepository.save(new Cart("cart"));
    }

    public Mono<Cart> getCart(String cartId) {
        return this.cartRepository.findById(cartId);
    }

    public Flux<Item> getInventory() {
        return this.itemRepository.findAll();
    }

    Mono<Item> saveItem(Item newItem) {
        return this.itemRepository.save(newItem);
    }

    Mono<Void> deleteItem(String id) {
        return this.itemRepository.deleteById(id);
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

		return itemRepository.findAll(probe); // <7>
	}

	Mono<Cart> addItemToCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId)
            .defaultIfEmpty(new Cart(cartId)) //
            .flatMap(cart -> cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                .findAny() //
                .map(cartItem -> {
                    cartItem.increment();
                    return Mono.just(cart);
                }) //
                .orElseGet(() -> {
                    return this.itemRepository.findById(itemId) //
                        .map(item -> new CartItem(item)) //
                        .map(cartItem -> {
                            cart.getCartItems().add(cartItem);
                            return cart;
                        });
                }))
            .flatMap(cart -> this.cartRepository.save(cart));
    }

    Mono<Cart> removeOneFromCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId)
            .defaultIfEmpty(new Cart(cartId))
            .flatMap(cart -> cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                .findAny()
                .map(cartItem -> {
                    cartItem.decrement();
                    return Mono.just(cart);
                }) //
                .orElse(Mono.empty()))
            .map(cart -> new Cart(cart.getId(), cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getQuantity() > 0)
                .collect(Collectors.toList())))
            .flatMap(cart -> this.cartRepository.save(cart));
    }
}
