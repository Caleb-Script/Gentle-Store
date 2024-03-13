package com.gentle.store.product.service.exception;

import lombok.Getter;

@Getter
public class VersionOutdatedException extends RuntimeException {
    private final int version;

    public VersionOutdatedException(final int version) {
        super(STR."Die Versionsnummer \{version} ist veraltet.");
        this.version = version;
    }
}
