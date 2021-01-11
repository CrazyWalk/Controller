package cn.luyinbros.valleyframework.controller;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import cn.luyinbros.valleyframework.controller.binding.ControllerBinding;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

class ControllerDelegateInfo {
    private final ControllerBinding controllerBinding;

    private ControllerDelegateInfo(ControllerBinding controllerBinding) {
        this.controllerBinding = controllerBinding;
    }

    JavaFile brewJava() {
        final TypeSpec bindingConfiguration = createTypeSpec();
        return JavaFile.builder(controllerBinding.getGenerationClassName().packageName(), bindingConfiguration)
                .addFileComment("Generated code . Do not modify!")
                .build();
    }

    private TypeSpec createTypeSpec() {
        TypeSpec.Builder result = TypeSpec.classBuilder(controllerBinding.getGenerationClassName().simpleName())
                .addModifiers(PUBLIC)
                .addAnnotation(Constants.ANNOTATION_KEEP)
                .addOriginatingElement(controllerBinding.getTypeElement());

        return result.build();
    }


    @Override
    public String toString() {
        return "ControllerDelegateInfo{" +
                "controllerBinding=" + controllerBinding +
                '}';
    }

    static class Builder {
        private final ControllerBinding controllerBinding;

        public Builder(ControllerBinding controllerBinding) {
            this.controllerBinding = controllerBinding;
        }


        public ControllerDelegateInfo build() {
            return new ControllerDelegateInfo(controllerBinding);
        }
    }

}
