package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import cn.luyinbros.valleyframework.controller.Constants;
import cn.luyinbros.valleyframework.controller.ControllerDelegateInfo;

import static javax.lang.model.element.Modifier.PROTECTED;

public class InitStateBindingProvider {
    private List<InitStateBinding> initStateBindings = new ArrayList<>();
    private BundleValueBindingProvider bundleValueBindingProvider = new BundleValueBindingProvider();

    public void addBinding(InitStateBinding binding) {
        initStateBindings.add(binding);
    }

    public void addBinding(BundleValueBinding binding) {
        bundleValueBindingProvider.addBinding(binding);
    }


    public boolean isEmpty() {
        return initStateBindings.isEmpty() && bundleValueBindingProvider.isEmpty();
    }

    public void code(TypeSpec.Builder result, ControllerDelegateInfo info, CodeBlock... otherBlock) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("initState")
                .addAnnotation(Override.class)
                .addModifiers(PROTECTED)
                .addParameter(Constants.INTERFACE_BUILD_CONTEXT, "buildContext")
                .returns(ClassName.VOID);
        methodBuilder.addStatement("super.initState(buildContext)");

        for (CodeBlock codeBlock : otherBlock) {
            methodBuilder.addCode(codeBlock);
        }
        methodBuilder.addCode(bundleValueBindingProvider.code(info));

        for (InitStateBinding initStateBinding : initStateBindings) {
            String methodName = initStateBinding.getMethodName();
            ClassName argClassName = initStateBinding.getParamClassName();
            if (argClassName != null) {
                methodBuilder.addStatement("target.$L($L)", methodName, "buildContext");
            } else {
                methodBuilder.addStatement("target.$L()", methodName);
            }
        }

        result.addMethod(methodBuilder.build());
    }
}
