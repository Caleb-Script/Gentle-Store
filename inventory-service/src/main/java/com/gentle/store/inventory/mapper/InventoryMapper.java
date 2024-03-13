package com.gentle.store.inventory.mapper;

import com.gentle.store.inventory.dto.InventoryDTO;
import com.gentle.store.inventory.dto.ReservedProductDTO;
import com.gentle.store.inventory.entity.Inventory;
import com.gentle.store.inventory.entity.ReservedProduct;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {
    Inventory toInventory(InventoryDTO inventoryDTO);

    InventoryDTO toInventoryDto(Inventory inventory);

    ReservedProduct toReservedProduct(ReservedProductDTO reservedProductDTO);
}