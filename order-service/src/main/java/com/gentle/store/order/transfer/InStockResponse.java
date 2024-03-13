package com.gentle.store.order.transfer;

public record InStockResponse(String skuCode, boolean isInStock) {
}
