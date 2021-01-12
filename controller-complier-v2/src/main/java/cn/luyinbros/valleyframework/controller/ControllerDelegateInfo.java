package cn.luyinbros.valleyframework.controller;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.luyinbros.valleyframework.controller.binding.ActivityResultBinding;
import cn.luyinbros.valleyframework.controller.binding.BuildViewBinding;
import cn.luyinbros.valleyframework.controller.binding.BundleValueBinding;
import cn.luyinbros.valleyframework.controller.binding.ControllerBinding;
import cn.luyinbros.valleyframework.controller.binding.DisposeBinding;
import cn.luyinbros.valleyframework.controller.binding.InitStateBinding;
import cn.luyinbros.valleyframework.controller.binding.LifecycleBinding;
import cn.luyinbros.valleyframework.controller.binding.PermissionResultBinding;
import cn.luyinbros.valleyframework.controller.binding.ViewFieldBinding;
import cn.luyinbros.valleyframework.controller.provider.ActivityResultBindingProvider;
import cn.luyinbros.valleyframework.controller.provider.BindViewProvider;
import cn.luyinbros.valleyframework.controller.provider.BundleValueBindingProvider;
import cn.luyinbros.valleyframework.controller.provider.DisposeBindingProvider;
import cn.luyinbros.valleyframework.controller.provider.InitStateBindingProvider;
import cn.luyinbros.valleyframework.controller.provider.LifecycleBindingProvider;
import cn.luyinbros.valleyframework.controller.provider.ListenerBindingProvider;
import cn.luyinbros.valleyframework.controller.provider.PermissionResultBindingProvider;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;

class ControllerDelegateInfo {
    private final ControllerBinding controllerBinding;
    private ControllerDelegateInfo mParent;
    private final BundleValueBindingProvider bundleValueBindingProvider;
    private final InitStateBindingProvider initStateBindingProvider;
    private final LifecycleBindingProvider lifecycleBindingProvider;
    private final ActivityResultBindingProvider activityResultBindingProvider;
    private final PermissionResultBindingProvider permissionResultBindingProvider;
    private final BindViewProvider bindViewProvider;
    private final DisposeBindingProvider disposeBindingProvider;

    private ControllerDelegateInfo(ControllerBinding controllerBinding,
                                   BundleValueBindingProvider bundleValueBindingProvider,
                                   InitStateBindingProvider initStateBindingProvider,
                                   LifecycleBindingProvider lifecycleBindingProvider,
                                   ActivityResultBindingProvider activityResultBindingProvider,
                                   PermissionResultBindingProvider permissionResultBindingProvider,
                                   BindViewProvider bindViewProvider,
                                   DisposeBindingProvider disposeBindingProvider) {
        this.controllerBinding = controllerBinding;
        this.bundleValueBindingProvider = bundleValueBindingProvider;
        this.initStateBindingProvider = initStateBindingProvider;
        this.lifecycleBindingProvider = lifecycleBindingProvider;
        this.activityResultBindingProvider = activityResultBindingProvider;
        this.permissionResultBindingProvider = permissionResultBindingProvider;
        this.bindViewProvider = bindViewProvider;
        this.disposeBindingProvider = disposeBindingProvider;
    }

    public void setParent(ControllerDelegateInfo mParent) {
        this.mParent = mParent;
    }

