package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面
 *
 * @author aoao
 * @create 2025-04-03-11:39
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /*
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws Throwable {
        // 获取当前方法的枚举类型是 update 还是 insert
        // 获取签名 Signature 接口提供的信息是比较一般的，例如可以获取方法名，但无法获取方法的参数类型、返回类型等更详细的信息 向下转型 MethodSignature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法
        Method method = signature.getMethod();
        // 获取注解
        AutoFill annotation = method.getAnnotation(AutoFill.class);
        OperationType value = null;
        if (annotation != null) {
            value = annotation.value();
        }
        // 获取实体
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        // 准备赋值的数据，通过 token 获取 id
        LocalDateTime localDateTime = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        // 根据当前不同的类型操作，为对应的属性通过反射赋值
        // setUpdateTime
        Method setUpdateTime = entity.getClass().getMethod("setUpdateTime", LocalDateTime.class);
        setUpdateTime.invoke(entity, localDateTime);
        // setUpdateUser
        Method setUpdateUser = entity.getClass().getMethod("setUpdateUser", Long.class);
        setUpdateUser.invoke(entity, currentId);
        if (value == OperationType.INSERT) {
            // 获取创建人和时间
            Method setCreateUser = entity.getClass().getMethod("setCreateUser", Long.class);
            Method setCreateTime = entity.getClass().getMethod("setCreateTime", LocalDateTime.class);
            setCreateUser.invoke(entity, currentId);
            setCreateTime.invoke(entity, localDateTime);
        }
    }
}