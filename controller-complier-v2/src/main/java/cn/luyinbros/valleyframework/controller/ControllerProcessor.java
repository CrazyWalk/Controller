package cn.luyinbros.valleyframework.controller;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.sun.source.util.Trees;


import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import cn.luyinbros.valleyframework.controller.annotation.BindView;
import cn.luyinbros.valleyframework.controller.annotation.BuildView;
import cn.luyinbros.valleyframework.controller.annotation.BundleValue;
import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.valleyframework.controller.annotation.DidChangeLifecycleEvent;
import cn.luyinbros.valleyframework.controller.annotation.Dispose;
import cn.luyinbros.valleyframework.controller.annotation.InitState;
import cn.luyinbros.valleyframework.controller.annotation.LiveOB;
import cn.luyinbros.valleyframework.controller.annotation.OnActivityResult;
import cn.luyinbros.valleyframework.controller.annotation.OnPermissionResult;
import cn.luyinbros.valleyframework.controller.binding.ActivityResultBinding;
import cn.luyinbros.valleyframework.controller.binding.BindingResult;
import cn.luyinbros.valleyframework.controller.binding.BuildViewBinding;
import cn.luyinbros.valleyframework.controller.binding.BundleValueBinding;
import cn.luyinbros.valleyframework.controller.binding.ControllerBinding;
import cn.luyinbros.valleyframework.controller.binding.DisposeBinding;
import cn.luyinbros.valleyframework.controller.binding.InitStateBinding;
import cn.luyinbros.valleyframework.controller.binding.LifecycleBinding;
import cn.luyinbros.valleyframework.controller.binding.PermissionResultBinding;
import cn.luyinbros.valleyframework.controller.binding.ViewFieldBinding;
import cn.luyinbros.valleyframework.controller.provider.ListenerBindingProvider;

import static cn.luyinbros.valleyframework.controller.ElementHelper.getSuperClass;
import static java.lang.Enum.valueOf;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
@AutoService(Processor.class)
public class ControllerProcessor extends AbstractProcessor {
    private Filer mFilter;
    @Nullable
    private Trees trees;
    private CompilerMessager messager;
    private RSProvider rsProvider;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mFilter = env.getFiler();
        messager = new CompilerMessager(env.getMessager());
        try {
            trees = Trees.instance(processingEnv);
        } catch (IllegalArgumentException ignored) {
            try {
                // Get original ProcessingEnvironment from Gradle-wrapped one or KAPT-wrapped one.
                for (Field field : processingEnv.getClass().getDeclaredFields()) {
                    if (field.getName().equals("delegate") || field.getName().equals("processingEnv")) {
                        field.setAccessible(true);
                        ProcessingEnvironment javacEnv = (ProcessingEnvironment) field.get(processingEnv);
                        trees = Trees.instance(javacEnv);
                        break;
                    }
                }
            } catch (Throwable ignored2) {
            }
        }
        rsProvider = new RSProvider(trees, messager);
        //  CompileMessager.setMessager(env.getMessager());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<Class<? extends Annotation>> annotationCls = new HashSet<>();
        annotationCls.add(Controller.class);
        annotationCls.add(InitState.class);
        annotationCls.add(BuildView.class);
        annotationCls.add(LiveOB.class);
        annotationCls.add(DidChangeLifecycleEvent.class);
        annotationCls.add(Dispose.class);
        annotationCls.add(OnActivityResult.class);
        annotationCls.add(OnPermissionResult.class);
        annotationCls.add(BindView.class);
        annotationCls.add(BundleValue.class);
        Set<String> result = new HashSet<>();
        for (Class<? extends Annotation> cls : annotationCls) {
            result.add(cls.getCanonicalName());
        }

