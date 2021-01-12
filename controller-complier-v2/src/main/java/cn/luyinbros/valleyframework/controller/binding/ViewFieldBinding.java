package cn.luyinbros.valleyframework.controller.binding;

import javax.lang.model.element.Element;

import cn.luyinbros.valleyframework.controller.ResId;


public class ViewFieldBinding {
    private ResId resId;
    private String fieldName;
    private boolean required;
    private Element element;

    public ResId getResId() {
        return resId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isRequired() {
        return required;
    }

    public Element getElement() {
        return element;
    }


    public void setResId(ResId resId) {
        this.resId = resId;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "ViewFieldBinding{" +
                "resId=" + resId +
                ", fieldName='" + fieldName + '\'' +
                ", required=" + required +
                '}';
    }
}
