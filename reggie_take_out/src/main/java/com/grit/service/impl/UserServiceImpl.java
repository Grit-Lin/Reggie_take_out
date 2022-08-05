package com.grit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grit.entity.User;
import com.grit.mapper.UserMapper;
import com.grit.service.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author:
 * @date:
 * @description
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public User login(Map map, HttpSession httpSession) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        Object codeInSession = httpSession.getAttribute(phone);

        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User :: getPhone, phone);
            User user = this.getOne(lqw);

            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                this.save(user);
            }
            user = this.getOne(lqw);
            httpSession.setAttribute("user", user.getId());
            return user;
        }
        return null;
    }
}
