package com.xandecoelho5.ProductService.model;

import lombok.Builder;

@Builder
public record ProductResponse(String productName, long productId, long quantity, long price) {
}
