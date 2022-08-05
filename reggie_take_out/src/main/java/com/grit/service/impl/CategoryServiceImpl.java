package com.grit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grit.common.Exception.CustomException;
import com.grit.entity.Category;
import com.grit.entity.Dish;
import com.grit.entity.Setmeal;
import com.grit.mapper.CategoryMapper;
import com.grit.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:
 * @date:
 * @description
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishServiceImpl dishService;
    @Autowired
    private SetmealServiceImpl setmealService;

    public IPage<Category> getPage (int page, int pageSize) {
        IPage<Category> getPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category :: getSort);
        getPage = categoryMapper.selectPage(getPage, lqw);
        return getPage;
    }

    /**
     * 根据id删除分类，并提前判断是否有绑定菜品或套餐
     * @param id
     */
    public void remove(Long id) {

        //1.是否关联菜品，如果已经关联，抛出业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish :: getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        if (count1 > 0) {
            //抛出自定义的业务异常
            throw new CustomException("当前分类下绑定了菜品，无法删除");
        }
        //2.是否关联套餐，如果已经关联，抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal :: getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

         if (count2 > 0) {
            //抛出自定义的业务异常
             throw new CustomException("当前分类下绑定了套餐，无法删除");
        }

         this.removeById(id);
    }



    public List<Category> list(Category category) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType() != null, Category :: getType, category.getType())
                .orderByAsc(Category :: getSort)
                .orderByDesc(Category :: getUpdateTime);
        return categoryMapper.selectList(lqw);
    }

}
