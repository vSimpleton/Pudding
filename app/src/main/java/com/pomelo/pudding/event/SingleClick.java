package com.pomelo.pudding.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NAME: 柚子啊
 * DATE: 2020/7/10
 * DESC: 定义一个自定义注解
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SingleClick {

    /**
     * 点击间隔时间
     */
    long value() default 1000;

}
