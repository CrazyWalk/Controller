package cn.luyinbros.android.controller;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.android.controller.binding.ActivityResultBinding;
import cn.luyinbros.android.controller.binding.ActivityResultBindingProvider;
import cn.luyinbros.android.controller.binding.BindViewProvider;
import cn.luyinbros.android.controller.binding.BuildViewBinding;
import cn.luyinbros.android.controller.binding.BundleValueBinding;
import cn.luyinbros.android.controller.binding.DisposeBinding;
import cn.luyinbros.android.controller.binding.DisposeBindingProvider;
import cn.luyinbros.android.controller.binding.InitStateBinding;
import cn.luyinbros.android.controller.binding.InitStateBindingProvider;
import cn.luyinbros.android.controller.binding.LifecycleBinding;
import cn.luyinbros.android.controller.binding.LifecycleBindingProvider;
import cn.luyinbros.android.controller.binding.PermissionResultBinding;
import cn.luyinbros.android.controller.binding.PermissionResultBindingProvider;
import cn.luyinbros.android.controller.binding.ViewFieldBinding;
import cn.luyinbros.android.controller.listener.ListenerBindingProvider;
import cn.luyinbros.compiler.TypeHelper;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

public class ControllerDelegateSet {
    private final ClassName targetClassName;
    private final ControllerDelegateInfo controllerDelegateInfo;
    private final TypeName targetTypeName;
    private final ClassName delegateClassName;
    private final TypeElement enclosingElement;
    private final InitStateBindingProvider initStateBindingProvider;
    private final BindViewProvider bindViewProvider;
    private final LifecycleBindingProvider lifecycleBindingProvider;
    private final DisposeBindingProvider disposeBindingProvider;
    private final ActivityResultBindingProvider activityResultBindingProvider;
    private final PermissionResultBindingProvider permissionResultBindingProvider;

    public ControllerDelegateSet(Builder builder) {
        this.targetClassName = builder.targetClassName;
        this.controllerDelegateInfo = builder.controllerDelegateInfo;
        this.targetTypeName = builder.targetTypeName;
        this.delegateClassName = builder.delegateClassName;
        this.enclosingElement = builder.enclosingElement;
        this.initStateBindingProvider = builder.initStateBindingProvider;
        this.bindViewProvider = builder.bindViewProvider;
        this.lifecycleBindingProvider = builder.lifecycleBindingProvider;
        this.disposeBindingProvider = builder.disposeBindingProvider;
        this.activityResultBindingProvider = builder.activityResultBindingProvider;
        this.permissionResultBindingProvider = builder.permissionResultBindingProvider;

    }

    public ClassName getTargetClassName() {
        return targetClassName;
    }

    public ClassName getDelegateClassName() {
        return delegateClassName;
    }

    JavaFile brewJava() {
        final TypeSpec bindingConfiguration = createTypeSpec();
        return JavaFile.builder(delegateClassName.packageName(), bindingConfiguration)
                .addFileComment("Generated code . Do not modify!")
                .build();
    }

    private TypeSpec createTypeSpec() {
        TypeSpec.Builder result = TypeSpec.classBuilder(delegateClassName.simpleName())
                .addModifiers(PUBLIC)
                .addAnnotation(Constants.ANNOTATION_KEEP)
                .addOriginatingElement(enclosingElement);

        if (controllerDelegateInfo.isFinal()) {
            result.addModifiers(FINAL);
        }

        switch (controllerDelegateInfo.getType()) {
            case ACTIVITY:
                result.superclass(Constants.CLASS_DELEGATE_ACTIVITY);
                break;
            case FRAGMENT:
                result.superclass(Constants.CLASS_DELEGATE_FRAGMENT);
                break;
            default:
                result.superclass(Constants.CLASS_DELEGATE_COMMON);
                break;
        }

        //field
        {
            result.addField(targetTypeName, "target", PRIVATE);
        }

        //createConstructor
        result.addMethod(MethodSpec.constructorBuilder()
                .addAnnotation(Constants.ANNOTATION_UI_THREAD)
                .addModifiers(PUBLIC)
                .addParameter(targetTypeName, "target")
                .addStatement("super(target)")
                .addStatement("this.target=target").build());

        initStateBindingProvider.code(result, controllerDelegateInfo);
        bindViewProvider.code(result);
        lifecycleBindingProvider.code(result);
        disposeBindingProvider.code(result,
                controllerDelegateInfo.getType() != ControllerDelegateInfo.Type.OTHER ? "dispose" : "internalDispose",
                bindViewProvider.dispose());
        activityResultBindingProvider.code(result);
        permissionResultBindingProvider.code(result);
        return result.build();
    }


