package cn.luyinbros.valleyframework.controller;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.sun.source.util.Trees;


import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import javax.lang.model.element.TypeElement;
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
import cn.luyinbros.valleyframework.controller.binding.BindingResult;
import cn.luyinbros.valleyframework.controller.binding.ControllerBinding;

import static cn.luyinbros.valleyframework.controller.ElementHelper.getSuperClass;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
@AutoService(Processor.class)
public class ControllerProcessor extends AbstractProcessor {
    private Filer mFilter;
    @Nullable
    private Trees trees;
    private Messager messager;
    private RSProvider rsProvider;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mFilter = env.getFiler();
        messager = env.getMessager();
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
        messager.printMessage(Diagnostic.Kind.NOTE, bindingMap.size() + "", null);
        for (Map.Entry<TypeElement, ControllerDelegateInfo> entry : bindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            ControllerDelegateInfo binding = entry.getValue();
            JavaFile javaFile = binding.brewJava();
            messager.printMessage(Diagnostic.Kind.NOTE, "", typeElement);
            try {
                javaFile.writeTo(mFilter);
            } catch (IOException e) {
                errorElement(typeElement, "Unable to write binding for type %s: %s", typeElement, e.getMessage());
            }
        }
        return false;
    }


    private Map<TypeElement, ControllerDelegateInfo> findAndParseTargets(RoundEnvironment env) {
        final Map<TypeElement, ControllerDelegateInfo.Builder> builderMap = new LinkedHashMap<>();
        final Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();
        if (notEnsureController(env, builderMap, erasedTargetNames)) {
            return new LinkedHashMap<>();
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "Builder");
        messager.printMessage(Diagnostic.Kind.NOTE, builderMap.size() + "");

        final Map<TypeElement, ControllerDelegateInfo> controllerDelegate = new LinkedHashMap<>();
        for (Map.Entry<TypeElement, ControllerDelegateInfo.Builder> builderEntry : builderMap.entrySet()) {
            controllerDelegate.put(builderEntry.getKey(), builderEntry.getValue().build());
        }

        for (Map.Entry<TypeElement, ControllerDelegateInfo> entry : controllerDelegate.entrySet()) {

            TypeElement superClass = getSuperClass(entry.getKey());
            while (superClass != null) {
                if (controllerDelegate.containsKey(superClass)) {
                    //   controllerDelegate.get(entry.getKey()).setParent(controllerDelegate.get(superClass));
                    break;
                }
                superClass = getSuperClass(superClass);
            }

        }
        return controllerDelegate;
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
                errorElement(element, e);
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
            messager.printMessage(Diagnostic.Kind.NOTE, bindingResult + "", null);
            builderMap.put(typeElement, new ControllerDelegateInfo.Builder(bindingResult.getBinding()));
        }
        messager.printMessage(Diagnostic.Kind.NOTE, builderMap + "");
        erasedTargetNames.add(typeElement);
        return false;
    }


    private boolean checkNotController(Element element) {
        if (element.getKind() != CLASS) {
            errorElement(element, "controller only be class");
            return true;
        }
        if (element.getModifiers().contains(PRIVATE)) {
            errorElement(element, "controller not be private");
            return true;
        }
        return false;
    }

    private <T> boolean checkNotBindResult(BindingResult<T> result) {
        if (result.isError()) {
            messager.printMessage(Diagnostic.Kind.ERROR, result.getMessage(), result.getElement());
            return true;
        } else if (result.isWarn()) {
            messager.printMessage(Diagnostic.Kind.WARNING, result.getMessage(), result.getElement());
            return true;
        } else {
            return false;
        }
    }

    public void errorElement(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }


    private void errorElement(Element element, Throwable e) {
        messager.printMessage(Diagnostic.Kind.ERROR, "invalidate " + e.getMessage(), element);
    }

    private void errorElement(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    public void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(kind, message, element);

    }
}
