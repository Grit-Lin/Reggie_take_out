package com.grit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grit.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
