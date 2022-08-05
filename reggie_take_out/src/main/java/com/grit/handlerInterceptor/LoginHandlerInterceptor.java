package com.grit.handlerInterceptor;

import com.grit.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author:
 * @date:
 * @description
 */
@Slf4j
public class LoginHandlerInterceptor implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String requestURI = request.getRequestURI();
//        log.info("执行了拦截器的preHandle方法，拦截了{}", requestURI);
//
//        try {
//            Employee employee = (Employee)request.getSession().getAttribute("employee");
//            if (employee != null) {
//                return true;
//            }
//
//            response.sendRedirect("/backend/page/login/login.html");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return false;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        log.info("执行了拦截器的postHandle方法");
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        log.info("执行了拦截器的afterCompletion方法");
//    }
}
