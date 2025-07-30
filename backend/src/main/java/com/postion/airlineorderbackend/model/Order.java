package com.postion.airlineorderbackend.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="orders_lqw")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="order_number")
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal amount;
    @Column(name="creation_date")
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
