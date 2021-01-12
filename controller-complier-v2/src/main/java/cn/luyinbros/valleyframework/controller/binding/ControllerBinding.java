package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;

import cn.luyinbros.valleyframework.controller.ControllerType;
import cn.luyinbros.valleyframework.controller.ResId;


public class ControllerBinding {
    private ResId layoutId;
    private ControllerType controllerType = ControllerType.OTHER;
    private boolean isFinal;
    private ClassName generationClassName;
    private TypeName controllerTypeName;
    private TypeElement typeElement;


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

    public ControllerType getControllerType() {
        return controllerType;
    }

    public void setControllerType(ControllerType controllerType) {
        this.controllerType = controllerType;
    }

    public ClassName getGenerationClassName() {
        return generationClassName;
    }

    public void setGenerationClassName(ClassName generationClassName) {
        this.generationClassName = generationClassName;
    }

    public TypeName getControllerTypeName() {
        return controllerTypeName;
    }

    public void setControllerTypeName(TypeName controllerTypeName) {
        this.controllerTypeName = controllerTypeName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    @Override
    public String toString() {
        return "ControllerBinding{" +
                "layoutId=" + layoutId +
                ", controllerType=" + controllerType +
                ", isFinal=" + isFinal +
                ", generationClassName=" + generationClassName +
                ", controllerTypeName=" + controllerTypeName +
                ", typeElement=" + typeElement +
                '}';
    }
}
