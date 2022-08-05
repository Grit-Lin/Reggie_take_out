package com.grit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.grit.common.BaseContext;
import com.grit.common.R;
import com.grit.entity.Orders;
import com.grit.service.OrderService;
import com.grit.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Action;

/**
 * @author:
 * @date:
 * @description
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功！");
    }

    @GetMapping("/userPage")
    public R<IPage<Orders>> getPage(int page, int pageSize) {
        IPage<Orders> userPage = orderService.getPage(page, pageSize);
        return R.success(userPage);
    }
}
