package com.grit.controller;

import com.grit.common.R;
import com.grit.entity.User;
import com.grit.service.UserService;
import com.grit.service.impl.UserServiceImpl;
import com.grit.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author:
 * @date:
 * @description
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession) {
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //随机生成4位的验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码是：{}", code);

            httpSession.setAttribute(phone, code);

            return R.success("短信发送成功！");
        }

        return R.error("短信发送失败！");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession) {
        User user = userService.login(map, httpSession);
        if (user != null) {
            return R.success(user);
        }
        return R.error("登录失败！");
    }

}
