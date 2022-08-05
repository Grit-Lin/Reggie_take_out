package com.grit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.grit.common.R;
import com.grit.entity.Employee;
import com.grit.service.impl.EmployeeServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author:
 * @date:
 * @description
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeService;

    /**
     * 登录方法
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        return employeeService.login(httpServletRequest, employee);
    }

    /**
     * 退出登录
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest) {
        return employeeService.logout(httpServletRequest);
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<IPage<Employee>> getPage(int page, int pageSize, String name) {
        IPage<Employee> getPage = employeeService.getPage(page, pageSize, name);
        return R.success(getPage);
    }

    /**
     * 根据ID返回employee对象
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee emp = employeeService.getById(id);
        if(emp != null) return R.success(employeeService.getById(id));
        else return R.error("Id对应的对象不存在");
    }

    /**
     * 修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 添加员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        employeeService.save(request, employee);
        return R.success("员工添加成功！");
    }
}
