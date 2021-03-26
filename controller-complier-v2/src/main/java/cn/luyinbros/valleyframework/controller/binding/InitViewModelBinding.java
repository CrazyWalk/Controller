package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.ClassName;

import javax.annotation.Nullable;


public class InitViewModelBinding {
    private final String methodName;

    public InitViewModelBinding(String methodName) {
        this.methodName = methodName;

    }

    public String getMethodName() {
        return methodName;
    }


    @Override
    public String toString() {
        return "InitViewModelBinding{" +
                "methodName='" + methodName + '\'' +
                '}';
    }


}
