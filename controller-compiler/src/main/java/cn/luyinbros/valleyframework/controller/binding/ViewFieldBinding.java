package cn.luyinbros.valleyframework.controller.binding;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import cn.luyinbros.valleyframework.controller.ResId;
import cn.luyinbros.valleyframework.controller.Utils;
import cn.luyinbros.compiler.ElementHelper;

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

    public static ViewFieldBinding create(Element element, ResId resId) {

        VariableElement variableElement = ElementHelper.asVariable(element);
        ViewFieldBinding binding = new ViewFieldBinding();
        binding.resId = resId;
        binding.fieldName = variableElement.getSimpleName().toString();
        binding.required = Utils.isNotNullElement(element);
        binding.element=element;
        return binding;
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
