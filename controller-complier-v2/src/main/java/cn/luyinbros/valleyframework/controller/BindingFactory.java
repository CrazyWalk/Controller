package cn.luyinbros.valleyframework.controller;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.binding.BindingResult;
import cn.luyinbros.valleyframework.controller.binding.ControllerBinding;
import cn.luyinbros.valleyframework.controller.binding.DisposeBinding;

import static javax.lang.model.element.Modifier.FINAL;

/**
 * 绑定工厂
 */
public class BindingFactory {

    public static BindingResult<ControllerBinding> createControllerBinding(TypeElement enclosingElement,
                                                                           ResId layoutRes) {
        final TypeMirror typeMirror = enclosingElement.asType();
        TypeName targetType = TypeName.get(typeMirror);
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }
        ControllerBinding binding = new ControllerBinding();
        binding.setGenerationClassName(getDelegateClassName(enclosingElement));
        binding.setControllerTypeName(targetType);
        binding.setFinal(enclosingElement.getModifiers().contains(FINAL));
        binding.setLayoutId(layoutRes);
        binding.setTypeElement(enclosingElement);
        if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_ACTIVITY)) {
            binding.setControllerType(ControllerType.ACTIVITY);
        } else if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_FRAGMENT)) {
            binding.setControllerType(ControllerType.FRAGMENT);
        }
        return BindingResult.createBindResult(binding);
    }

    private static ClassName getDelegateClassName(TypeElement typeElement) {
        String packageName = Utils.getPackageName(typeElement);
        String className = typeElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');
        return ClassName.get(packageName, className + "_ControllerDelegate");
    }

    /**
     * 创建dispose
     */
    public static BindingResult<DisposeBinding> createDisposeBinding(Element element) {
        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        final TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            return BindingResult.createErrorResult(element, "return must be void");
        }
        if (!executableElement.getParameters().isEmpty()) {
            return BindingResult.createErrorResult(element, "parameters must empty");
        }
        return BindingResult.createBindResult(new DisposeBinding(executableElement.getSimpleName().toString()));
    }

}
