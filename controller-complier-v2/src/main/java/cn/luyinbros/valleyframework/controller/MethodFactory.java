package cn.luyinbros.valleyframework.controller;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.binding.BundleValueBinding;
import cn.luyinbros.valleyframework.controller.provider.BundleValueBindingProvider;
import cn.luyinbros.valleyframework.controller.provider.LifecycleBindingProvider;

public class MethodFactory {
    public static final String METHOD_NAME_INIT_STATE = "initState";
    public static final String METHOD_NAME_INJECT_BUNDLE_VALUE = "injectBundleValue";
    public static final String METHOD_NAME_INIT_LIFECYCLE_FILED = "initLifecycleField";
    public static final String METHOD_NAME_INJECT_VIEW = "injectView";
    public static final String METHOD_NAME_INJECT_LISTENER = "injectListener";
    public static final String METHOD_NAME_UNINJECT_VIEW = "uninjectView";
    public static final String METHOD_NAME_UNINJECT_LISTENER = "uninjectListener";


    public static MethodSpec createInjectBundleValue(ControllerType controllerType,
                                                     BundleValueBindingProvider provider) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_NAME_INJECT_BUNDLE_VALUE)
                .addModifiers(Modifier.PRIVATE)
                .returns(ClassName.VOID)
                .addJavadoc("inject bundle value");

        if (controllerType == ControllerType.ACTIVITY) {
            builder.addParameter(Constants.CLASS_INTENT, "data");
        } else {
            builder.addParameter(Constants.CLASS_BUNDLE, "data");
        }
        builder.beginControlFlow("if(data==null)");


        for (BundleValueBinding bundleValueBinding : provider.getBundleValueBindings()) {
            TypeMirror mirror = bundleValueBinding.getTypeMirror();
            TypeKind typeKind = mirror.getKind();
            if (!typeKind.isPrimitive()) {
                if (bundleValueBinding.isRequired()) {
                    builder.addStatement("$L.requireNonNull(target.$L)", Constants.CLASS_OBJECTS, bundleValueBinding.getFiledName());
                }
            }
        }
        builder.nextControlFlow("else");
        for (BundleValueBinding bundleValueBinding : provider.getBundleValueBindings()) {
            TypeMirror mirror = bundleValueBinding.getTypeMirror();
            TypeKind typeKind = mirror.getKind();
            if (typeKind.isPrimitive()) {
                builder.addStatement("target.$L=data.$L($S,target.$L)",
                        bundleValueBinding.getFiledName(),
                        BundleValueBindingProvider.getInvokeMethodName(controllerType, mirror),
                        bundleValueBinding.getKey(),
                        bundleValueBinding.getFiledName()
                );
            } else if (typeKind == TypeKind.ARRAY || typeKind == TypeKind.DECLARED) {
                String invokeMethodName = BundleValueBindingProvider.getInvokeMethodName(controllerType, mirror);
                if (invokeMethodName != null && !invokeMethodName.isEmpty()) {
                    builder.addStatement("final $L $L=($L)data.$L($S)",
                            ClassName.get(bundleValueBinding.getTypeMirror()),
                            bundleValueBinding.getFiledName(),
                            ClassName.get(bundleValueBinding.getTypeMirror()),
                            invokeMethodName,
                            bundleValueBinding.getKey());
                    if (bundleValueBinding.isRequired()) {
                        builder.beginControlFlow("if($L==null)",
                                bundleValueBinding.getFiledName());
                        builder.addStatement("$L.requireNonNull(target.$L)",
                                Constants.CLASS_OBJECTS,
                                bundleValueBinding.getFiledName());
                        builder.nextControlFlow("else");
                        builder.addStatement("target.$L=$L.requireNonNull($L)",
                                bundleValueBinding.getFiledName(),
                                Constants.CLASS_OBJECTS,
                                bundleValueBinding.getFiledName());
                        builder.endControlFlow();
                    } else {
                        builder.addStatement("target.$L=$L",
                                bundleValueBinding.getFiledName(),
                                bundleValueBinding.getFiledName());
                    }
                }

            }
        }

        builder.endControlFlow();

        return builder.build();
    }

    public static MethodSpec createInitLifecycleField(LifecycleBindingProvider provider) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_NAME_INIT_LIFECYCLE_FILED)
                .addModifiers(Modifier.PRIVATE)
                .returns(ClassName.VOID)
                .addJavadoc("init lifecycle field");
        builder.addCode(provider.initFields());
        return builder.build();
    }

}
