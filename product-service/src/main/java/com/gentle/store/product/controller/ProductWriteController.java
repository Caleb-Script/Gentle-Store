package com.gentle.store.product.controller;

import com.gentle.store.product.dto.ProductDTO;
import com.gentle.store.product.service.ProductWriteService;
import com.gentle.store.product.util.UriHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.product.util.Constants.ID_PATTERN;
import static com.gentle.store.product.util.Constants.PRODUCT_PATH;
import static com.gentle.store.product.util.VersionUtils.getVersion;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(PRODUCT_PATH)
@RequiredArgsConstructor
@Slf4j
public class ProductWriteController {
    private final ProductWriteService productWriteService;
    private final UriHelper uriHelper;

    @PostMapping
    ResponseEntity<Void> addProduct(
            @RequestBody ProductDTO productDTO,
            final HttpServletRequest request
    ) throws URISyntaxException {
        if(productDTO == null)
            return badRequest().build();

        final var product = productWriteService.create(productDTO);
        final var baseUri = uriHelper.getBaseUri(request);
        final var location = new URI(STR."\{baseUri.toString()}/\{product.getId()}");
        log.debug("POST: new Product={}", product);
        log.info("POST: new ProductId={}", product.getId());
        return created(location).build();
    }

    @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<Void> put(
            @PathVariable final UUID id,
            @RequestBody final ProductDTO productDTO,
            @RequestHeader("If-Match") final Optional<String> version,
            final HttpServletRequest request
    ) {
        log.debug("put: id={}, productDTO={}", id, productDTO);

        final int versionInt = getVersion(version, request);
        final var updatedProduct = productWriteService.update(productDTO, id, versionInt);

        log.debug("put: updatedCProduct={}", updatedProduct);
        return noContent().eTag(STR."\"\{updatedProduct.getVersion()}\"").build();
    }

    @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
    @ResponseStatus(NO_CONTENT)
    ResponseEntity<String> deleteById(@PathVariable final UUID id) {
        log.debug("deleteById: id={}", id);
        return ok().body(productWriteService.deleteById(id));
    }

    @PostMapping("/inventory/{id}")
    public String addToInventory(@PathVariable UUID id) {
        return productWriteService.addToInventory(id);
    }

}
