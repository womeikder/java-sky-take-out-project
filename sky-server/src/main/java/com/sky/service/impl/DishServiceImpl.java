package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private SetMealMapper setmealMapper;

    /**
     * 保存菜品及其口味
     * @param dto
     */
    @Transactional
    @Override
    public void SaveWithFlavor(DishDTO dto) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dto,dish);
        // 向菜品表插入一条数据
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        // 向口味表插入多条数据
        List<DishFlavor> flavors = dto.getFlavors();
        if (!flavors.isEmpty() && flavors.size() > 0 ){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorMapper.insert(flavors);
        }

    }

    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    @Override
    public PageResult select(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<DishVO> page = dishMapper.select(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        // 判断菜品是否在起售中，起售中的菜品不能删除
        for (Long id : ids) {
             Dish dish = dishMapper.getById(id);
             if(dish.getStatus() == StatusConstant.ENABLE){
                 // 起售中的菜品不允许删除，直接向前端抛出异常信息
                 throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
             }
        }
        // 判断菜品是否包含在某个套餐内，包含的话就不能删除
        List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishIds(ids);
        if (setMealIds != null && setMealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
/*        // 删除菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
            // 删除对应的菜品口味
            dishFlavorMapper.deleteByDishId(id);
        }*/

        // 删除菜品数据
        dishMapper.deleteByIds(ids);
        // 删除对应的菜品口味
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 获取菜品信息
        Dish dish = dishMapper.getById(id);
        // 获取口味信息
        List<DishFlavor> dto = dishFlavorMapper.getById(id);
        // 融合为一个DishVO对象
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dto);

        return dishVO;

    }

    /**
     * 修改菜品信息
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        // 拷贝到合适对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        //先删除原有的口味数据再插入新的，实现更新
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        // 向口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!flavors.isEmpty() && flavors.size() > 0 ){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insert(flavors);
        }
    }

    /**
     * 菜品的起售停售
     * @param status
     */
    @Override
    public void status(Integer status,Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }


    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getById(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