    public boolean isBuildNewView() {
        if (mParent != null) {
            return mParent.isBuildNewView() || bindViewProvider.isBuildNewView();
        }

        return false;
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

        if (mParent != null) {
            result.superclass(mParent.controllerBinding.getGenerationClassName());
        } else {
            switch (controllerBinding.getControllerType()) {
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
        }

        //field
        {
            result.addField(controllerBinding.getControllerTypeName(), "target", PRIVATE);
        }

        //createConstructor
        result.addMethod(MethodSpec.constructorBuilder()
                .addAnnotation(Constants.ANNOTATION_UI_THREAD)
                .addModifiers(PUBLIC)
                .addParameter(controllerBinding.getControllerTypeName(), "target")
                .addStatement("super(target)")
                .addStatement("this.target=target").build());

        final List<MethodSpec> generationMethodList = new ArrayList<>(buildInitState(result));
        buildSetIntent(result);
        generationMethodList.addAll(buildLifecycle(result));
        generationMethodList.addAll(buildView(result));
        buildActivityResult(result);
        buildPermissionResult(result);
        generationMethodList.addAll(buildDispose(result));
        //generation method
        for (MethodSpec methodSpec : generationMethodList) {
            result.addMethod(methodSpec);
        }

        return result.build();
    }

    private List<MethodSpec> buildInitState(TypeSpec.Builder result) {
        int needInitCount = 0;
        needInitCount += bundleValueBindingProvider.isEmpty() ? 0 : 1;
        needInitCount += initStateBindingProvider.isEmpty() ? 0 : 1;
        needInitCount += lifecycleBindingProvider.hasField() ? 1 : 0;
        if (needInitCount == 0) {
            return Collections.emptyList();
        }

        final ControllerType controllerType = controllerBinding.getControllerType();
        final List<MethodSpec> generationMethodList = new ArrayList<>();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(MethodFactory.METHOD_NAME_INIT_STATE)
                .addAnnotation(Override.class)
                .addModifiers(PROTECTED)
                .addParameter(Constants.INTERFACE_BUILD_CONTEXT, "buildContext")
                .returns(ClassName.VOID);
        methodBuilder.addStatement("super.initState(buildContext)");

        //init lifecycle filed
        if (lifecycleBindingProvider.hasField()) {
            methodBuilder.addStatement("$L()", MethodFactory.METHOD_NAME_INIT_LIFECYCLE_FILED);
        }

        //inject bundle
        MethodSpec injectBundleValueMethod = null;
        if (!bundleValueBindingProvider.isEmpty()) {
            injectBundleValueMethod = MethodFactory.createInjectBundleValue(controllerBinding.getControllerType(),
                    bundleValueBindingProvider);
            generationMethodList.add(injectBundleValueMethod);
        }
        if (injectBundleValueMethod != null) {
            if (controllerType == ControllerType.ACTIVITY) {
                methodBuilder.addStatement("$L(target.$L())", MethodFactory.METHOD_NAME_INJECT_BUNDLE_VALUE, "getIntent");
            } else {
                methodBuilder.addStatement("$L(target.$L())", MethodFactory.METHOD_NAME_INJECT_BUNDLE_VALUE, "getArguments");
            }
        }

        //initStateMethod
        if (!initStateBindingProvider.isEmpty()) {
            for (InitStateBinding initStateBinding : initStateBindingProvider.getInitStateBindings()) {
                String methodName = initStateBinding.getMethodName();
                ClassName argClassName = initStateBinding.getParamClassName();
                if (argClassName != null) {
                    methodBuilder.addStatement("target.$L($L)", methodName, "buildContext");
                } else {
                    methodBuilder.addStatement("target.$L()", methodName);
                }
            }
        }

        result.addMethod(methodBuilder.build());
        return generationMethodList;
    }

    private List<MethodSpec> buildLifecycle(TypeSpec.Builder result) {
        if (lifecycleBindingProvider.isEmpty()) {
            return Collections.emptyList();
        }
        lifecycleBindingProvider.code(result);
        if (lifecycleBindingProvider.hasField()) {
            List<MethodSpec> methodSpecs = new ArrayList<>();
            methodSpecs.add(MethodFactory.createInitLifecycleField(lifecycleBindingProvider));
            return methodSpecs;
        }
        return Collections.emptyList();
    }

    private List<MethodSpec> buildView(TypeSpec.Builder result) {
        return bindViewProvider.code(controllerBinding.getTypeElement(), result, isBuildNewView());
    }

    private void buildActivityResult(TypeSpec.Builder result) {
        if (activityResultBindingProvider.isEmpty()) {
            return;
        }
        activityResultBindingProvider.code(result);
    }

    private void buildPermissionResult(TypeSpec.Builder result) {
        if (permissionResultBindingProvider.isEmpty()) {
            return;
        }
        permissionResultBindingProvider.code(result);
    }


    private void buildSetIntent(TypeSpec.Builder result) {
        if (bundleValueBindingProvider.isEmpty()) {
            return;
        }
        if (controllerBinding.getControllerType() == ControllerType.ACTIVITY) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("setIntent")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(Constants.CLASS_INTENT, "data")
                    .returns(ClassName.VOID);

            methodBuilder.addStatement("super.setIntent(data)");
            methodBuilder.addStatement("$L(data)", MethodFactory.METHOD_NAME_INJECT_BUNDLE_VALUE);
            result.addMethod(methodBuilder.build());
        }
    }


