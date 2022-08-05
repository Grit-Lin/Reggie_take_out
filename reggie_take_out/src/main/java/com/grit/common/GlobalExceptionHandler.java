package com.grit.common;

import com.grit.common.Exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author:
 * @date:
 * @description 全局异常处理
 */
//表示代理所有有这两个注解的类
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//表示一旦抛出这种异常，就会被这个类处理
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        return R.error("添加员工失败！");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception) {
        log.error(exception.getMessage());
        return R.error(exception.getMessage());
    }
}
