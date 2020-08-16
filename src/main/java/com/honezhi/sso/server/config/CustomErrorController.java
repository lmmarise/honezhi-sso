package com.honezhi.sso.server.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常 controller
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        /*
         * javax.servlet.error.status_code             类型为Integer        错误状态代码
         * javax.servlet.error.exception_type          类型为Class          异常的类型
         * javax.servlet.error.message                 类型为String         异常的信息
         * javax.servlet.error.exception               类型为Throwable      异常类
         * javax.servlet.error.request_uri             类型为String         异常出现的页面
         * javax.servlet.error.servlet_name            类型为String         异常出现的servlet名
         */
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "/404";
        } else {
            return "/error";
        }
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