    private List<MethodSpec> buildDispose(TypeSpec.Builder result) {
        int needInitCount = 0;
        needInitCount += disposeBindingProvider.isEmpty() ? 0 : 1;
        needInitCount += bindViewProvider.isFieldEmpty() ? 0 : 1;
        needInitCount += bindViewProvider.isListenerEmpty() ? 0 : 1;
        if (needInitCount == 0) {
            return Collections.emptyList();
        }
        List<MethodSpec> specList = new ArrayList<>();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("dispose")
                .addAnnotation(Override.class)
                .addModifiers(PROTECTED)
                .returns(ClassName.VOID);
        methodBuilder.addStatement("super.dispose()");

        if (!disposeBindingProvider.isEmpty()) {
            for (DisposeBinding binding : disposeBindingProvider.getDisposeBindings()) {
                String methodName = binding.getMethodName();
                methodBuilder.addStatement("target.$L()", methodName);
            }
        }
        if (!bindViewProvider.isListenerEmpty()) {
            methodBuilder.addStatement("$L()", MethodFactory.METHOD_NAME_UNINJECT_LISTENER);
            specList.add(bindViewProvider.createUninjectListener());
        }

        if (!bindViewProvider.isFieldEmpty()) {
            methodBuilder.addStatement("$L()", MethodFactory.METHOD_NAME_UNINJECT_VIEW);
            specList.add(bindViewProvider.createUninjectView());
        }

        result.addMethod(methodBuilder.build());
        return specList;
    }


    @Override
    public String toString() {
        return "ControllerDelegateInfo{" +
                "controllerBinding=" + controllerBinding +
                ", mParent=" + mParent +
                ", bundleValueBindingProvider=" + bundleValueBindingProvider +
                ", initStateBindingProvider=" + initStateBindingProvider +
                ", disposeBindingProvider=" + disposeBindingProvider +
                '}';
    }

    static class Builder {
        private final ControllerBinding controllerBinding;
        private final BundleValueBindingProvider bundleValueBindingProvider = new BundleValueBindingProvider();
        private final InitStateBindingProvider initStateBindingProvider = new InitStateBindingProvider();
        private final LifecycleBindingProvider lifecycleBindingProvider = new LifecycleBindingProvider();
        private final ActivityResultBindingProvider activityResultBindingProvider = new ActivityResultBindingProvider();
        private final PermissionResultBindingProvider permissionResultBindingProvider = new PermissionResultBindingProvider();
        private final DisposeBindingProvider disposeBindingProvider = new DisposeBindingProvider();
        private final BindViewProvider bindViewProvider;

        Builder(ControllerBinding controllerBinding, CompilerMessager messager) {
            this.controllerBinding = controllerBinding;
            bindViewProvider = new BindViewProvider(controllerBinding, messager);
        }

        void addBinding(BundleValueBinding binding) {
            bundleValueBindingProvider.addBinding(binding);
        }


        void addBinding(InitStateBinding binding) {
            initStateBindingProvider.addBinding(binding);
        }

        void addBinding(LifecycleBinding binding) {
            lifecycleBindingProvider.addBinding(binding);
        }

        void addBinding(ActivityResultBinding binding) {
            activityResultBindingProvider.addBinding(binding);
        }

        public void addBinding(PermissionResultBinding binding) {
            permissionResultBindingProvider.addBinding(binding);
        }

        void addBinding(DisposeBinding binding) {
            disposeBindingProvider.addBinding(binding);
        }

        void addBinding(ViewFieldBinding binding) {
            bindViewProvider.addBinding(binding);
        }

        void addBinding(BuildViewBinding binding) {
            bindViewProvider.addBinding(binding);
        }

        public void setListenerBindingProvider(ListenerBindingProvider listenerBindingProvider) {
            bindViewProvider.setListenerBindingProvider(listenerBindingProvider);
        }

        ControllerDelegateInfo build() {
            return new ControllerDelegateInfo(controllerBinding,
                    bundleValueBindingProvider,
                    initStateBindingProvider,
                    lifecycleBindingProvider,
                    activityResultBindingProvider,
                    permissionResultBindingProvider,
                    bindViewProvider,
                    disposeBindingProvider);
        }


    }

}
