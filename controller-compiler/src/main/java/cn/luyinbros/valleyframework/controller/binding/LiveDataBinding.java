package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.compiler.ElementHelper;
import cn.luyinbros.compiler.TypeHelper;
import cn.luyinbros.valleyframework.controller.CompileMessager;
import cn.luyinbros.valleyframework.controller.FullTypeName;
import cn.luyinbros.valleyframework.controller.Utils;
import cn.luyinbros.valleyframework.controller.annotation.LiveOB;
import cn.luyinbros.valleyframework.controller.annotation.OnPermissionResult;

public class LiveDataBinding {
    private String methodName;
    private List<FullTypeName> params;
    private String resolve;

    public String getMethodName() {
        return methodName;
    }

    public List<FullTypeName> getParams() {
        return params;
    }

    public String getResolve() {
        return resolve;
    }

    public static LiveDataBinding create(Element element) {
//        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
//        final TypeMirror returnType = executableElement.getReturnType();
//        if (returnType.getKind() != TypeKind.VOID) {
//            CompileMessager.error(element, "invalidate");
//            return null;
//        }
//
//        LiveDataBinding binding = new LiveDataBinding();
//        binding.params = new ArrayList<>();
//        binding.methodName = executableElement.getSimpleName().toString();
//        LiveOB liveOB = executableElement.getAnnotation(LiveOB.class);
//        binding.resolve = liveOB.value();
//        boolean hasError = true;
//
//        final List<? extends VariableElement> parameters = executableElement.getParameters();
//        final int parametersSize = parameters.size();
//
//
//        if (parametersSize == 0) {
//            hasError = false;
//        } else if (parametersSize == 1) {
//            VariableElement variableElement = parameters.get(0);
//            TypeMirror typeMirror = variableElement.asType();
//            hasError = false;
//            binding.params.add(new FullTypeName(TypeName.get(typeMirror), typeMirror));
//        }
        return new LiveDataBinding();

    }

    @Override
    public String toString() {
        return "LiveDataBinding{" +
                "methodName='" + methodName + '\'' +
                ", params=" + params +
                ", resolve='" + resolve + '\'' +
                '}';
    }
}
