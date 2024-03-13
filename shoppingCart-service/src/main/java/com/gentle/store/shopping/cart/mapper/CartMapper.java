package com.gentle.store.shopping.cart.mapper;

import com.gentle.store.shopping.cart.dto.ItemDTO;
import com.gentle.store.shopping.cart.dto.ShoppingCartDTO;
import com.gentle.store.shopping.cart.entity.Item;
import com.gentle.store.shopping.cart.entity.ShoppingCart;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {
    ShoppingCart toShoppingcart(ShoppingCartDTO shoppingCartDTO);

    Item toItem(ItemDTO itemDTO);
}