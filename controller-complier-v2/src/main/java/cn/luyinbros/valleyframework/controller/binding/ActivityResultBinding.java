package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.TypeName;


import java.util.List;


public class ActivityResultBinding {
    private int requestCode;
    private int resultCode;
    private boolean requiredNonNullIntent;
    private List<TypeName> params;
    private String methodName;
    private boolean after;
    public static final int ALLOW_EXCLUDE_MIN_REQUEST_CODE = 1;
    public static final int ALLOW_EXCLUDE_MIN_RESULT_CODE = -1;


    public int getRequestCode() {
        return requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public boolean isRequiredNonNullIntent() {
        return requiredNonNullIntent;
    }

    public List<TypeName> getParams() {
        return params;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isAfter() {
        return after;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setRequiredNonNullIntent(boolean requiredNonNullIntent) {
        this.requiredNonNullIntent = requiredNonNullIntent;
    }

    public void setParams(List<TypeName> params) {
        this.params = params;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setAfter(boolean after) {
        this.after = after;
    }

    @Override
    public String toString() {
        return "ActivityResultBinding{" +
                "requestCode=" + requestCode +
                ", resultCode=" + resultCode +
                ", requiredNonNullIntent=" + requiredNonNullIntent +
                ", params=" + params +
                ", methodName='" + methodName + '\'' +
                ", after=" + after +
                '}';
    }
}
