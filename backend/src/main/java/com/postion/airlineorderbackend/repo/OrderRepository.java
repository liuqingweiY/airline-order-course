package com.postion.airlineorderbackend.repo;

import com.postion.airlineorderbackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAll();
    Optional<Order> findById(Long id);
    @Transactional
    default Order updateOrInsert(Order entity) {
        return save(entity);
    };
}
