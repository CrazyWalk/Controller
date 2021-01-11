package cn.luyinbros.valleyframework.controller;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;


import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Field;
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

import cn.luyinbros.valleyframework.controller.annotation.Controller;

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
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        return false;
    }


    private Map<TypeElement, ControllerDelegateInfo.Builder> findAndParseTargets(RoundEnvironment env) {
        final Map<TypeElement, ControllerDelegateInfo.Builder> builderMap = new LinkedHashMap<>();
        final Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();
        if (notEnsureController(env, builderMap, erasedTargetNames)) {
            return new LinkedHashMap<>();
        }
        return builderMap;
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
//        if (builder == null) {
//            BindingFactory.createControllerBinding(typeElement,rsProvider.)
//            builder = ControllerDelegateInfo.newBuilder(typeElement,
//                    elementToId(typeElement,
//                            Controller.class,
//                            typeElement.getAnnotation(Controller.class).value()),
//                    this);
//            builderMap.put(typeElement, builder);
//        }

        erasedTargetNames.add(typeElement);
        return true;
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



    private void errorElement(Element element, Throwable e) {
//        StackTraceElement[] stackTraceElementArray = e.getStackTrace();
//        StringBuilder sb = new StringBuilder();
//        for (StackTraceElement stackTraceElement : stackTraceElementArray) {
//            sb.append(stackTraceElement).append("\n");
//        }
//        CompileMessager.warn(e + " " + sb.toString());
        messager.printMessage(Diagnostic.Kind.ERROR, "invalidate " + e.getMessage(), element);
    }

    private void errorElement(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

}
