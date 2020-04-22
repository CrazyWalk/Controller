package cn.luyinbros.valleyframework.controller.listener;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.ResId;


public class ListenerBinding {
    private String methodName;
    private boolean hasArguments;
    private List<TypeMirror> argumentTypeMirrors ;

    private TypeMirror returnTypeMirror;
    private List<ResId> ids = new ArrayList<>();
    private ListenerClassInfo listenerClassInfo;
    private ListenerMethodInfo listenerMethodInfo;
    private boolean isRequired;

    public List<ResId> getIds() {
        return ids;
    }

    public void setIds(List<ResId> ids) {
        this.ids = ids;
    }

    public ListenerClassInfo getListenerClassInfo() {
        return listenerClassInfo;
    }

    public void setListenerClassInfo(ListenerClassInfo listenerClassInfo) {
        this.listenerClassInfo = listenerClassInfo;
    }

    public ListenerMethodInfo getListenerMethodInfo() {
        return listenerMethodInfo;
    }

    public void setListenerMethodInfo(ListenerMethodInfo listenerMethodInfo) {
        this.listenerMethodInfo = listenerMethodInfo;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isHasArguments() {
        return hasArguments;
    }

    public void setHasArguments(boolean hasArguments) {
        this.hasArguments = hasArguments;
    }

    public TypeMirror getReturnTypeMirror() {
        return returnTypeMirror;
    }

    public void setReturnTypeMirror(TypeMirror returnTypeMirror) {
        this.returnTypeMirror = returnTypeMirror;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }


    public List<TypeMirror> getArgumentTypeMirrors() {
        return argumentTypeMirrors;
    }

    public void setArgumentTypeMirrors(List<TypeMirror> argumentTypeMirrors) {
        this.argumentTypeMirrors = argumentTypeMirrors;
    }

    @Override
    public String toString() {
        return "ListenerBinding{" +
                "ids=" + ids +
                ", listenerClassInfo=" + listenerClassInfo +
                ", listenerMethodInfo=" + listenerMethodInfo +
                "isRequired=" + isRequired +
                '}';
    }
}
