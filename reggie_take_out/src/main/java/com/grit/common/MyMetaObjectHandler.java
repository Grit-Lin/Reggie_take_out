package com.grit.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author:
 * @date:
 * @description 自定义的元数据对象处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入自动填充字段【insert】");

        Long currentId = BaseContext.getCurrentId();
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", currentId);
        metaObject.setValue("updateUser", currentId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新自动填充字段【update】");

        Long currentId = BaseContext.getCurrentId();
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", currentId);
    }
}
