package com.grit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grit.common.Exception.CustomException;
import com.grit.common.R;
import com.grit.dto.SetmealDto;
import com.grit.entity.*;
import com.grit.mapper.SetmealMapper;
import com.grit.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:
 * @date:
 * @description
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishServiceImpl setmealDishService;

    @Autowired
    private CategoryServiceImpl categoryService;

    /**
     * 将套餐和套餐内的菜品分别保存
     * @param setmealDto
     * @return
     */
    public boolean saveWithSetmealDish(SetmealDto setmealDto) {
        boolean saveSetmeal = this.save(setmealDto);
        Long setmealId = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        if (setmealDishes != null) {
            setmealDishes = setmealDishes.stream().map((item) -> {
                item.setSetmealId(setmealId);
                return item;
            }).collect(Collectors.toList());
            boolean saveDishes = setmealDishService.saveBatch(setmealDishes);
            return saveDishes && saveSetmeal;
        }

        return saveSetmeal;
    }

    public IPage<SetmealDto> getPage(int page, int pageSize, String name) {
        IPage<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Setmeal :: getName, name)
                .orderByDesc(Setmeal :: getUpdateTime);
        this.page(setmealPage, lqw);

        IPage<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> newRecords = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = setmealDto.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(newRecords);
        return setmealDtoPage;
    }


    /**
     * 将套餐和套餐对应的套餐菜品封装进SetmealDish对象
     * @param id
     * @return
     */
    public SetmealDto getByIdWithSetmealDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        // 封装套餐菜品
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish :: getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(lqw);
        setmealDto.setSetmealDishes(setmealDishes);

        // 获取分类名称
        Long categoryId = setmealDto.getCategoryId();
        Category category = categoryService.getById(id);
        if (category != null) {
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
        }

        return setmealDto;
    }

    public boolean removeByIdsWithDish(Long[] ids) {
        boolean flag = true;
        for (Long id : ids) {
            // 判断套餐是否在售，在售则无法删除
            Setmeal setmeal = this.getById(id);
            Integer status = setmeal.getStatus();
            if (status == 1) {
                throw new CustomException("所删除的套餐中，"+ setmeal.getName() +"套餐在售，无法删除！");
            }

            // 删除对应id的套餐下关联的SetmealDish表中的记录
            LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
            lqw.eq(SetmealDish :: getSetmealId, id);
            flag = flag && setmealDishService.remove(lqw);
        }
        return flag && this.removeByIds(Arrays.asList(ids));
    }

    public boolean changeStatus(int status, Long[] ids) {
        List<Setmeal> list = Arrays.asList(ids).stream().map((id) -> {
            Setmeal setmeal = this.getById(id);
            setmeal.setStatus(status);
            return setmeal;
        }).collect(Collectors.toList());
        boolean changeStatus = this.updateBatchById(list);
        return changeStatus;
    }
}
