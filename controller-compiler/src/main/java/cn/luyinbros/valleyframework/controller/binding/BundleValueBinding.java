package cn.luyinbros.valleyframework.controller.binding;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.Utils;
import cn.luyinbros.valleyframework.controller.annotation.BundleValue;
import cn.luyinbros.compiler.ElementHelper;

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
