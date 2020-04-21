package cn.luyinbros.android.controller;

public class ControllerDelegateInfo {
    public enum Type {
        ACTIVITY,
        FRAGMENT,
        OTHER
    }

    private ResId layoutId;
    private Type mType = Type.OTHER;
    private boolean isFinal;

    // 用jtree改为resId
    public ResId getLayoutId() {
        return layoutId;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setLayoutId(ResId layoutId) {
        this.layoutId = layoutId;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        this.mType = type;
    }


    @Override
    public String toString() {
        return "ControllerDelegateInfo{" +
                "layoutId=" + layoutId +
                ", mType=" + mType +
                ", isFinal=" + isFinal +
                '}';
    }
}
