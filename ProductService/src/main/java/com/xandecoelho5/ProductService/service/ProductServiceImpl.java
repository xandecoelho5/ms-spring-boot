package com.xandecoelho5.ProductService.service;

import com.xandecoelho5.ProductService.entity.Product;
import com.xandecoelho5.ProductService.exception.ProductServiceCustomException;
import com.xandecoelho5.ProductService.model.ProductRequest;
import com.xandecoelho5.ProductService.model.ProductResponse;
import com.xandecoelho5.ProductService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product...");

        var product = Product.builder()
                .productName(productRequest.name())
                .price(productRequest.price())
                .quantity(productRequest.quantity())
                .build();
        productRepository.save(product);

        log.info("Product created!");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Getting product by id...");
        var product = getById(productId);

        var productResponse = ProductResponse.builder().build();
        copyProperties(product, productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reducing quantity {} for Id: {}.", quantity, productId);

        Product product = getById(productId);

        if (product.getQuantity() < quantity) {
            throw new ProductServiceCustomException("Product does not have sufficient Quantity!", "INSUFFICIENT_QUANTITY");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product Quantity updated Succesfully!");
    }

    private Product getById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException("Product not found!", "PRODUCT_NOT_FOUND"));
    }
}
