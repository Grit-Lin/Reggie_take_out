package com.grit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grit.entity.OrderDetail;
import com.grit.mapper.OrderDetailMapper;
import com.grit.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author:
 * @date:
 * @description
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
