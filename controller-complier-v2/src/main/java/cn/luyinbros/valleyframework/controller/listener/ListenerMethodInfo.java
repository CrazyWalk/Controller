package cn.luyinbros.valleyframework.controller.listener;

import java.util.Arrays;

public class ListenerMethodInfo {
    private String name;
    private String[] parameters;
    private String returnType="void";
    private String defaultReturn="null";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getDefaultReturn() {
        return defaultReturn;
    }

    public void setDefaultReturn(String defaultReturn) {
        this.defaultReturn = defaultReturn;
    }

    @Override
    public String toString() {
        return "ListenerMethodInfo{" +
                "name='" + name + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", returnType='" + returnType + '\'' +
                ", defaultReturn='" + defaultReturn + '\'' +
                '}';
    }
}
