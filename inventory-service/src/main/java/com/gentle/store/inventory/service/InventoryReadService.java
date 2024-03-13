package com.gentle.store.inventory.service;

import com.gentle.store.inventory.entity.Inventory;
import com.gentle.store.inventory.model.InStockResponse;
import com.gentle.store.inventory.model.InventoryResponse;
import com.gentle.store.inventory.repository.InventoryRepository;
import com.gentle.store.inventory.repository.SpecificationBuilder;
import com.gentle.store.inventory.service.exception.NotFoundException;
import com.gentle.store.inventory.transfer.ProductDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.gentle.store.inventory.util.Constants.ADMIN_BASIC_AUTH;
import static com.gentle.store.inventory.util.Constants.PRODUCT_CLIENT;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryReadService {
    private final WebClient.Builder webClientBuilder;
    private final InventoryRepository inventoryRepository;
    private final SpecificationBuilder specificationBuilder;

    public List<InStockResponse> isInStock(List<String> skuCode) {
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory -> {
                    return new InStockResponse(inventory.getSkuCode(), inventory.getQuantity() > 0);
                })
                .toList();
    }

    public Inventory findById(UUID id, boolean fetchAll) {
        log.debug("findById: id={}", id);

        final var inventory = fetchAll
                ? inventoryRepository.findByIdFetchAll(id).orElseThrow(NotFoundException::new)
                : inventoryRepository.findById(id).orElseThrow(NotFoundException::new);

        final var product = findProductById(inventory.getProductId());
        log.debug("findById: product={}", product);

        inventory.setProductName(product.name());

        log.debug("findById: inventory={}", inventory);
        return inventory;
    }

    public Inventory findBySkuCodeFetchAll(final String skuCode) {
        log.debug("findBySkuCodeFetchAll: skuCode={}",skuCode);
        final var inventory = inventoryRepository.findBySkuCodeFetchAll(skuCode)
                .orElseThrow(() -> new NotFoundException(skuCode));

        final var product = findProductById(inventory.getProductId());
        log.debug("findBySkuCode: product={}", inventory);

        final var name = product.name();
        log.debug("findBySkuCodeFetchAll: name={}",name);

        inventory.setProductName(name);
        log.debug("findBySkuCodeFetchAll: updatedInventory={}",inventory);

        return inventory;
    }

    public InventoryResponse findBySkuCode(final String skuCode) {
        log.debug("findBySkuCode: skuCode={}", skuCode);

        final var inventory = inventoryRepository.findBySkuCode(skuCode).orElseThrow(() -> {
            String errorMessage = STR."Inventar nicht gefunden für SKU-Code: \{skuCode}";
            log.error(errorMessage);
            return new NotFoundException(skuCode);
        });

        log.debug("findBySkuCode: inventory={}", inventory);

        final var product = findProductById(inventory.getProductId());
        log.debug("findBySkuCode: product={}", inventory);

        final var price = inventory.getUnitPrice();
        final var name = product.name();

        return new InventoryResponse(name, price);
    }

    ProductDTO findProductById(final UUID productId) {
        log.debug("findProductById: productId={}", productId);

        final var productDTO = productClient(productId);

        log.debug("findProductById: product={}", productDTO);
        return productDTO;
    }

    private ProductDTO productClient(final UUID productId) {

        return webClientBuilder.build()
                .get()
                .uri(STR."\{PRODUCT_CLIENT}/\{productId}")
                .header("Authorization",ADMIN_BASIC_AUTH)
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .block();
    }


    public @NonNull Collection<Inventory> find(@NonNull final Map<String, List<String>> searchCriteria) {
        log.debug("find: searchCriteria={}", searchCriteria);

        if (searchCriteria.isEmpty()) {
            return inventoryRepository.findAll();
        }

        final var specification = specificationBuilder
                .build(searchCriteria)
                .orElseThrow(() -> new NotFoundException(searchCriteria));
        final var inventories = inventoryRepository.findAll(specification);

        if (inventories.isEmpty())
            throw new NotFoundException(searchCriteria);

        log.debug("find: inventories={}", inventories);
        return inventories;
    }
}
