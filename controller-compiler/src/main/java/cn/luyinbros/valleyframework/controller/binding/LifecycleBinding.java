package cn.luyinbros.valleyframework.controller.binding;

import androidx.lifecycle.Lifecycle;

import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.CompileMessager;
import cn.luyinbros.valleyframework.controller.Constants;
import cn.luyinbros.valleyframework.controller.annotation.DidChangeLifecycleEvent;
import cn.luyinbros.compiler.ElementHelper;
import cn.luyinbros.compiler.TypeHelper;
import cn.luyinbros.compiler.TypeNameHelper;


public class LifecycleBinding {
    private final String methodName;
    private final ClassName argumentClassName;
    private final Lifecycle.Event event;
    private final int count;

    public LifecycleBinding(String methodName,
                            ClassName argumentClassName,
                            Lifecycle.Event state,
                            int count) {
        this.methodName = methodName;
        this.argumentClassName = argumentClassName;
        this.event = state;
        this.count = count;

    }

    public String getMethodName() {
        return methodName;
    }

    public ClassName getArgumentClassName() {
        return argumentClassName;
    }

    public Lifecycle.Event getState() {
        return event;
    }

    public int getCount() {
        return count;
    }


    public static LifecycleBinding create(Element element) {
        ExecutableElement executableElement = ElementHelper.asExecutable(element);
        TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            CompileMessager.error(element,"invalidate");
            return null;
        }

        ClassName argumentClassName = null;
        boolean hasError = true;

        final List<? extends VariableElement> methodParameters = executableElement.getParameters();
        int size = methodParameters.size();
        if (size == 0) {
            hasError = false;
        } else if (size == 1) {
            hasError = false;
            TypeMirror mirror = methodParameters.get(0).asType();
            if (TypeHelper.isTypeEqual(mirror, Constants.TYPE_LIFECYCLE_EVENT)) {
                argumentClassName = TypeNameHelper.get(mirror);
            }
        }

        if (hasError) {
            CompileMessager.error(element,"invalidate");
            return null;
        }

        DidChangeLifecycleEvent state = executableElement.getAnnotation(DidChangeLifecycleEvent.class);
        final String methodName = executableElement.getSimpleName().toString();
        return new LifecycleBinding(methodName,
                argumentClassName,
                state.value(),
                state.count());
    }

    @Override
    public String toString() {
        return "LifecycleBinding{" +
                "methodName='" + methodName + '\'' +
                ", argumentClassName=" + argumentClassName +
                ", event=" + event +
                ", count=" + count +
                '}';
    }
}
