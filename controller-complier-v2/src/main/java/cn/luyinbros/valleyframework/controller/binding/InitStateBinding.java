package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.ClassName;


import javax.annotation.Nullable;


public class InitStateBinding {
    private final String methodName;
    private final ClassName paramClassName;

    public InitStateBinding(String methodName, ClassName paramClassName) {
        this.methodName = methodName;
        this.paramClassName = paramClassName;

    }

    public String getMethodName() {
        return methodName;
    }

    @Nullable
    public ClassName getParamClassName() {
        return paramClassName;
    }


//    public static InitStateBinding create(Element element) {
//        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
//        final TypeMirror returnTypeMirror = executableElement.getReturnType();
//        if (returnTypeMirror.getKind() != TypeKind.VOID) {
//            CompileMessager.error(element,"invalidate");
//            return null;
//        }
//
//        ClassName argumentClassName = null;
//        boolean hasError = true;
//        final List<? extends VariableElement> methodParameters = executableElement.getParameters();
//        int size = methodParameters.size();
//        if (size == 0) {
//            hasError = false;
//        } else if (size == 1) {
//            TypeMirror mirror = methodParameters.get(0).asType();
//            if (TypeHelper.isTypeEqual(mirror, Constants.TYPE_BUILD_CONTEXT)) {
//                argumentClassName= TypeNameHelper.get(mirror);
//                hasError = false;
//            }
//        }
//
//        if (hasError) {
//            CompileMessager.error(element,"invalidate");
//            return null;
//        }
//
//
//        final String methodName = executableElement.getSimpleName().toString();
//        return new InitStateBinding(methodName, argumentClassName);
//    }

    @Override
    public String toString() {
        return "InitStateBinding{" +
                "methodName='" + methodName + '\'' +
                ", paramClassName=" + paramClassName +
                '}';
    }


}
