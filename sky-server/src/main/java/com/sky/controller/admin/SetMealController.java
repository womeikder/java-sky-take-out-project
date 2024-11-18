package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "套餐管理")
@RestController
@RequestMapping("/admin/setmeal")
public class SetMealController {

    @Autowired
    private SetMealService mealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    @Cacheable(cacheNames = "SetmealCache",key = "#setmealDTO.categoryId") // key: SetmealCache::categoryId
    public Result add(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐条目{}",setmealDTO);
        mealService.add(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> PageSelect(SetmealPageQueryDTO pageQueryDTO){
        log.info("分页查询参数{}",pageQueryDTO);
        PageResult page = mealService.PageSelect(pageQueryDTO);
        return Result.success(page);
    }


    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "SetmealCache", allEntries = true) // 删除所有该包下的所有缓存数据
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除套餐");
        mealService.delete(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐信息")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询信息{}",id);
        SetmealVO setmealVO = mealService.getById(id);
        return Result.success(setmealVO);
    }


    /**
     * 更新套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("更新套餐")
    @CacheEvict(cacheNames = "SetmealCache", allEntries = true) // 删除所有该包下的所有缓存数据
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("更新套餐{}",setmealDTO);
        mealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐的销售状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐销售状态")
    @CacheEvict(cacheNames = "SetmealCache", allEntries = true) // 删除所有该包下的所有缓存数据
    public Result status(@PathVariable Integer status , Long id){
        log.info("{}套餐的销售状态{}",id,status);
        mealService.status(status,id);
        return Result.success();
    }




}
