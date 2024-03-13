package com.gentle.store.customer.service.patch;

public record PatchOperation(
    PatchOperationType operationType,
    String path,
    String value
) {
}
