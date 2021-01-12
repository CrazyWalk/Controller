package cn.luyinbros.valleyframework.controller.binding;


public class DisposeBinding {
    private final String methodName;

    public DisposeBinding(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return "DisposeBinding{" +
                "methodName='" + methodName + '\'' +
                '}';
    }
}
