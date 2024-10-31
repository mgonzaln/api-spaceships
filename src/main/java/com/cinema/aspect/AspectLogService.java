package com.cinema.aspect;

import com.cinema.exception.NegativeIDNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AspectLogService {

    @AfterThrowing(pointcut = "execution(* com.cinema.controllers.Controller.*(..))", throwing = "ex")
    public void afterThrowingAdvice(NegativeIDNotAllowedException ex) {
      log.info(ex.getMessage());
    }
}
