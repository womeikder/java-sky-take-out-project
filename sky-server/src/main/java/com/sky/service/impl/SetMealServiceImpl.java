package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper mealMapper;
    @Autowired
    private SetMealDishMapper mealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void add(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        mealMapper.insert(setmeal);

        // 插入菜品
        Long id = setmeal.getId();
        List<SetmealDish> dishList = setmealDTO.getSetmealDishes();
        dishList.forEach(setmealDish -> setmealDish.setSetmealId(id));

        mealDishMapper.insert(dishList);

    }

    /**
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    @Override
    public PageResult PageSelect(SetmealPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<SetmealVO> page = mealMapper.PageSelect(pageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());

    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        // 查询套餐是否处于起售中，起售套餐不能删除
        for (Long id : ids) {
            Setmeal setmeal = mealMapper.getById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        // 可删除、同时删除相关连的菜品表数据
        mealMapper.delete(ids);
        mealDishMapper.delete(ids);

    }

    /**
     * 更新套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // 更新套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        mealMapper.update(setmeal);

        // 更新菜品
        //先删除原有的口味数据再插入新的，实现更新
        mealDishMapper.deleteByDishId(setmealDTO.getId());
        // 向口味表插入多条数据
        Long id = setmeal.getId();
        List<SetmealDish> dishList = setmealDTO.getSetmealDishes();
        dishList.forEach(setmealDish -> setmealDish.setSetmealId(id));

        mealDishMapper.insert(dishList);
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        // 获取套餐信息
        Setmeal setmeal = mealMapper.getById(id);
        // 获取菜品信息
        List<SetmealDish> dishList = mealDishMapper.getById(id);

        // 将套餐与菜品融合
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(dishList);

        return setmealVO;
    }

    /**
     * 套餐的销售状态
     * @param status
     * @param id
     */
    @Override
    public void status(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        mealMapper.update(setmeal);
    }

// 用户小程序端相关接口

    /**
     * 根据分类id查询套餐
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = mealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return mealMapper.getDishItemBySetmealId(id);
    }
}
