package com.grit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grit.common.BaseContext;
import com.grit.entity.ShoppingCart;
import com.grit.mapper.ShoppingCartMapper;
import com.grit.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author:
 * @date:
 * @description
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    public ShoppingCart add(HttpServletRequest request, ShoppingCart shoppingCart) {
        // 获取用户id
        Long userId = (Long) request.getSession().getAttribute("user");

        // 如果添加到购物车的菜品或者套餐已经存在在购物车中，则对数目加一
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(userId != null, ShoppingCart :: getUserId, userId)
                .eq(shoppingCart.getDishId() != null, ShoppingCart :: getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart :: getSetmealId, shoppingCart.getSetmealId());
//                .eq(shoppingCart.getDishFlavor() != null, ShoppingCart :: getDishFlavor, shoppingCart.getDishFlavor());
        ShoppingCart item = this.getOne(lqw);
        if (item == null) {
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
        }else {
            item.setNumber(item.getNumber() + 1);
            this.updateById(item);
        }
        item = this.getOne(lqw);
        return item;
    }

    public ShoppingCart sub(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        Long userId = BaseContext.getCurrentId();
        lqw.eq(userId != null, ShoppingCart :: getUserId, userId)
                .eq(shoppingCart.getDishId() != null, ShoppingCart :: getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart :: getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart item = this.getOne(lqw);
        Integer number = item.getNumber();
        if (number > 1) {
            item.setNumber(number - 1);
            this.updateById(item);
        }else {
            this.removeById(item);
        }
        item = this.getOne(lqw);
        return item;
    }
}
