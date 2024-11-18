package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;


public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO submitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param
     * @return
     */
    PageResult pageQuery4User(int page,int pageSize,Integer status);

    /**
     * 订单详情
     * @param id
     * @return
     */
    OrderVO detailById(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancelOrder(Long id) throws Exception;

    /**
     * 再来一单
     * @param id
     */
    void againOrder(Long id);

    /**
     * 管理端分页查询
     * @param queryDTO
     * @return
     */
    PageResult list(OrdersPageQueryDTO queryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     * @param confirmDTO
     */
    void confirm(OrdersConfirmDTO confirmDTO);

    /**
     * 拒单
     * @param rejectionDTO
     */
    void rejection(OrdersRejectionDTO rejectionDTO);

    /**
     * 取消订单
     * @param cancelDTO
     */
    void cancel(OrdersCancelDTO cancelDTO);

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 客户催单
     * @param id
     */
    void reminder(Long id);
}
