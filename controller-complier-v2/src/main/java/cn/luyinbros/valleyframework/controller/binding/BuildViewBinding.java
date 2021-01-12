package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.ClassName;

import java.util.List;

import cn.luyinbros.valleyframework.controller.FullTypeName;

/**
 * if has returnClassName .  paramClassNames must has one buildContext;
 */
public class BuildViewBinding {
    private final String methodName;
    private final List<FullTypeName> paramClassNames;

    private final ClassName returnClassName;

    public BuildViewBinding(String methodName, List<FullTypeName> paramClassNames, ClassName returnClassName) {
        this.methodName = methodName;
        this.paramClassNames = paramClassNames;
        this.returnClassName = returnClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<FullTypeName> getParamClassNames() {
        return paramClassNames;
    }

    public boolean hasParam() {
        return paramClassNames != null && !paramClassNames.isEmpty();
    }

    public ClassName getReturnClassName() {
        return returnClassName;
    }



    @Override
    public String toString() {
        return "BuildViewBinding{" +
                "methodName='" + methodName + '\'' +
                ", paramClassNames=" + paramClassNames +
                ", returnClassName=" + returnClassName +
                '}';
    }
}
