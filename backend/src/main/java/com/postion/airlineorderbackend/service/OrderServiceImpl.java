package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        ArrayList<OrderDto> orderDtoList = new ArrayList<>();

        for (Order order : orders) {
            orderDtoList.add(setOrderDto(order));
        }

        return orderDtoList;
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        return setOrderDto(order);
    }

    @Override
    public OrderDto payOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new RuntimeException("Order is payed");
        }
        order.setStatus(OrderStatus.PAID);
        Order orderUpdate = orderRepository.updateOrInsert(order);
        return setOrderDto(orderUpdate);
    }

    @Override
    public OrderDto cancelOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            throw new RuntimeException("Order is cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order orderUpdate = orderRepository.updateOrInsert(order);
        return setOrderDto(orderUpdate);
    }

    private OrderDto setOrderDto(Order order) {
        OrderDto.UserDto userDto = new OrderDto.UserDto();
        if (order == null) {
            return null;
        }
        userDto.setId(order.getUser().getId());
        userDto.setUserName(order.getUser().getUsername());

        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setOrderNumber(order.getOrderNumber());
        orderDto.setStatus(order.getStatus());
        orderDto.setAmount(order.getAmount());
        orderDto.setCreationDate(order.getCreateDate());
        orderDto.setUser(userDto);

        Map<String, Object> map  = new HashMap<>();
        map.put("flightNumber", "MU5180");
        orderDto.setFlightInfo(map);
        return orderDto;
    }

}
