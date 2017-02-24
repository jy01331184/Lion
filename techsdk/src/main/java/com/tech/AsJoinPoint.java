package com.tech;

/**
 * Created by tianyang on 16/12/16.
 */
public class AsJoinPoint {

    public String className;
    public String methodName;

    public static AsJoinPoint of(String className,String methodName){
        AsJoinPoint joinPoint = new AsJoinPoint();
        joinPoint.className = className;
        joinPoint.methodName = methodName;
        return joinPoint;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }
}
