package com.grit.filter;

import com.alibaba.fastjson.JSON;
import com.grit.common.BaseContext;
import com.grit.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author:
 * @date:
 * @description
 */

/**
 * 检测用户是否已经登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")//拦截所有请求
@Slf4j
public class LoginCheckFilter implements Filter {

    //支持通配符的路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;



        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);

        //将需要放行的请求路径封装成一个数组
        String[] urls = {
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/user/sendMsg",
            "/user/login"
        };

        //检查当前的URI是否需要拦截
        boolean check = check(urls, requestURI);
        if(check) {
            filterChain.doFilter(request, response);
            return;
        }

        //检查当前员工是否登录，如果已登录也放行
        Long empId = (Long) request.getSession().getAttribute("employee");
        if(empId != null) {
            log.info("已登录，员工ID是{}", request.getSession().getAttribute("employee"));
            BaseContext.setCurrentId(empId);//将员工id存入ThreadLocal
            filterChain.doFilter(request, response);
            return;
        }

        //检查当前用户是否登录，如果已登录也放行
        Long userId = (Long) request.getSession().getAttribute("user");
        if(userId != null) {
            log.info("已登录，用户ID是{}", request.getSession().getAttribute("user"));
            BaseContext.setCurrentId(userId);//将用户id存入ThreadLocal
            filterChain.doFilter(request, response);
            return;
        }

        //除了以上情况外都需要拦截，向前端响应信息，让页面跳转回登录页面
        //通过response向前端返回数据，将R对象转换为JSON对象返回，message为NOTLOGIN
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls, String requestURI) {
        for(String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) return true;
        }
        return false;
    }
}
