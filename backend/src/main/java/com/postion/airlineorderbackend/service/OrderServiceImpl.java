package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.adapter.outbound.AirlineApiClient;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final AirlineApiClient airlineApiClient;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, AirlineApiClient airlineApiClient) {
        this.orderRepository = orderRepository;
        this.airlineApiClient = airlineApiClient;
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
        Order order = orderRepository.findById(id).orElseThrow(() ->
            new BusinessException(HttpStatus.BAD_REQUEST, "没有找到该订单。订单ID：" + id));

        return setOrderDto(order);
    }

    @Override
    public OrderDto payOrder(Long id) {
        log.info("开始处理支付订单请求，订单ID:{}", id);
        Order order = orderRepository.findById(id).orElseThrow(() ->
            new BusinessException(HttpStatus.BAD_REQUEST, "没有找到该订单。订单ID：" + id));
        if (!OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            log.warn("支付失败：订单 {} 的状态不是PENDING_PAYMENT，当前状态为 {} ", id, order.getStatus());
            throw new BusinessException(HttpStatus.BAD_REQUEST, "只有待支付的订单才能支付。当前状态：" + order.getStatus());
        }
        order.setStatus(OrderStatus.PAID);
        Order orderUpdate = orderRepository.updateOrInsert(order);
        log.info("订单 {} 状态已更新为 PAID ", id);

        // 异步触发下一步，出票
        requestTicketIssuance(order.getId());
        return setOrderDto(orderUpdate);
    }

    @Override
    public OrderDto cancelOrder(Long id) {
        log.info("开始处理取消订单请求，订单ID:{}", id);
        Order order = orderRepository.findById(id).orElseThrow(() ->
            new BusinessException(HttpStatus.BAD_REQUEST, "没有找到该订单。订单ID：" + id));
        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            log.warn("取消失败：订单 {} 的状态是CANCELLED。", id);
            throw new BusinessException(HttpStatus.BAD_REQUEST, "该订单已经被取消。当前状态：" + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order orderUpdate = orderRepository.updateOrInsert(order);
        log.info("订单 {} 状态已更新为 CANCELLED ", id);

        return setOrderDto(orderUpdate);
    }

    @Override
    public void requestTicketIssuance(Long id) {
        log.info("开始处理订单出票请求，订单ID:{}", id);
        try {
            String ticket = airlineApiClient.issueTicket(id);
            log.info("出票成功。{}", ticket);
        } catch (InterruptedException e) {
            log.warn("出票失败。订单ID:{}", id);
            throw new BusinessException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private OrderDto setOrderDto(Order order) {
        if (order == null) {
            return null;
        }
        OrderDto.UserDto userDto = orderMapper.userToUserDto(order.getUser());
        OrderDto orderDto = orderMapper.orderToOrderDto(order);
        Map<String, Object> map  = new HashMap<>();
        map.put("flightNumber", "MU5180");
        orderDto.setFlightInfo(map);
        orderDto.setUser(userDto);
        return orderDto;
    }

    @Scheduled(cron = "0/5 * * * * ? ")
    @Transactional
    @SchedulerLock(
        name = "cancelUnpaidOrdersTask",
        lockAtMostFor = "30000",
        lockAtLeastFor = "20000"
    )
    public void cancelUnpaidOrdersTask() {
        log.info("[定时任务] 开始检查并取消支付超时订单..任务1执行..");
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Order> unpaidOrder = orderRepository.findByStatusAndCreationDateBefore(
            OrderStatus.PENDING_PAYMENT,
            fifteenMinutesAgo
        );

        if (!unpaidOrder.isEmpty()) {
            log.info("[定时任务]发现 {} 个超时订单，将他们的状态更新为 CANCELLED。", unpaidOrder.size());
            for (Order order: unpaidOrder) {
                order.setStatus(OrderStatus.CANCELLED);
            }
            orderRepository.saveAll(unpaidOrder);
        } else {
            log.info("[定时任务]未发现超时订单。");
        }
    }

    @Scheduled(cron = "0/5 * * * * ? ")
    @Transactional
    @SchedulerLock(
        name = "cancelUnpaidOrdersTask2"
    )
    public void cancelUnpaidOrdersTask2() {
        log.info("[定时任务] 开始检查并取消支付超时订单..任务2执行..");
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Order> unpaidOrder = orderRepository.findByStatusAndCreationDateBefore(
            OrderStatus.PENDING_PAYMENT,
            fifteenMinutesAgo
        );

        if (!unpaidOrder.isEmpty()) {
            log.info("[定时任务]发现 {} 个超时订单，将他们的状态更新为 CANCELLED。", unpaidOrder.size());
            for (Order order: unpaidOrder) {
                order.setStatus(OrderStatus.CANCELLED);
            }
            orderRepository.saveAll(unpaidOrder);
        } else {
            log.info("[定时任务]未发现超时订单。");
        }
    }

}
