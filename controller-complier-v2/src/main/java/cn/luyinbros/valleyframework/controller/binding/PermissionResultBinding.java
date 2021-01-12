package cn.luyinbros.valleyframework.controller.binding;


import java.util.List;

import cn.luyinbros.valleyframework.controller.FullTypeName;

public class PermissionResultBinding {
    private String methodName;
    private String[] permissions;
    private int requestCode;
    private List<FullTypeName> params;
    private boolean after;
    public static final int ALLOW_EXCLUDE_MIN_REQUEST_CODE = 1;

    public String getMethodName() {
        return methodName;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public List<FullTypeName> getParams() {
        return params;
    }

    public boolean isAfter() {
        return after;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setParams(List<FullTypeName> params) {
        this.params = params;
    }

    public void setAfter(boolean after) {
        this.after = after;
    }


}
