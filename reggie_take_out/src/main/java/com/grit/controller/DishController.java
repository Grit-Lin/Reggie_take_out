package com.grit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.grit.common.R;
import com.grit.dto.DishDto;
import com.grit.entity.Category;
import com.grit.entity.Dish;
import com.grit.service.impl.CategoryServiceImpl;
import com.grit.service.impl.DishFlavorServiceImpl;
import com.grit.service.impl.DishServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author:
 * @date:
 * @description
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishServiceImpl dishService;


    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        boolean update = dishService.updateWithFlavor(dishDto);
        return update ? R.success("菜品更新成功！") : R.error("菜品更新失败！");
    }


    /**
     * 分页,返回的是DishDto对象
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<IPage<DishDto>> getPage(int page, int pageSize, String name) {
        IPage<DishDto> getPage = dishService.getPage(page, pageSize, name);
        return R.success(getPage);
    }

    /**
     * 根据路径传入的ID查询对象
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        Boolean save = dishService.saveWithFlavor(dishDto);
        return save ? R.success("菜品添加成功！") : R.error("菜品保存失败！");
    }

    /**
     * 根据菜品分类取出所有菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = dishService.listWithFlavor(dish);
        return R.success(dishDtoList);
    }

    /**
     * 删除和批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids) {
        boolean delete = dishService.removeByIdsWithFlavor(ids);
        return delete ? R.success("删除成功！") : R.error("删除失败！");
    }

    /**
     * 批量变更状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, Long[] ids) {
        boolean changeStatus = dishService.changeStatus(status, ids);
        return changeStatus ? R.success("变更状态成功！") : R.error("变更状态失败！");
    }
}
