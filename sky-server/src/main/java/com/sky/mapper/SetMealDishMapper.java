package com.sky.mapper;


import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {


    List<Long> getSetMealIdsByDishIds(List<Long> ids);

    /**
     * 插入菜品
     * @param dishList
     */
    void insert(List<SetmealDish> dishList);

    /**
     * 同套餐一起删除的菜品
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 删除菜品
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteByDishId(Long id);

    /**
     * 根据套餐id查询信息
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getById(Long id);
}
