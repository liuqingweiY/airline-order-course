package airlineorderbackend.services;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;

    private Order testOrder;
    private Order testOrderPay;
    private Order testOrderCancel;
    private Order testOrderCancelled;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testOrder = new Order();
        testOrder.setId(100L);
        testOrder.setOrderNumber("ORD12345");
        testOrder.setStatus(OrderStatus.PAID);
        testOrder.setAmount(new BigDecimal("250.75"));
        testOrder.setCreationDate(LocalDateTime.now());
        testOrder.setUser(testUser);

        testOrderPay = new Order();
        testOrderPay.setId(101L);
        testOrderPay.setOrderNumber("ORD00002");
        testOrderPay.setStatus(OrderStatus.PENDING_PAYMENT);
        testOrderPay.setAmount(new BigDecimal("300.75"));
        testOrderPay.setCreationDate(LocalDateTime.now());
        testOrderPay.setUser(testUser);

        testOrderCancel = new Order();
        testOrderCancel.setId(102L);
        testOrderCancel.setOrderNumber("ORD00003");
        testOrderCancel.setStatus(OrderStatus.PAID);
        testOrderCancel.setAmount(new BigDecimal("320.75"));
        testOrderCancel.setCreationDate(LocalDateTime.now());
        testOrderCancel.setUser(testUser);

        testOrderCancelled = new Order();
        testOrderCancelled.setId(103L);
        testOrderCancelled.setOrderNumber("ORD00004");
        testOrderCancelled.setStatus(OrderStatus.CANCELLED);
        testOrderCancelled.setAmount(new BigDecimal("330.75"));
        testOrderCancelled.setCreationDate(LocalDateTime.now());
        testOrderCancelled.setUser(testUser);
    }

    @Test
    @DisplayName("当调用 getAllOrders 时，应返回所有订单的 DTO 列表")
    void shouldReturnAllOrdersAsDtoList() {

        when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));

        List<OrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD12345", result.get(0).getOrderNumber());
        assertEquals("testUser", result.get(0).getUser().getUserName());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("当使用有效 ID 调用时 getOrderById 时，应返回对应的订单 DTO 并包含航班信息")
    void shouldReturnOrderDtoWithFlightInfoForValidId() {

        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        OrderDto result = orderService.getOrderById(100L);
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("ORD12345", result.getOrderNumber());
        assertNotNull(result.getFlightInfo(), "航班信息不应为空");
        assertEquals("MU5180", result.getFlightInfo().get("flightNumber"));

        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("当使用无效 ID 调用 getOrderById 时，应输出 RunTimeException")
    void shouldThrowExceptionForInvalidId() {

        long invalidId = 999L;
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(invalidId);
        });

        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("当使用有效 ID 调用时 payOrder 时，应返回支付后的订单 DTO ")
    void shouldReturnOrderDtoPayed() {

        when(orderRepository.findById(101L)).thenReturn(Optional.of(testOrderPay));

        orderService.payOrder(101L);
        OrderDto result = orderService.getOrderById(101L);
        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("ORD00002", result.getOrderNumber());
        assertEquals(OrderStatus.PAID, result.getStatus());

        verify(orderRepository, times(2)).findById(101L);
    }

    @Test
    @DisplayName("当使用无效 ID 调用 payOrder 时，应输出 RunTimeException")
    void shouldThrowExceptionForInvalidIdPay() {

        long invalidId = 999L;
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.payOrder(invalidId);
        });

        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("当数据的状态不是 PENDING_PAYMENT 调用 payOrder 时，应输出 RunTimeException")
    void shouldThrowExceptionForNoPendingPayment() {

        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.payOrder(100L);
        });

        assertEquals("Order is payed", exception.getMessage());

        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("当使用有效 ID 调用时 cancelOrder 时，应返回支付后的订单 DTO ")
    void shouldReturnOrderDtoCanceled() {

        when(orderRepository.findById(102L)).thenReturn(Optional.of(testOrderCancel));

        orderService.cancelOrder(102L);
        OrderDto result = orderService.getOrderById(102L);
        assertNotNull(result);
        assertEquals(102L, result.getId());
        assertEquals("ORD00003", result.getOrderNumber());
        assertEquals(OrderStatus.CANCELLED, result.getStatus());

        verify(orderRepository, times(2)).findById(102L);
    }

    @Test
    @DisplayName("当使用无效 ID 调用 payOrder 时，应输出 RunTimeException")
    void shouldThrowExceptionForInvalidIdCancel() {

        long invalidId = 999L;
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(invalidId);
        });

        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("当数据的状态是 CANCELLED 调用 cancelOrder 时，应输出 RunTimeException")
    void shouldThrowExceptionForCancelled() {

        when(orderRepository.findById(103L)).thenReturn(Optional.of(testOrderCancelled));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(103L);
        });

        assertEquals("Order is cancelled", exception.getMessage());

        verify(orderRepository, times(1)).findById(103L);
    }

}
