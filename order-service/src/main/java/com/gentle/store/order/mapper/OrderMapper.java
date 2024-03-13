package com.gentle.store.order.mapper;

import com.gentle.store.order.dto.OrderDTO;
import com.gentle.store.order.dto.OrderedItemDTO;
import com.gentle.store.order.entity.Order;
import com.gentle.store.order.entity.OrderedItem;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    Order toOrder(OrderDTO orderDTO);

    List<OrderedItem> toOrderedItems(Collection<OrderedItemDTO> orderedItemDTO);
    OrderedItem toOrderedItem(OrderedItemDTO orderedItemDTO);
}