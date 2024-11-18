package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 插入订单明细表
     * @param detail
     */
    void insert(List<OrderDetail> detail);

    /**
     * 历史订单查询
     * @param
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);


    /**
     * 查询已完成订单中销量最好的10款
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getByTop(LocalDateTime begin, LocalDateTime end);
}
