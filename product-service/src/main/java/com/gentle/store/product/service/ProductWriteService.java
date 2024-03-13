package com.gentle.store.product.service;

import com.gentle.store.product.dto.ProductDTO;
import com.gentle.store.product.entity.Product;
import com.gentle.store.product.mapper.ProductMapper;
import com.gentle.store.product.repository.ProductRepository;
import com.gentle.store.product.service.exception.ConstraintViolationsException;
import com.gentle.store.product.service.exception.NotFoundException;
import com.gentle.store.product.service.exception.VersionOutdatedException;
import com.gentle.store.product.transfer.InventoryRequest;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.UUID;

import static com.gentle.store.product.util.Constants.INVENTORY_CLIENT;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ProductWriteService {
    private final WebClient.Builder webClientBuilder;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final Validator validator;

    public Product create(ProductDTO productDTO) {

        final var product = productMapper.toProduct(productDTO);

        final var violations = validator.validate(product, Default.class);
        if (!violations.isEmpty()) {
            log.debug("create: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }

        final var productDb = productRepository.save(product);
        log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

        log.debug("create: productDb={}", productDb);
        return productDb;
    }

    public Product update(ProductDTO productDTO, UUID id, int version) {
        final var product = productMapper.toProduct(productDTO);

        final var violations = validator.validate(product);
        if (!violations.isEmpty()) {
            log.debug("update: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }
        log.trace("update: Keine Constraints verletzt");

        final var productDb = productRepository.findById(id).orElseThrow(NotFoundException::new);
        log.trace("update: version={}, productDb={}", version, productDb);

        if (version < productDb.getVersion()) {
            log.error("version ist nicht die Aktuelle Verion");
            throw new VersionOutdatedException(version);
        }
        if (version > productDb.getVersion()) {
            log.error("version gibt es noch nicht");
            throw new VersionOutdatedException(version);
        }

        productDb.set(product);
        final var updatedProductDb = productRepository.save(productDb);
        log.debug("update: updatedProductDB={}", productDb);
        log.debug("update: updatedProduct={}", productDb);

        return updatedProductDb;
    }

    public String deleteById(UUID id) {
        log.debug("deleteById: id={}", id);

        final var product = productRepository.findById(id).orElseThrow(NotFoundException::new);
        productRepository.delete(product);
        return STR."Produkt: \{product.getName()} mit der ID: \{product.getId()} wurde aus der Datenbank gelöscht";
    }

    public String addToInventory(UUID id) {

        final var product = productRepository.findById(id).orElseThrow(NotFoundException::new);
        final var unitPrice = product.getPrice().multiply(BigDecimal.valueOf(1.19));

        // Extrahiere die ersten 8 Zeichen der UUID und konvertiere sie zu einem String
        String skuCode = id.toString().substring(0, 8);

        final var  request = new InventoryRequest(skuCode,50,unitPrice,id);

        return inventoryClient(request);
    }

   private String inventoryClient(final InventoryRequest request) {

        return  webClientBuilder.build()
                .post()
                .uri(INVENTORY_CLIENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
