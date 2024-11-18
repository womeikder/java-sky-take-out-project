package com.sky.controller.admin;

import org.springframework.web.bind.annotation.RequestBody;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "客户端订单管理")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询
     * @param queryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("分页查询")
    public Result<PageResult> list(OrdersPageQueryDTO queryDTO){
        log.info("分页查询参数{}",queryDTO);
        PageResult query = orderService.list(queryDTO);
        return Result.success(query);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics(){
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO statisticsVO = orderService.statistics();
        return Result.success(statisticsVO);
    }

    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("根据id{}查询订单详情",id);
        OrderVO orderVO = orderService.detailById(id);
        return Result.success(orderVO);
    }


    /**
     * 接单
     * @param confirmDTO
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO confirmDTO){
        log.info("根据id{}接单",confirmDTO);
        orderService.confirm(confirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param rejectionDTO
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO rejectionDTO){
        log.info("根据id{}拒单",rejectionDTO);
        orderService.rejection(rejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param cancelDTO
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO cancelDTO){
        log.info("根据id{}取消订单",cancelDTO);
        orderService.cancel(cancelDTO);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        log.info("根据id{}接单",id);
        orderService.complete(id);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        log.info("根据id{}接单",id);
        orderService.delivery(id);
        return Result.success();
    }

}
