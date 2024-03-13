package com.gentle.store.inventory.service;

import com.gentle.store.inventory.dto.InventoryDTO;
import com.gentle.store.inventory.dto.ReservedProductDTO;
import com.gentle.store.inventory.entity.Inventory;
import com.gentle.store.inventory.entity.ReservedProduct;
import com.gentle.store.inventory.mapper.InventoryMapper;
import com.gentle.store.inventory.repository.InventoryRepository;
import com.gentle.store.inventory.service.exception.ConstraintViolationsException;
import com.gentle.store.inventory.service.exception.InsufficientInventoryException;
import com.gentle.store.inventory.service.exception.NotFoundException;
import com.gentle.store.inventory.service.exception.VersionOutdatedException;
import com.gentle.store.inventory.transfer.ItemDTO;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InventoryWriteService {
    private final InventoryRepository inventoryRepository;
    private final InventoryReadService inventoryReadService;
    private final InventoryMapper inventoryMapper;
    private final Validator validator;

    public Inventory create(InventoryDTO inventoryDTO) {

        final var product = inventoryMapper.toInventory(inventoryDTO);

        final var violations = validator.validate(product, Default.class);
        if (!violations.isEmpty()) {
            log.debug("create: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }

        final var productDb = inventoryRepository.save(product);
        log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

        log.debug("create: productDb={}", productDb);
        return productDb;
    }

    public Inventory update(InventoryDTO inventoryDTO, UUID id, int version) {
        final var inventory = inventoryMapper.toInventory(inventoryDTO);

        final var violations = validator.validate(inventory);
        if (!violations.isEmpty()) {
            log.debug("update: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }
        log.trace("update: Keine Constraints verletzt");

        final var inventoryDb = inventoryRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.trace("update: version={}, inventoryDb={}", version, inventoryDb);

        if (version < inventoryDb.getVersion()) {
            log.error("version ist nicht die Aktuelle Verion");
            throw new VersionOutdatedException(version);
        }
        if (version > inventoryDb.getVersion()) {
            log.error("version gibt es noch nicht");
            throw new VersionOutdatedException(version);
        }

        inventoryDb.set(inventory);
        final var updatedInventuryDb = inventoryRepository.save(inventoryDb);
        log.debug("update: updatedProductDB={}", inventoryDb);
        log.debug("update: updatedProduct={}", inventoryDb);

        return updatedInventuryDb;
    }

    public String deleteById(UUID id) {
        log.debug("deleteById: id={}", id);

        final var inventory = inventoryRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        inventoryRepository.delete(inventory);
        return STR."Inventar: mit der ID: \{inventory.getId()} wurde aus der Datenbank gelöscht";
    }

    public String reserveItems(List<ItemDTO> reservedItemDTOs, UUID customerId) {
        log.debug("reserveItems: customer={}, orderDTO={}", customerId, reservedItemDTOs);

        final var message = new StringBuilder();

        reservedItemDTOs.forEach(itemDTO -> {
            final var inventory = inventoryReadService.findBySkuCodeFetchAll(itemDTO.skuCode());
            log.debug("reserveItem: inventory={}",inventory);
            log.debug("reserveItem: reservedItem={}",inventory.getReservedProductsList());

            inventory.getReservedProductsList().stream()
                    .filter(reserveItem -> reserveItem.getCustomerId().equals(customerId))
                    .findFirst()
                    .ifPresentOrElse(reservedProduct -> {
                        reservedProduct.setQuantity(reservedProduct.getQuantity() + itemDTO.quantity());

                                final var sumReservedQuantity = inventory.getReservedProductsList().stream()
                                        .mapToInt(ReservedProduct::getQuantity)
                                        .sum();

                                log.debug("reserveItem: new quantity={}",inventory.getQuantity());
                                log.debug("reserveItem: sumReservedQuantity={}",sumReservedQuantity);

                                if (inventory.getQuantity() < sumReservedQuantity) {
                                    final var available = inventory.getQuantity() - sumReservedQuantity + itemDTO.quantity();
                                    throw new InsufficientInventoryException(inventory,available,itemDTO);
                                }
                        },
                            () -> {
                        final var reserveProductDTO = new ReservedProductDTO(itemDTO.quantity(), customerId,itemDTO.skuCode());
                        log.debug("reserveItem: reservedProductDTO={}",reserveProductDTO);

                        final var newReserveItem = inventoryMapper.toReservedProduct(reserveProductDTO);
                        log.debug("reserveItem: newReserveItem={}",newReserveItem);

                        inventory.getReservedProductsList().add(newReserveItem);
                        message.append(STR."Produkt \{inventory.getProductName()} wurde für dich \{itemDTO.quantity()} mal  reserviert\n");
                    });
        });
        message.append("diese Reservierung hält 1 Stunde an");
        return message.toString();
    }

    private String nachbestellen(ItemDTO itemDTO) {
        log.debug("nachbestellen: item={}",itemDTO);
        final var inventory = inventoryRepository.findBySkuCode(itemDTO.skuCode()).orElseThrow(() -> new NotFoundException(itemDTO.skuCode()));
        inventory.setQuantity(inventory.getQuantity() + itemDTO.quantity());
        return STR."lagerbestand wurde um \{itemDTO.quantity()} erhöht";
    }

    public void buyItems(final UUID customerId, List<ItemDTO> itemDTOs) {
        log.debug("buyItems: customerId={}, itemDTO={}", customerId, itemDTOs);

        final List<ReservedProduct> toBeRemoveItems = new ArrayList<>();
        itemDTOs.forEach(itemDTO -> {
            final var inventory = inventoryReadService.findBySkuCodeFetchAll(itemDTO.skuCode());
            inventory.setQuantity(inventory.getQuantity() - itemDTO.quantity());
            log.trace("buyItems: inventory={}",inventory);

            inventory.getReservedProductsList().stream()
                    .filter(reservedProduct -> reservedProduct.getCustomerId().equals(customerId))
                    .peek(reservedProduct -> {
                        log.trace("buyItems: reservedProduct={}", reservedProduct);
                        reservedProduct.setQuantity(reservedProduct.getQuantity() - itemDTO.quantity());
                        if (reservedProduct.getQuantity() == 0) {
                            toBeRemoveItems.add(reservedProduct);
                        }
                        if (reservedProduct.getQuantity() < 0) {
                            throw new InsufficientInventoryException(inventory);
                        }
                    })
                    .findAny()
                    .orElseThrow(() -> new NotFoundException(customerId,inventory.getId()));

            log.debug("buyItems: updated inventory={}",inventory);
            inventoryRepository.save(inventory);

            log.debug("buyItems: toBeRemoveItems={}",toBeRemoveItems);
            inventory.removeReservation(toBeRemoveItems);
        });
    }
}
