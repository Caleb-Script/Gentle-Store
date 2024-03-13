package com.gentle.store.order.rest;

import com.gentle.store.order.entity.Order;
import com.gentle.store.order.service.OrderReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.gentle.store.order.util.Constants.ORDER_PATH;

@RestController
@RequestMapping(ORDER_PATH)
@RequiredArgsConstructor
@Slf4j
public class OrderGetController {
    private final OrderReadService orderReadService;

    @GetMapping("{id}")
    Order getById(@PathVariable UUID id) {
        return orderReadService.findById(id);
    }

    @GetMapping("orderNumber/{orderNumber}")
    Order getByOrderNumber(@PathVariable final String orderNumber) {
        return orderReadService.findByOrderNumber(orderNumber);
    }

    @GetMapping
    List<Order> getAll() {
        return orderReadService.findAll();
    }

}