    static Builder newBuilder(TypeElement enclosingElement, ResId layoutRes, ControllerProcessor processor) {
        final TypeMirror typeMirror = enclosingElement.asType();
        TypeName targetType = TypeName.get(typeMirror);
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }
        ClassName delegateClassName = getDelegateClassName(enclosingElement);
        ControllerDelegateInfo controllerDelegateInfo = new ControllerDelegateInfo();
        controllerDelegateInfo.setFinal(enclosingElement.getModifiers().contains(FINAL));
        controllerDelegateInfo.setLayoutId(layoutRes);
        if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_ACTIVITY)) {
            controllerDelegateInfo.setType(ControllerDelegateInfo.Type.ACTIVITY);
        } else if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_FRAGMENT)) {
            controllerDelegateInfo.setType(ControllerDelegateInfo.Type.FRAGMENT);
        }
        return new Builder(
                ClassName.get(enclosingElement),
                controllerDelegateInfo,
                targetType,
                delegateClassName,
                enclosingElement,
                ListenerBindingProvider.of(enclosingElement, processor));

    }

    static ClassName getDelegateClassName(TypeElement typeElement) {
        String packageName = Utils.getPackageName(typeElement);
        String className = typeElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');
        return ClassName.get(packageName, className+"_ControllerDelegate");

//        String generationClassName;
//        int hashCode = className.hashCode();
//        if (hashCode < 0) {
//            generationClassName = "c" + "n" + Math.abs(hashCode);
//        } else {
//            generationClassName = "c" + hashCode;
//        }
//        return ClassName.get(packageName,generationClassName);


    }

    final static class Builder {
        private final ClassName targetClassName;
        private final ControllerDelegateInfo controllerDelegateInfo;
        private final TypeName targetTypeName;
        private final ClassName delegateClassName;
        private final TypeElement enclosingElement;
        private final InitStateBindingProvider initStateBindingProvider = new InitStateBindingProvider();
        private final BindViewProvider bindViewProvider;
        private final LifecycleBindingProvider lifecycleBindingProvider = new LifecycleBindingProvider();
        private final DisposeBindingProvider disposeBindingProvider = new DisposeBindingProvider();
        private final ActivityResultBindingProvider activityResultBindingProvider = new ActivityResultBindingProvider();
        private final PermissionResultBindingProvider permissionResultBindingProvider = new PermissionResultBindingProvider();

        Builder(ClassName targetClassName,
                ControllerDelegateInfo controllerDelegateInfo,
                TypeName targetTypeName,
                ClassName delegateClassName,
                TypeElement enclosingElement,
                ListenerBindingProvider listenerBindingProvider) {
            this.targetClassName = targetClassName;
            this.controllerDelegateInfo = controllerDelegateInfo;
            this.targetTypeName = targetTypeName;
            this.delegateClassName = delegateClassName;
            this.enclosingElement = enclosingElement;
            bindViewProvider = new BindViewProvider(controllerDelegateInfo);
            bindViewProvider.setListenerBindingProvider(listenerBindingProvider);
        }


        public void addBinding(InitStateBinding binding) {
            if (checkMethodNotExist(binding.getMethodName())) {
                initStateBindingProvider.addBinding(binding);
            }
        }

        public void addBinding(BundleValueBinding binding) {
            initStateBindingProvider.addBinding(binding);
        }

        public void addBinding(BuildViewBinding binding) {
            if (checkMethodNotExist(binding.getMethodName())) {
                bindViewProvider.addBinding(binding);
            }
        }


        public void addBinding(LifecycleBinding binding) {
            if (checkMethodNotExist(binding.getMethodName())) {
                lifecycleBindingProvider.addBinding(binding);
            }
        }

        public void addBinding(DisposeBinding binding) {
            if (checkMethodNotExist(binding.getMethodName())) {
                disposeBindingProvider.addBinding(binding);
            }
        }

        public void addBinding(ActivityResultBinding binding) {
            if (checkMethodNotExist(binding.getMethodName())) {
                activityResultBindingProvider.addBinding(binding);
            }
        }

        public void addBinding(PermissionResultBinding binding) {
            if (checkMethodNotExist(binding.getMethodName())) {
                permissionResultBindingProvider.addBinding(binding);
            }
        }

        public void addBinding(ViewFieldBinding binding) {
            bindViewProvider.addBinding(binding);
        }

        public ControllerDelegateSet build() {
            return new ControllerDelegateSet(this);
        }


        private boolean checkMethodNotExist(String name) {
            return true;
        }
    }


}
