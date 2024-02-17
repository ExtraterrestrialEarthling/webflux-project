package ru.flamexander.product.details.service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.product.details.service.dtos.ProductDetailsDto;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/details")
public class ProductDetailsController {
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> getProductDetailsById(@PathVariable Long id) throws InterruptedException {
        if (id > 100 || id % 2 == 1) {
            return ResponseEntity.notFound().build();
        }
        Thread.sleep(2500 + (int) (Math.random() * 2500));
        ProductDetailsDto details = new ProductDetailsDto(id, "Product description: product №" + id);
        return ResponseEntity.ok(details);
    }
}
