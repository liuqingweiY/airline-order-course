package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;
import com.postion.airlineorderbackend.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders/{id}")
public class OrderActionController {

    private final OrderService orderService;

    @Autowired
    public OrderActionController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/pay")
    public ResponseEntity<OrderDto> pay(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.payOrder(id));
    }

    @PostMapping("cancel")
    public ResponseEntity<OrderDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
//
//    @PostMapping("/retry-ticketing")
//    public ResponseEntity<OrderDto> retryTicketing(@PathVariable Long id) {
//        // 直接调用一步方法，立即返回202 Accepted
//        orderService.requestTicketIssuance(id);
//        return ResponseEntity.accepted().build();
//    }
}
