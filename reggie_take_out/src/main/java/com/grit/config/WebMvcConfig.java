package com.grit.config;

import com.grit.common.JacksonObjectMapper;
import com.grit.handlerInterceptor.LoginHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author:
 * @date:
 * @description
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //第一个方法是设置url中访问的路径，第二个方法是设置这个访问路径映射到类路径下的位置。
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("front/**").addResourceLocations("classpath:/front/");
    }

//    @Override
//    protected void addInterceptors(InterceptorRegistry registry) {
//        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new LoginHandlerInterceptor());
//        interceptorRegistration.addPathPatterns("/**");
//        interceptorRegistration.excludePathPatterns(
//                "/employee/login",
//                "/employee/logout",
//                "/backend/**",
//                "/front/**"
//        );
//    }


    /**
     * 拓展MVC框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器（就是为消息转换器对象设置自定义的JSON转换器，这里使用的是Jackson）
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将创建的转换器放在mvc的转换器列表中，并且为了保证我们自定义的消息转换器能生效，我们得要设置转换器在最前位。
        converters.add(0, messageConverter);
    }

}
