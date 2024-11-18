package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {


    /**
     * 插入订单
     * @param orders
     */
    Long insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**

     * 用于替换微信支付更新数据库状态的问题

     * @param orderStatus

     * @param orderPaidStatus

     */

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);


    /**
     *
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO pageQueryDTO);

    /**
     *  根据id查询数据
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);


    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Select("select count(*) from orders where status = 3")
    Integer getConfirmed();
    @Select("select count(*) from orders where status = 4")
    Integer getDeliveryInProgress();
    @Select("select count(*) from orders where status = 2")
    Integer getToBeConfirmed();

    /**
     * 修改当前订单的状态
     * @param orders
     */
    void setStatus(Orders orders);

    /**
     * 根据订单状态以及下单时间查询订单
     * @param i
     * @param now
     * @return
     */
    @Select("select id from orders where status = #{i} and order_time < #{now}")
    List<Long> IdByTime(Integer i, LocalDateTime now);

    /**
     * 查询每天的营业额
     * @param dateMin
     * @param dateMax
     * @return
     */
    @Select("select sum(amount) from orders where status = 5 and order_time > #{dateMin} and order_time < #{dateMax}")
    Long StatisticsGetByTime(LocalDateTime dateMin,LocalDateTime dateMax);

    /**
     * 查询所有用户总数
     * @return
     */
    @Select("select count(openid) from user where create_time > #{dateMin} and create_time < #{dateMax}")
    Long total(LocalDateTime dateMin,LocalDateTime dateMax);

    /**
     * 计算订单的总数
     * @param dateTimeMin
     * @param dateTimeMax
     * @param status
     * @return
     */
    Integer getByStatus(LocalDateTime dateTimeMin, LocalDateTime dateTimeMax, Integer status);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);


}
