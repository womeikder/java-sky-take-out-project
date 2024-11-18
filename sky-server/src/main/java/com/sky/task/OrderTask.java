package com.sky.task;


import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 定时任务类、定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;


    /**
     * 处理订单超时方法
     */
    @Scheduled(cron = " 0 * * * * ? ")  //每分钟触发一次
    public void ProcessOrderTask(){
        log.info("每分钟处理一次超时订单{}", LocalDateTime.now());
        // 根据状态与时间来查询修改
        List<Long> orderId = orderMapper.IdByTime(Orders.PAID,LocalDateTime.now().minusMinutes(15));
        log.info("超时订单ID{}",orderId);
        if (orderId != null) {
            Orders orders = new Orders();
            for (Long l : orderId) {
                orders.setId(l);
                orders.setStatus(6);
                orders.setCancelReason(MessageConstant.OVERTIME_PAYMENT);
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.setStatus(orders);
            }

        }
    }

    /**
     * 处理每天派送未点击完成方法
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每晚12点触发一次
    public void ProcessDeliveryTask(){
        log.info("每天把处理派送未点击完成任务{}", LocalDateTime.now());
        // 根据状态与时间来查询修改
        List<Long> orderId = orderMapper.IdByTime(Orders.DELIVERY_IN_PROGRESS,LocalDateTime.now().minusHours(1));
        log.info("未完成派送任务ID{}",orderId);
        if (orderId != null) {
            Orders orders = new Orders();
            for (Long l : orderId) {
                orders.setId(l);
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                orderMapper.setStatus(orders);
            }
        }
    }
}
