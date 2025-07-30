package com.postion.airlineorderbackend.model;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    TICKETING_IN_PROGRESS,
    TICKETING_FAILED,
    TICKETED,
    CANCELLED;
}
