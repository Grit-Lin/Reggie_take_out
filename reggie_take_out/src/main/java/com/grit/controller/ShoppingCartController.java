package com.grit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grit.common.BaseContext;
import com.grit.common.R;
import com.grit.entity.Dish;
import com.grit.entity.ShoppingCart;
import com.grit.service.ShoppingCartService;
import com.grit.service.impl.ShoppingCartServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author:
 * @date:
 * @description
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartServiceImpl shoppingCartService;

    /**
     * 展示购物车中的菜品或套餐
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart :: getUserId, BaseContext.getCurrentId())
                .orderByDesc(ShoppingCart :: getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);
    }

    /**
     * 向购物车中添加菜品或套餐
     * @param request
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(HttpServletRequest request, @RequestBody ShoppingCart shoppingCart) {
        ShoppingCart add = shoppingCartService.add(request, shoppingCart);
        return R.success(add);
    }

    /**
     * 从购物车中减少/删除 菜品/套餐
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        ShoppingCart sub = shoppingCartService.sub(shoppingCart);
        return R.success(sub);
    }

    /**
     * 清空购物车中的菜品
     * @param request
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(userId != null, ShoppingCart :: getUserId, userId);
        boolean remove = shoppingCartService.remove(lqw);
        return remove ? R.success("已清空购物车！") : R.error("清空购物车失败！");
    }
}
