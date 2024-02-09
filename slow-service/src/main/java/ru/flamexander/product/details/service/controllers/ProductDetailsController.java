package ru.flamexander.product.details.service.controllers;

import org.springframework.web.bind.annotation.*;
import ru.flamexander.product.details.service.dtos.ProductDetailsDto;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/details")
public class ProductDetailsController {
    @GetMapping("/{id}")
    public ProductDetailsDto getProductDetailsById(@PathVariable Long id) throws InterruptedException {
        if (id > 100) {
            throw new NoSuchElementException("There is only 100 product details!");
        }
        if(id%2==1){
           return null;
        }
        Thread.sleep(2500 + (int)(Math.random() * 2500));
        return new ProductDetailsDto(id, "Product description: product №" + id);
    }
}
