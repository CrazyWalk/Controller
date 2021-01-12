package cn.luyinbros.valleyframework.controller.binding;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;


import cn.luyinbros.valleyframework.controller.ElementHelper;
import cn.luyinbros.valleyframework.controller.Utils;
import cn.luyinbros.valleyframework.controller.annotation.BundleValue;

public class BundleValueBinding {
    private String filedName;
    private String key;
    private TypeMirror typeMirror;
    private boolean isRequired;

    public String getFiledName() {
        return filedName;
    }

    public String getKey() {
        return key;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTypeMirror(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public static BundleValueBinding create(Element element) {
        VariableElement variableElement = ElementHelper.asVariable(element);
        BundleValueBinding binding = new BundleValueBinding();
        BundleValue bundleValue = variableElement.getAnnotation(BundleValue.class);
        binding.key = bundleValue.value();
        binding.filedName = variableElement.getSimpleName().toString();
        binding.typeMirror = variableElement.asType();
        binding.isRequired= Utils.isNotNullElement(element);
        return binding;
    }
}
