package cn.luyinbros.android.controller.binding;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import static javax.lang.model.element.Modifier.PUBLIC;

public class DisposeBindingProvider {
    private List<DisposeBinding> disposeBindings;

    public void addBinding(DisposeBinding binding) {
        if (disposeBindings == null) {
            disposeBindings = new ArrayList<>();
        }
        disposeBindings.add(binding);
    }

    public boolean isEmpty() {
        return disposeBindings == null || disposeBindings.isEmpty();
    }

    public void code(TypeSpec.Builder result,String disposeMethodName,CodeBlock... disposes) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("dispose")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(ClassName.VOID);
        methodBuilder.addStatement("super.dispose()");

        for (CodeBlock codeBlock : disposes) {
            methodBuilder.addCode(codeBlock);
        }

        if(!isEmpty()){
            for (DisposeBinding binding : disposeBindings) {
                String methodName = binding.getMethodName();
                methodBuilder.addStatement("target.$L()", methodName);
            }
        }
        result.addMethod(methodBuilder.build());
    }
}
