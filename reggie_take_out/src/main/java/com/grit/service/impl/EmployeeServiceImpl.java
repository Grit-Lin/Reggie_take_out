package com.grit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grit.common.R;
import com.grit.entity.Employee;
import com.grit.mapper.EmployeeMapper;
import com.grit.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author:
 * @date:
 * @description
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    public R<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        //1.对密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeMapper.selectOne(lqw);

        //3.如果没有查询到相应用户，返回登录失败
        if(emp == null) {
            return R.error("登录失败");
        }

        //4.密码比对，如果不一致返回登录失败
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //5.检查是否员工状态为禁用
        if (emp.getStatus() == 0) {
            return R.error("员工已被禁用");
        }

        //6.登录成功，将用户id存到Session中，并返回登录成功;
        httpServletRequest.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    public R<String> logout(HttpServletRequest httpServletRequest) {
        //1.清理Session
        httpServletRequest.getSession().removeAttribute("employee");

        //2.返回退出登录的结果（前端代码中编写了跳转回登录页面的代码）
        return R.success("退出登录成功");
    }

    /**
     * 获取分页对象，同时满足模糊查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public IPage<Employee> getPage(int page, int pageSize, String name) {
        IPage<Employee> getPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Employee :: getName, name)
                .orderByDesc(Employee :: getUpdateTime);
        getPage = employeeMapper.selectPage(getPage, lqw);
        return getPage;
    }

    public Boolean save(HttpServletRequest request, @RequestBody Employee employee) {

        //设置初识密码并对密码进行MD5加密，否则无法登录
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建时间和更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //设置创建者和更新者（这个应该都是针对管理员）
//        Long userId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(userId);
//        employee.setUpdateUser(userId);

        int save = employeeMapper.insert(employee);
        return save > 0;
    }
}
