package com.example.demo;

import reactor.core.publisher.Mono;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;

// 웹페이지를 리턴하는 컨트롤러임을 나타내는 애너테이션
@Controller
public class HomeController {
    // GET요청을 처리하는 애너테이션
    // @GetMapping
    // Mono<String> home() {
    //     // html file name
    //     return Mono.just("home");
    // }

    private ItemRepository itemRepository;
    private CartRepository cartRepository;
    private CartService cartService;

    public HomeController(ItemRepository itemRepository, CartRepository cartRepository, CartService cartService) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    @GetMapping
	Mono<Rendering> home() {
		return Mono.just(Rendering.view("home2.html") 
                // modelAttribut로 template에서 사용될 데이터 지정
				.modelAttribute("items",
						this.itemRepository.findAll())
				.modelAttribute("cart",
                        // My Cart존재하지 않으면 새로운 Cart생성
						this.cartRepository.findById("My Cart")
								.defaultIfEmpty(new Cart("My Cart")))
				.build());
	}

    @PostMapping("/add/{id}")
    // @PathVariable 를 통해 {id} 자리에 들어오는 값을 추출해 id파라미터에 값을 주입한다.                                                                                                                                                               
    Mono<String> addToCart(@PathVariable String id) {
        return this.cartService.addToCart("My Cart", id)//
            .thenReturn("redirect:/");
    }

}
