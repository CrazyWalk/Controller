package cn.luyinbros.android.controller.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListenerClassInfo {
    private String targetType;
    private String setter;
    private String remover = "";
    private String type;
    private List<ListenerMethodInfo> methodInfoList = new ArrayList<>();
    private Map<String, ListenerMethodInfo> methodInfoMap = new HashMap<>();
    private String callbackTypeName;


    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getSetter() {
        return setter;
    }

    public void setSetter(String setter) {
        this.setter = setter;
    }

    public String getRemover() {
        return remover;
    }

    public void setRemover(String remover) {
        this.remover = remover;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ListenerMethodInfo> getMethodInfoList() {
        return methodInfoList;
    }

    public void setMethodInfoList(List<ListenerMethodInfo> methodInfoList) {
        this.methodInfoList = methodInfoList;
    }

    public Map<String, ListenerMethodInfo> getMethodInfoMap() {
        return methodInfoMap;
    }

    public void setMethodInfoMap(Map<String, ListenerMethodInfo> methodInfoMap) {
        this.methodInfoMap = methodInfoMap;
    }

    public String getCallbackTypeName() {
        return callbackTypeName;
    }

    public void setCallbackTypeName(String callbackTypeName) {
        this.callbackTypeName = callbackTypeName;
    }

    public void adjust() {
        if (!methodInfoMap.isEmpty()) {
            methodInfoList.clear();
            for (Map.Entry<String, ListenerMethodInfo> entry : methodInfoMap.entrySet()) {
                methodInfoList.add(entry.getValue());
            }
        }
    }


    @Override
    public String toString() {
        return "ListenerClassInfo{" +
                "targetType='" + targetType + '\'' +
                ", setter='" + setter + '\'' +
                ", remover='" + remover + '\'' +
                ", type='" + type + '\'' +
                ", methodInfoList=" + methodInfoList +
                ",methodInfoMap=" + methodInfoMap +
                '}';
    }
}
