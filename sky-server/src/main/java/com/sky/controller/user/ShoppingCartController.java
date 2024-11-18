package com.sky.controller.user;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端-购物车相关接口")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 为购物车添加商品
     * @param cartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("为购物车添加商品")
    public Result add(@RequestBody ShoppingCartDTO cartDTO){
        log.info("为购物车添加商品{}",cartDTO);
        shoppingCartService.add(cartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list(){
        Long currentId = BaseContext.getCurrentId();
        log.info("查看{}用户购物车",currentId);
        List<ShoppingCart> shoppingCart = shoppingCartService.list(currentId);
        return Result.success(shoppingCart);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result delete(){
        Long currentId = BaseContext.getCurrentId();
        log.info("清空{}用户购物车",currentId);
        shoppingCartService.delete(currentId);
        return Result.success();
    }

    /**
     * 减少一个购物车中的菜品或套餐
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/sub")
    @ApiOperation("减少一个购物车中的菜品或套餐")
    public Result subById(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("减少一个购物车中的菜品或套餐");
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }


}
