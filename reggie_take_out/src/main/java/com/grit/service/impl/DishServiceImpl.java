package com.grit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grit.common.Exception.CustomException;
import com.grit.common.R;
import com.grit.dto.DishDto;
import com.grit.entity.*;
import com.grit.mapper.DishMapper;
import com.grit.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:
 * @date:
 * @description
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorServiceImpl dishFlavorService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private SetmealDishServiceImpl setmealDishService;


    /**
     * 分页，要注意Dish与DishDto之间的转化
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public IPage<DishDto> getPage (int page, int pageSize, String name) {
        IPage<Dish> dishPage = new Page<>(page, pageSize);
        IPage<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Dish :: getName, name)
                .orderByDesc(Dish :: getUpdateTime);
        this.page(dishPage, lqw);

        //对象拷贝（忽略records对象，因为records中的泛型是Dish，得要重新封装一个List）
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //创建一个新的records
        List<Dish> records = dishPage.getRecords();
        List<DishDto> newRecords = null;
        //通过Dish对象的分类id查分类表
        newRecords = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(newRecords);

        return dishDtoPage;
    }

    /**
     * 将dto对象中存储的Dish和Flavor分别持久化，并且要对Flavor中的DishId属性赋值
     * @param dishDto
     * @return
     */
    public Boolean saveWithFlavor(DishDto dishDto) {

        boolean saveDish = this.save(dishDto);

        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();

        // 判断口味是否存在，如果存在存入口味表中
        if (flavors != null) {
            flavors = flavors.stream().map((item) -> {
                item.setDishId(dishId);
                return item;
            }).collect(Collectors.toList());

            boolean saveFlavors = dishFlavorService.saveBatch(flavors);
            return saveFlavors && saveDish;
        }
        return saveDish;
    }

    /**
     * 更新Dish信息，删除原先的口味信息，保存新的口味信息
     * @param dishDto
     * @return
     */
    public boolean updateWithFlavor(DishDto dishDto) {
        boolean updateDish = this.updateById(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();

        // 删除原先的口味信息
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor :: getDishId, dishId);
        dishFlavorService.remove(lqw);

        // 保存新的口味信息
        if (flavors != null) {
            flavors = flavors.stream().map((item) -> {
                item.setDishId(dishId);
                return item;
            }).collect(Collectors.toList());

            boolean saveFlavors = dishFlavorService.saveBatch(flavors);
            return saveFlavors && updateDish;
        }
        return updateDish;
    }

    /**
     * 通过Id查询菜品和口味，封装到DishDto对象中返回
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor :: getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(lqw);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 返回对应分类下的所有菜品Dto的集合，并将每个菜品的口味封装进DishDto
     * @param dish
     * @return
     */
    public List<DishDto> listWithFlavor(Dish dish) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId() != null, Dish :: getCategoryId, dish.getCategoryId());
        List<Dish> list = this.list(lqw);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor :: getDishId, item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

        return dishDtoList;
    }

    /**
     * 将菜品和菜品口味删除，支持批量删除
     * @param ids
     * @return
     */
    public boolean removeByIdsWithFlavor(Long[] ids) {

        // 查询是否有套餐关联了这个菜品
        for (Long id : ids) {

            // 判断菜品是否在售，在售则无法删除
            Dish dish = this.getById(id);
            Integer status = dish.getStatus();
            if (status == 1) {
                throw new CustomException("所删除的菜品中，" + dish.getName() + "菜品在售，无法删除！");
            }

            // 判断删除的菜品是否有被其他套餐关联，有则无法删除
            LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
            lqw.eq(SetmealDish :: getDishId, id);
            int count = setmealDishService.count(lqw);
            if (count > 0) {
                throw new CustomException(dish.getName() + "菜品被套餐关联，删除失败！");
            }
        }


        boolean flag = true;
        // 删除口味表中的记录
        for (Long id : ids) {
            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DishFlavor :: getDishId, id);
            flag = flag && dishFlavorService.remove(lqw);
        }
        return flag && this.removeByIds(Arrays.asList(ids));// 删除菜品
    }

    public boolean changeStatus(int status, Long[] ids) {
        List<Dish> list = Arrays.asList(ids).stream().map((id) -> {
            Dish dish = this.getById(id);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        boolean changeStatus = this.updateBatchById(list);
        return changeStatus;
    }

}
