package com.xandecoelho5.OrderService.external.response;

import lombok.Builder;

@Builder
public record ProductResponse(String productName, long productId, long quantity, long price) {
}