        return result;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        final Map<TypeElement, ControllerDelegateInfo> bindingMap = findAndParseTargets(roundEnvironment);
        for (Map.Entry<TypeElement, ControllerDelegateInfo> entry : bindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            ControllerDelegateInfo delegateInfo = entry.getValue();
            JavaFile javaFile = delegateInfo.brewJava();
            //messager.printMessage(Diagnostic.Kind.NOTE, delegateInfo + "");
            try {
                javaFile.writeTo(mFilter);
            } catch (IOException e) {
                messager.errorElement(typeElement, "Unable to write binding for type %s: %s", typeElement, e.getMessage());
            }
        }
        return false;
    }


    private Map<TypeElement, ControllerDelegateInfo> findAndParseTargets(RoundEnvironment env) {
        final Map<TypeElement, ControllerDelegateInfo.Builder> builderMap = new LinkedHashMap<>();
        final Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();
        //controller
        if (notEnsureController(env, builderMap, erasedTargetNames)) {
            return Collections.emptyMap();
        }
        //bundleValue
        if (notEnsureBundleValue(env, builderMap, erasedTargetNames)) {
            return Collections.emptyMap();
        }
        //initState
        if (notEnsureInitState(env, builderMap, erasedTargetNames)) {
            return Collections.emptyMap();
        }
        //Lifecycle
        if (notEnsureLifecycle(env, builderMap, erasedTargetNames)) {
            return Collections.emptyMap();
        }

        //activityResult
        if (notEnsureActivityResult(env, builderMap, erasedTargetNames)) {
            return Collections.emptyMap();
        }
        //permissionResult
        if (notEnsurePermissionResult(env, builderMap, erasedTargetNames)) {
            return Collections.emptyMap();
        }
        //Dispose
        if (notEnsureDispose(env, builderMap, erasedTargetNames)) {
            return Collections.emptyMap();
        }
        //buildView
        if (notEnsureBuildView(env, builderMap, erasedTargetNames)) {
            return Collections.emptyMap();
        }
        //bindView
        if (notEnsureBindView(env, builderMap, erasedTargetNames)) {
            return Collections.emptyMap();
        }
        //listener
        for (Map.Entry<TypeElement, ControllerDelegateInfo.Builder> entry : builderMap.entrySet()) {
            entry.getValue().setListenerBindingProvider(ListenerBindingProvider.of(entry.getKey(), rsProvider));
        }

        final Map<TypeElement, ControllerDelegateInfo> controllerDelegateMap = new LinkedHashMap<>();
        for (Map.Entry<TypeElement, ControllerDelegateInfo.Builder> builderEntry : builderMap.entrySet()) {
            controllerDelegateMap.put(builderEntry.getKey(), builderEntry.getValue().build());
        }

        for (Map.Entry<TypeElement, ControllerDelegateInfo> entry : controllerDelegateMap.entrySet()) {

            TypeElement superClass = getSuperClass(entry.getKey());
            while (superClass != null) {
                if (controllerDelegateMap.containsKey(superClass)) {
                    controllerDelegateMap.get(entry.getKey()).setParent(controllerDelegateMap.get(superClass));
                    break;
                }
                superClass = getSuperClass(superClass);
            }

        }
        return controllerDelegateMap;
    }

    private boolean notEnsureController(RoundEnvironment env,
                                        Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                        Set<TypeElement> erasedTargetNames) {
        for (Element element : env.getElementsAnnotatedWith(Controller.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                if (notParseController(element, builderMap, erasedTargetNames)) {
                    return true;
                }
            } catch (Exception e) {
                messager.errorElement(element, e);
                return true;
            }
        }
        return false;
    }

    private boolean notParseController(Element element,
                                       Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                       Set<TypeElement> erasedTargetNames) {
        if (checkNotController(element)) {
            return true;
        }

        TypeElement typeElement = ElementHelper.asType(element);
        ControllerDelegateInfo.Builder builder = builderMap.get(typeElement);
        if (builder == null) {
            BindingResult<ControllerBinding> bindingResult = BindingFactory.createControllerBinding(typeElement,
                    rsProvider.elementToId(element, Controller.class, typeElement.getAnnotation(Controller.class).value()));
            if (checkNotBindResult(bindingResult)) {
                return true;
            }
            builderMap.put(typeElement, new ControllerDelegateInfo.Builder(bindingResult.getBinding(), messager));
        }
        erasedTargetNames.add(typeElement);
        return false;
    }


    private boolean notEnsureBundleValue(RoundEnvironment env,
                                         Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                         Set<TypeElement> erasedTargetNames) {
        for (Element element : env.getElementsAnnotatedWith(BundleValue.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                if (notParseBundleValue(element, builderMap, erasedTargetNames)) {
                    return true;
                }
            } catch (Exception e) {
                messager.errorElement(element, e);
                return true;
            }
        }
        return false;
    }


    private boolean notParseBundleValue(Element element,
                                        Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                        Set<TypeElement> erasedTargetNames) {


        if (checkNotController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, BundleValue.class, "field")) {
            return true;
        }
        if (isNotFieldElement(element, BundleValue.class)) {
            return true;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateInfo.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            BindingResult<BundleValueBinding> bindingResult = BindingFactory.createBundleValueBinding(element);
            if (checkNotBindResult(bindingResult)) {
                return true;
            }
            builder.addBinding(bindingResult.getBinding());
            erasedTargetNames.add(enclosingElement);
        }
        return false;
    }


    private boolean notEnsureInitState(RoundEnvironment env,
                                       Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                       Set<TypeElement> erasedTargetNames) {
        for (Element element : env.getElementsAnnotatedWith(InitState.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                if (notParseInitState(element, builderMap, erasedTargetNames)) {
                    return true;
                }
            } catch (Exception e) {
                messager.errorElement(element, e);
                return true;
            }
        }
        return false;
    }

    private boolean notParseInitState(Element element,
                                      Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                      Set<TypeElement> erasedTargetNames) {

        if (checkNotController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, InitState.class, "method")) {
            return true;
        }
        if (isNotMethodElement(element, InitState.class)) {
            return true;
        }
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateInfo.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            BindingResult<InitStateBinding> bindingResult = BindingFactory.createInitStatBinding(element);
            if (checkNotBindResult(bindingResult)) {
                return true;
            }
            builder.addBinding(bindingResult.getBinding());
            erasedTargetNames.add(enclosingElement);
        }
        return false;
    }

    private boolean notEnsureBuildView(RoundEnvironment env,
                                       Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                       Set<TypeElement> erasedTargetNames) {
        for (Element element : env.getElementsAnnotatedWith(BuildView.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                if (notParseBuildView(element, builderMap, erasedTargetNames)) {
                    return true;
                }
            } catch (Exception e) {
                messager.errorElement(element, e);
                return true;
            }
        }
        return false;
    }

    private boolean notParseBuildView(Element element,
                                      Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                      Set<TypeElement> erasedTargetNames) {

        if (checkNotController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, BuildView.class, "method")) {
            return true;
        }

        if (isNotMethodElement(element, BuildView.class)) {
            return true;
        }
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateInfo.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            BindingResult<BuildViewBinding> bindingResult = BindingFactory.createBuildView(element);
            if (checkNotBindResult(bindingResult)) {
                return true;
            }
            builder.addBinding(bindingResult.getBinding());
            erasedTargetNames.add(enclosingElement);
        }
        return false;
    }


    private boolean notEnsureBindView(RoundEnvironment env,
                                      Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                      Set<TypeElement> erasedTargetNames) {
        for (Element element : env.getElementsAnnotatedWith(BindView.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                if (notParseBindView(element, builderMap, erasedTargetNames)) {
                    return true;
                }
            } catch (Exception e) {
                messager.errorElement(element, e);
                return true;
            }
        }
        return false;
    }

    private boolean notParseBindView(Element element,
                                     Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                     Set<TypeElement> erasedTargetNames) {

        if (checkNotController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, BindView.class, "field")) {
            return true;
        }
        if (isNotFieldElement(element, BindView.class)) {
            return true;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateInfo.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            BindingResult<ViewFieldBinding> bindingResult = BindingFactory.createViewFieldBinding(element,
                    rsProvider.elementToId(element, BindView.class, element.getAnnotation(BindView.class).value()));
            if (checkNotBindResult(bindingResult)) {
                return true;
            }
            builder.addBinding(bindingResult.getBinding());
            erasedTargetNames.add(enclosingElement);
        }
        return false;
    }


    private boolean notEnsureLifecycle(RoundEnvironment env,
                                       Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                       Set<TypeElement> erasedTargetNames) {
        for (Element element : env.getElementsAnnotatedWith(DidChangeLifecycleEvent.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                if (notParseLifecycle(element, builderMap, erasedTargetNames)) {
                    return true;
                }
            } catch (Exception e) {
                messager.errorElement(element, e);
                return true;
            }
        }
        return false;
    }

    private boolean notParseLifecycle(Element element,
                                      Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                      Set<TypeElement> erasedTargetNames) {

        if (checkNotController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, DidChangeLifecycleEvent.class, "method")) {
            return true;
        }
        if (isNotMethodElement(element, DidChangeLifecycleEvent.class)) {
            return true;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateInfo.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            BindingResult<LifecycleBinding> bindingResult = BindingFactory.createLifecycleBinding(element);
            if (checkNotBindResult(bindingResult)) {
                return true;
            }
            builder.addBinding(bindingResult.getBinding());
            erasedTargetNames.add(enclosingElement);
        }
        return false;
    }

    private boolean notEnsureActivityResult(RoundEnvironment env,
                                            Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                            Set<TypeElement> erasedTargetNames) {
        for (Element element : env.getElementsAnnotatedWith(OnActivityResult.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                if (notParseActivityResult(element, builderMap, erasedTargetNames)) {
                    return true;
                }
            } catch (Exception e) {
                messager.errorElement(element, e);
                return true;
            }
        }
        return false;
    }

    private boolean notParseActivityResult(Element element,
                                           Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                           Set<TypeElement> erasedTargetNames) {

        if (checkNotController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, OnActivityResult.class, "method")) {
            return true;
        }
        if (isNotMethodElement(element, OnActivityResult.class)) {
            return true;
        }
        if (isNotActivityOrFragment(element.getEnclosingElement(), OnActivityResult.class)) {
            return true;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateInfo.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            BindingResult<ActivityResultBinding> bindingResult = BindingFactory.createActivityResultBinding(element);
            if (checkNotBindResult(bindingResult)) {
                return true;
            }
            builder.addBinding(bindingResult.getBinding());
            erasedTargetNames.add(enclosingElement);
        }
        return false;
    }

    private boolean notEnsurePermissionResult(RoundEnvironment env,
                                              Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                              Set<TypeElement> erasedTargetNames) {
        for (Element element : env.getElementsAnnotatedWith(OnPermissionResult.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                if (notParsePermissionResult(element, builderMap, erasedTargetNames)) {
                    return true;
                }
            } catch (Exception e) {
                messager.errorElement(element, e);
                return true;
            }
        }
        return false;
    }

    private boolean notParsePermissionResult(Element element,
                                             Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                             Set<TypeElement> erasedTargetNames) {

        if (checkNotController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, OnPermissionResult.class, "method")) {
            return true;
        }
        if (isNotMethodElement(element, OnPermissionResult.class)) {
            return true;
        }
        if (isNotActivityOrFragment(element.getEnclosingElement(), OnPermissionResult.class)) {
            return true;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateInfo.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            BindingResult<PermissionResultBinding> bindingResult = BindingFactory.createPermissionResult(element);
            if (checkNotBindResult(bindingResult)) {
                return true;
            }
            builder.addBinding(bindingResult.getBinding());
            erasedTargetNames.add(enclosingElement);
        }
        return false;
    }


    private boolean notEnsureDispose(RoundEnvironment env,
                                     Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                     Set<TypeElement> erasedTargetNames) {
        for (Element element : env.getElementsAnnotatedWith(Dispose.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                if (notParseDispose(element, builderMap, erasedTargetNames)) {
                    return true;
                }
            } catch (Exception e) {
                messager.errorElement(element, e);
                return true;
            }
        }
        return false;
    }

    private boolean notParseDispose(Element element,
                                    Map<TypeElement, ControllerDelegateInfo.Builder> builderMap,
                                    Set<TypeElement> erasedTargetNames) {

        if (checkNotController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, Dispose.class, "method")) {
            return true;
        }
        if (isNotMethodElement(element, Dispose.class)) {
            return true;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateInfo.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            BindingResult<DisposeBinding> bindingResult = BindingFactory.createDisposeBinding(element);
            if (checkNotBindResult(bindingResult)) {
                return true;
            }
            builder.addBinding(bindingResult.getBinding());
            erasedTargetNames.add(enclosingElement);
        }
        return false;
    }


    /************Check START*************/

    private boolean checkNotController(Element element) {
        if (element.getKind() != CLASS) {
            messager.errorElement(element, "controller only be class");
            return true;
        }
        if (element.getModifiers().contains(PRIVATE)) {
            messager.errorElement(element, "controller not be private");
            return true;
        }
        return false;
    }


    private boolean isInaccessibleElement(Element element,
                                          Class<? extends Annotation> annotationClass,
                                          String targetThing) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            TypeElement enclosingElement = ElementHelper.asType(element);
            messager.errorElement(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            return true;
        } else {
            return false;
        }
    }

    private boolean isNotFieldElement(Element element,
                                      Class<? extends Annotation> annotationClass) {
        if (element.getKind() != ElementKind.FIELD) {
            messager.errorElement(element, "@%s annotation must be on a field", annotationClass);
            return true;
        }
        return false;
    }

    private boolean isNotMethodElement(Element element,
                                       Class<? extends Annotation> annotationClass) {
        if (element.getKind() != ElementKind.METHOD) {
            messager.errorElement(element, "@%s annotation must be on a method", annotationClass);
            return true;
        }
        return false;
    }

    private <T> boolean checkNotBindResult(BindingResult<T> result) {
        if (result.isError()) {
            messager.errorElement(result.getElement(), result.getMessage());
            return true;
        } else if (result.isWarn()) {
            messager.warnElement(result.getElement(), result.getMessage());
            return true;
        } else {
            return false;
        }
    }

    private boolean isNotActivityOrFragment(Element element,
                                            Class<? extends Annotation> annotationClass) {
        TypeMirror typeMirror = ElementHelper.asType(element).asType();
        if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_ACTIVITY) ||
                TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_FRAGMENT)) {
            return false;
        }
        messager.errorElement(element, "@%s annotation only support Activity or Fragment", annotationClass);
        return true;

    }


}
