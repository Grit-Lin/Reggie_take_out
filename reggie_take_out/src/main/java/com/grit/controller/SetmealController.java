package com.grit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.grit.common.R;
import com.grit.dto.SetmealDto;
import com.grit.entity.Setmeal;
import com.grit.entity.SetmealDish;
import com.grit.service.SetmealDishService;
import com.grit.service.impl.SetmealDishServiceImpl;
import com.grit.service.impl.SetmealServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:
 * @date:
 * @description
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealServiceImpl setmealService;


    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        boolean save = setmealService.saveWithSetmealDish(setmealDto);

        return save ? R.success("套餐添加成功！") : R.error("套餐添加失败！");
    }

    @GetMapping("/page")
    public R<IPage<SetmealDto>> getPage(int page, int pageSize, String name) {
        IPage<SetmealDto> getPage = setmealService.getPage(page, pageSize, name);
        return R.success(getPage);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithSetmealDish(id);
        return R.success(setmealDto);
    }

    @DeleteMapping
    public R<String> delete(Long[] ids) {
        boolean remove = setmealService.removeByIdsWithDish(ids);
        return remove ? R.success("删除成功!") : R.error("删除失败!");
    }

    /**
     * 批量变更状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, Long[] ids) {
        boolean changeStatus = setmealService.changeStatus(status, ids);
        return changeStatus ? R.success("变更状态成功！") : R.error("变更状态失败！");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null, Setmeal :: getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal :: getStatus, setmeal.getStatus());
        List<Setmeal> list = setmealService.list(lqw);
        return R.success(list);
    }

}
