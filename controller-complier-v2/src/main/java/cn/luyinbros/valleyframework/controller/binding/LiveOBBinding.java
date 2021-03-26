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
    private String viewModel;
    private String liveData;

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

    public String getViewModel() {
        return viewModel;
    }

    public void setViewModel(String viewModel) {
        this.viewModel = viewModel;
    }

    public String getLiveData() {
        return liveData;
    }

    public void setLiveData(String liveData) {
        this.liveData = liveData;
    }

    @Override
    public String toString() {
        return "LiveOBBinding{" +
                "methodName='" + methodName + '\'' +
                ", paramClassName=" + paramClassName +
                ", viewModel='" + viewModel + '\'' +
                ", liveData='" + liveData + '\'' +
                '}';
    }
}
