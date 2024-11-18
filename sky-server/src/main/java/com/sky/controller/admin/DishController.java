package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService service;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 增加菜品
     * @param dto
     * @return
     */
    @PostMapping
    @ApiOperation("增加菜品")
    public Result add(@RequestBody DishDTO dto){
        log.info("增加菜品的数据{}",dto);
        String key = "dish_"+dto.getId();
        ClearCache(key);
        service.SaveWithFlavor(dto);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> select(DishPageQueryDTO dto){
        log.info("菜品查询的信息{}",dto);
        PageResult dish = service.select(dto);
        return Result.success(dish);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除的菜品id{}",ids);
        ClearCache("dish_*");
        service.deleteByIds(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品信息")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询信息{}",id);
        DishVO dishVO = service.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品传递来的属性{}",dishDTO);
        ClearCache("dish_*");
        service.update(dishDTO);
        return Result.success();
    }


    /**
     * 菜品的起售停售
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品的起售停售")
    public Result status(@PathVariable Integer status,Long id){
        log.info("{}菜品起售or停售{}",id,status);
        ClearCache("dish_*");
        service.status(status,id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = service.list(categoryId);
        return Result.success(list);
    }

    /**
     * 抽取清理redis缓存方法
     * @param pattern
     */
    public void ClearCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }


}
