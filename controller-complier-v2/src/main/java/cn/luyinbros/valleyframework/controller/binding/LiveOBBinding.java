package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveOBBinding {
    private String methodName;
    private ClassName paramClassName;
    private Map<String, String> map ;
    private boolean isForever;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public ClassName getParamClassName() {
        return paramClassName;
    }

    public void setParamClassName(ClassName paramClassName) {
        this.paramClassName = paramClassName;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public boolean isForever() {
        return isForever;
    }

    public void setForever(boolean forever) {
        isForever = forever;
    }

    @Override
    public String toString() {
        return "LiveOBBinding{" +
                "methodName='" + methodName + '\'' +
                ", paramClassName=" + paramClassName +
                ", map=" + map +
                ", isForever=" + isForever +
                '}';
    }
}
