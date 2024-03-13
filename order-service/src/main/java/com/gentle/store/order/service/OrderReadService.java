package com.gentle.store.order.service;

import com.gentle.store.order.entity.Order;
import com.gentle.store.order.repository.OrderRepository;
import com.gentle.store.order.transfer.InventoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

import static com.gentle.store.order.util.Constants.ADMIN_BASIC_AUTH;
import static com.gentle.store.order.util.Constants.INVENTORY_CLIENT;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class OrderReadService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public Order findById(final UUID id) {
        return orderRepository.findByIdFetchAll(id).orElseThrow(NotFoundException::new);
    }



    public Order findByOrderNumber(final String orderNumber) {

        return orderRepository.findByOrderNumber(orderNumber).orElseThrow(NotFoundException::new);
    }

    public InventoryResponse getInventoryDetails(final String skuCode) {
        log.debug("getInventoryDetails: skuCode={}",skuCode);

        return webClientBuilder.build()
                .get()
                .uri(STR."\{INVENTORY_CLIENT}/skuCode/\{skuCode}")
                .header("Authorization",ADMIN_BASIC_AUTH)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .block();

    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
