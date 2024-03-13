package com.gentle.store.order.rest;

import com.gentle.store.order.dto.OrderedItemDTO;
import com.gentle.store.order.response.TotalAmount;
import com.gentle.store.order.service.OrderWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.gentle.store.order.util.Constants.ORDER_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(ORDER_PATH)
@RequiredArgsConstructor
@Slf4j
public class OrderWriteController {
    private final OrderWriteService orderWriteService;
    @PostMapping(value = "{customerId}", consumes = APPLICATION_JSON_VALUE)
    String placeOrder(@RequestBody List<OrderedItemDTO> orderedItem, @PathVariable final UUID customerId) {
        log.debug("order: {}",orderedItem.toString());
        return orderWriteService.placeOrder(orderedItem, customerId);
    }

//    @PostMapping()
//    @ResponseStatus(HttpStatus.CREATED)
//    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
//    @TimeLimiter(name = "inventory")
//    @Retry(name = "inventory")
//    public CompletableFuture<String> placeOrder(@RequestBody OrderDTO orderDTO) {
//        return CompletableFuture.supplyAsync(()-> orderWriteService.placeOrder(orderDTO));
//    }

//    public CompletableFuture<String> fallbackMethod(OrderDTO orderDTO, RuntimeException runtimeException) {
//        return CompletableFuture.supplyAsync(()-> "SHIIIIIIIIIIIII");
//    }
//
//    @GetMapping()
//    public List<Order> getAll() {
//        return orderWriteService.getAll();
//    }

    @PostMapping("buy/{orderNumber}")
    TotalAmount pay(@PathVariable final String orderNumber) {
        return orderWriteService.completeOrder(orderNumber);
    }
}
