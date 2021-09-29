package com.example.demo;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.catalog.Catalog;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class CartService {
    
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    CartService(ItemRepository itemRepository, CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    // Mono<Cart> addToCart(String cartId, String id) {
    //     return this.cartRepository.findById(cartId)//
    //         .defaultIfEmpty(new Cart(cartId))//
    //         .flatMap(cart -> cart.getCartItems().stream()//
    //             .filter(cartItem -> cartItem.getItem()//
    //                 .getId().equals(id))//
    //             .findAny()//
    //             .map(cartItem -> {
    //                 cartItem.increment();
    //                 return Mono.just(cart);
    //             })//
    //             .orElseGet(() ->//
    //                 this.itemRepository.findById(id)//
    //                     // .map(item -> new CartItem(item))과 같은의미
    //                     .map(CartItem::new)//
    //                     .doOnNext(cartItem -> cart.getCartItems().add(cartItem))//
    //                     .map(cartItem -> cart)))
    //         // .flatMap(cart -> this.cartRepository.save(cart))
    //         .flatMap(this.cartRepository::save);
    // }

    Mono<Cart> addToCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId)
            .defaultIfEmpty(new Cart(cartId))
            .flatMap(cart -> cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                .findAny()
                .map(cartItem -> {
                    cartItem.increment();
                    return Mono.just(cart);
                })
                .orElseGet(() -> {
                    return this.itemRepository.findById(itemId)
                        .map(item -> new CartItem(item))
                        .map(cartItem -> {
                            cart.getCartItems().add(cartItem);
                            return cart;
                        });
                        
                })
            )
            .flatMap(cart -> this.cartRepository.save(cart));
    }

    // Mono<Cart> removeOneFromCart(String cartId, String itemId) {
	// 	return this.cartRepository.findById(cartId) //
	// 			.defaultIfEmpty(new Cart(cartId)) //
	// 			.flatMap(cart -> cart.getCartItems().stream() //
	// 					.filter(cartItem -> cartItem.getItem() //
	// 							.getId().equals(itemId))
	// 					.findAny() //
	// 					.map(cartItem -> {
	// 						cartItem.decrement();
	// 						return Mono.just(cart);
	// 					}) //
	// 					.orElse(Mono.empty())) //
	// 			.map(cart -> new Cart(cart.getId(), cart.getCartItems().stream() //
	// 					.filter(cartItem -> cartItem.getQuantity() > 0) //
	// 					.collect(Collectors.toList()))) //
	// 			.flatMap(cart -> this.cartRepository.save(cart));
	// }
    Mono<Cart> deleteOneFromCart(String cartId, String itemId) {

        return this.cartRepository.findById(cartId)
            .defaultIfEmpty(new Cart(cartId))
            .flatMap(cart -> cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                .findAny()
                .map(cartItem -> {
                    cartItem.decrement();
                    return Mono.just(cart);
                }).orElse(Mono.empty())
            )
            .map(cart -> new Cart(cart.getId(),cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getQuantity() > 0)
                .collect(Collectors.toList())
            ))
            .flatMap(cart -> this.cartRepository.save(cart));
    }

}
