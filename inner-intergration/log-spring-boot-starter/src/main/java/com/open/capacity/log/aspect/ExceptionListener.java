package com.open.capacity.log.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.context.request.RequestContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常捕获切面
 *
 * @author kongchong
 */
@Aspect
@Slf4j
@ConditionalOnClass({HttpServletRequest.class, RequestContextHolder.class})
public class ExceptionListener {

   

    @AfterThrowing(value = "@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller) || @within(com.kc.exception.notice.annotation.ExceptionNotice)", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, Exception e) {
        
    }
}
