package ru.flamexander.reactive.service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.flamexander.reactive.service.dtos.DetailedProductDto;
import ru.flamexander.reactive.service.dtos.ProductDetailsDto;
import ru.flamexander.reactive.service.entities.Product;
import ru.flamexander.reactive.service.services.ProductDetailsService;
import ru.flamexander.reactive.service.services.ProductsService;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/v1/detailed")
@RequiredArgsConstructor
public class ProductsDetailsController {
    private final ProductDetailsService productDetailsService;
    private final ProductsService productsService;

    private final String DEFAULT_DESCRIPTION_MESSAGE = "No description yet, come back later!";


    @GetMapping("/demo")
    public Flux<ProductDetailsDto> getManySlowProducts() {
        Mono<ProductDetailsDto> p1 = productDetailsService.getProductDetailsById(1L);
        Mono<ProductDetailsDto> p2 = productDetailsService.getProductDetailsById(2L);
        Mono<ProductDetailsDto> p3 = productDetailsService.getProductDetailsById(3L);
        return p1.mergeWith(p2).mergeWith(p3);
    }

    @GetMapping("/{id}")
    public Mono<DetailedProductDto> getDetailedProductById(@PathVariable(name = "id") Long id) {
        Mono<Product> product = productsService.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Product not found")));
        Mono<ProductDetailsDto> details = productDetailsService.getProductDetailsById(id)
                .onErrorResume(error -> Mono.just(new ProductDetailsDto(id, DEFAULT_DESCRIPTION_MESSAGE)));
        return Mono.zip(product, details).map(this::mapToDto);
    }

    @GetMapping
    public Flux<DetailedProductDto> getAllDetailedProductsById(@RequestParam List<Long> ids) {
        Flux<ProductDetailsDto> productDetails = Flux.fromIterable(ids).flatMap(id ->
                productDetailsService.getProductDetailsById(id)
                        .onErrorResume(error -> Mono.just(new ProductDetailsDto(id, DEFAULT_DESCRIPTION_MESSAGE))));
        Flux<Product> products = productsService.findAllById(ids)
                .switchIfEmpty(Flux.error(new NoSuchElementException("Product not found")));
        return Flux.zip(products.sort(Comparator.comparing(Product::getId)),
                        productDetails.sort(Comparator.comparing(ProductDetailsDto::getId)))
                .map(this::mapToDto);
    }

    @GetMapping("/all")
    public Flux<DetailedProductDto> getAllDetailedProducts() {
        Flux<Product> products = productsService.findAll();
        Flux<Long> productIds = products.map(Product::getId);
        Flux<ProductDetailsDto> productDetails = productIds
                .flatMap(id -> productDetailsService.getProductDetailsById(id)
                                .onErrorResume(error-> Mono.just(new ProductDetailsDto(id, DEFAULT_DESCRIPTION_MESSAGE))));
        return Flux.zip(products.sort(Comparator.comparing(Product::getId)),
                        productDetails.sort(Comparator.comparing(ProductDetailsDto::getId)))
                .map(this::mapToDto);
    }

    private DetailedProductDto mapToDto(Tuple2<Product, ProductDetailsDto> tuple) {
        return new DetailedProductDto(tuple.getT1().getId(),
                tuple.getT1().getName(), tuple.getT2().getDescription());
    }
}