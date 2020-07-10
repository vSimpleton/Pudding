package com.pomelo.pudding.event;

import android.view.View;

import com.pomelo.pudding.utils.XClickUtil;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * NAME: 柚子啊
 * DATE: 2020/7/10
 * DESC: Aspect AOP处理类
 */

@Aspect
public class SingleClickAspect {

    /**
     * 定义切点，标记切点为所有被@SingleClick注解的方法
     */
    @Pointcut("execution(@com.pomelo.pudding.event.SingleClick * *(..))")
    public void methodAnnotated() {
    }

    /**
     * 定义一个切面方法，包裹切点方法
     */
    @Around("methodAnnotated()")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出方法的参数
        View view = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof View) {
                view = (View) arg;
                break;
            }
        }
        if (view == null) {
            return;
        }

        // 取出方法的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        if (!method.isAnnotationPresent(SingleClick.class)) {
            return;
        }

        SingleClick singleClick = method.getAnnotation(SingleClick.class);
        // 判断是否快速点击
        if (!XClickUtil.isFastDoubleClick(view, singleClick.value())) {
            //不是快速点击，执行原方法
            joinPoint.proceed();
        }
    }

}
