package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    /**
     * 为购物车添加商品
     * @param cartDTO
     */
    @Override
    public void add(ShoppingCartDTO cartDTO) {

        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(cartDTO,cart);
        Long currentId = BaseContext.getCurrentId();
        cart.setUserId(currentId);
        // 判断当前加入的商品在购物车中是否存在
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        // 如果已经存在，只需要将数量加一
        if (list != null && list.size() > 0) {
            ShoppingCart cart1 = list.get(0);
            cart1.setNumber(cart1.getNumber()+1);
            // 将数据更新到数据库
            shoppingCartMapper.updateNumberById(cart1);
        } else {
            // 如果不存在，就向数据表中插入一条数据
            Long dishId = cartDTO.getDishId();
            Long setmealId = cartDTO.getSetmealId();
            // 判断我加入的是套餐还是菜品
            if (dishId != null){
                // 本次添加的是菜品
                Dish dish = dishMapper.getById(dishId);
                cart.setImage(dish.getImage());
                cart.setName(dish.getName());
                cart.setAmount(dish.getPrice());
            } else {
                // 本次添加的是套餐
                Setmeal setmeal = setMealMapper.getById(setmealId);
                cart.setName(setmeal.getName());
                cart.setImage(setmeal.getImage());
                cart.setAmount(setmeal.getPrice());
            }
            cart.setNumber(1);
            cart.setCreateTime(LocalDateTime.now());

            // 将加入的的数据添加到数据库
            shoppingCartMapper.insert(cart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list(Long currentId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;

    }

    /**
     * 清空购物车
     * @param currentId
     */
    @Override
    public void delete(Long currentId) {
        shoppingCartMapper.delete(currentId);

    }

    /**
     * 减少一个购物车中的菜品或套餐
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 获取当前用户信息
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,cart);
        cart.setUserId(currentId);
        // 查询元素
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        // 将数据减一
        ShoppingCart cart1 = list.get(0);
        cart1.setNumber(cart1.getNumber() - 1);
        // 将数据更新到数据库
        shoppingCartMapper.updateNumberById(cart1);
        // 判断商品个数小于一就从数据库删除
        list.forEach( shoppingCart -> {
            Integer number = shoppingCart.getNumber();
            if (number < 1) {
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }
        });

    }
}
