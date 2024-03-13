package com.gentle.store.order.service;

import com.gentle.store.order.dto.OrderDTO;
import com.gentle.store.order.dto.OrderedItemDTO;
import com.gentle.store.order.entity.OrderedItem;
import com.gentle.store.order.event.OrderPlacedEvent;
import com.gentle.store.order.mapper.OrderMapper;
import com.gentle.store.order.repository.OrderRepository;
import com.gentle.store.order.response.TotalAmount;
import com.gentle.store.order.transfer.InStockResponse;
import com.gentle.store.order.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.*;

import static com.gentle.store.order.util.Constants.ADMIN_BASIC_AUTH;
import static com.gentle.store.order.util.Constants.INVENTORY_CLIENT;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderWriteService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final OrderReadService orderReadService;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public TotalAmount completeOrder(final String orderNumber) {
        log.debug("completeOrder: orderNumber={}", orderNumber);

        final var order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(NotFoundException::new);

        log.debug("completeOrder: order{}", order);
        log.debug("completeOrder: orderedItems={}", order.getOrderedItems());

        final var cleanedItems = order.getOrderedItems().stream()
                .filter(Objects::nonNull)
                .toList();

        log.debug("completeOrder: cleanedItems={}", cleanedItems);

        order.setComplete(true);
        log.info("Order has been Completed");

        orderRepository.save(order);

        final var path = STR."/buy/\{order.getCustomerId()}";
        log.info("starte kommunikationen mit inventory-service...");
        final var message = inventoryClient(path, cleanedItems, null);
        return new TotalAmount(order.getTotalAmount());
    }

    public String placeOrder(List<OrderedItemDTO> orderedItemDTO, final UUID customerId) {
        log.debug("placeOrder: customerId={} orderedItemDTO={}", customerId, orderedItemDTO);

        List<OrderedItemDTO> cleanOrderedItems = orderedItemDTO.stream()
                .map(item -> {
                    int totalQuantity = orderedItemDTO.stream()
                            .filter(itemDuplicate -> itemDuplicate.skuCode().equals(item.skuCode()))
                            .mapToInt(OrderedItemDTO::quantity)
                            .sum();
                    return new OrderedItemDTO(item.skuCode(), totalQuantity, item.price());
                })
                .distinct() // Doppelte Einträge entfernen, falls vorhanden
                .toList();

        log.trace("placeOrder: doppelte einträge wurden entfernt");


        final List<OrderedItem> items = new ArrayList<>();
        cleanOrderedItems.forEach(orderedItem -> {
            final var inventoryDetails = orderReadService.getInventoryDetails(orderedItem.skuCode());
            final var item = orderMapper.toOrderedItem(orderedItem);
            item.setPrice(inventoryDetails.price());
            items.add(item);
        });


        final var newOrderDTO = new OrderDTO("ASD", orderedItemDTO);
        log.debug("placeOrder: newOrderDTO={}", newOrderDTO);

        final var order = orderMapper.toOrder(newOrderDTO);
        order.setOrderNumber("ASDASD");
        order.setOrderedItems(items);
        order.setComplete(false);
        order.setCustomerId(customerId);

        log.debug("placeOrder: newOrder={}", order);
        log.debug("placeOrder: newOrderItems={}", order.getOrderedItems());

        final var skuCodes = order.getOrderedItems().stream()
                .map(OrderedItem::getSkuCode)
                .toList();

        log.trace("placeOrder: isInStock()");
        log.info("starte kommunikationen mit inventory-service...");
        InStockResponse[] inStockResponseArray = webClientBuilder.build().get()
                .uri(STR."\{INVENTORY_CLIENT}/skuCode", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .header("Authorization", ADMIN_BASIC_AUTH)
                .retrieve()
                .bodyToMono(InStockResponse[].class)
                .block();


        assert inStockResponseArray != null;
        log.debug("placeOrder: InStockResponse={}", inStockResponseArray[0]);

        final var allProductsInStock = Arrays.stream(inStockResponseArray)
                .allMatch(InStockResponse::isInStock);

        if (allProductsInStock) {
            order.setTotalAmount(totalAmount(order.getOrderedItems()));
            orderRepository.save(order);
            order.addItems(items);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            return reserveItems(newOrderDTO, customerId);
        } else
            throw new IllegalArgumentException("gibs net");
    }

    private BigDecimal totalAmount(List<OrderedItem> orderedItems) {
        log.debug("TotalAmount: orderedItems={}", orderedItems);

        final BigDecimal[] totalAmount = {new BigDecimal(0)};
        orderedItems.forEach(item -> {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount[0] = totalAmount[0].add(itemTotal);
        });
        log.debug("TotalAmount: TotalAmount={}", totalAmount[0]);
        return totalAmount[0];
    }

    private String reserveItems(OrderDTO orderDTO, final UUID customerId) {
        log.debug("reserveItems: items={}", orderDTO.toString());

        final var path = STR."/reserve/\{customerId}";

        log.trace("reserveItems: reserveItems()");
        log.info("starte kommunikationen mit inventory-service...");
        return inventoryClient(path, null, orderDTO.orderedItems());
    }

    private String inventoryClient(final String path, final List<OrderedItem> orderedItems, final List<OrderedItemDTO> itemDTOS) {

        final var items = orderedItems == null
                ? itemDTOS
                : orderedItems;

        return  webClientBuilder.build()
                .post()
                .uri(STR."\{INVENTORY_CLIENT}/\{path}")
                .header("Authorization",ADMIN_BASIC_AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(items))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
