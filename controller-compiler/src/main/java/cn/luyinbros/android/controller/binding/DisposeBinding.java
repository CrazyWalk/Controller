package cn.luyinbros.android.controller.binding;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.android.controller.CompileMessager;
import cn.luyinbros.compiler.ElementHelper;

public class DisposeBinding {
    private final String methodName;

    public DisposeBinding(String methodName) {
        this.methodName = methodName;

    }

    public String getMethodName() {
        return methodName;
    }


    public static DisposeBinding create(Element element) {
        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            CompileMessager.error(element,"invalidate");
            return null;
        }
        if (!executableElement.getParameters().isEmpty()) {
            CompileMessager.error(element,"invalidate");
            return null;
        }

        return new DisposeBinding(executableElement.getSimpleName().toString());
    }


}
