package com.sky.mapper;


import com.sky.dto.DishDTO;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insert(List<DishFlavor> flavors);

    /**
     * 根据dish_id删除口味
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据dish_id集合来删除对应的菜品口味
     * @param ids
     */
    void deleteByDishIds(List<Long> ids);

    /**
     * 根据dish_id查询口味信息
     * @param dish_id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dish_id}")
    List<DishFlavor> getById(Long dish_id);
}
