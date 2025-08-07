package com.postion.airlineorderbackend.adapter.outbound;

import com.postion.airlineorderbackend.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class AirlineApiClient {
    @Async
    public String issueTicket(Long orderId) throws InterruptedException {
        System.out.println("开始为订单：" + orderId + " 调用航司接口出票。。。");
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));
        if (ThreadLocalRandom.current().nextInt(10) < 8) {
            System.out.println("订单：" + orderId + " 出票成功！");
            return "TKT" + System.currentTimeMillis();
        } else {
            System.out.println("订单：" + orderId + " 出票失败：航司返回错误！");
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "AirLine Api Error: Insufficient seats");
        }
    }
}
