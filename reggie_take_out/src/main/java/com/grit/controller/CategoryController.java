package com.grit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.grit.common.R;
import com.grit.entity.Category;
import com.grit.mapper.CategoryMapper;
import com.grit.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author:
 * @date:
 * @description
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping("/page")
    public R<IPage<Category>> getPage(int page, int pageSize) {
        IPage<Category> getPage = categoryService.getPage(page, pageSize);
        return R.success(getPage);
    }

    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("分类保存成功！");
    }

    @DeleteMapping
    public R<String> delete(Long ids) {
       categoryService.remove(ids);
       return R.success("分类删除成功！");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功！");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        List<Category> list = categoryService.list(category);
        return R.success(list);
    }
}
