package com.xandecoelho5.ProductService.service;

import com.xandecoelho5.ProductService.model.ProductRequest;
import com.xandecoelho5.ProductService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
