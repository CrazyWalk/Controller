package cn.luyinbros.valleyframework.controller;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.annotation.BindView;
import cn.luyinbros.valleyframework.controller.annotation.BuildView;
import cn.luyinbros.valleyframework.controller.annotation.BundleValue;
import cn.luyinbros.valleyframework.controller.annotation.DidChangeLifecycleEvent;
import cn.luyinbros.valleyframework.controller.annotation.Dispose;
import cn.luyinbros.valleyframework.controller.annotation.InitState;
import cn.luyinbros.valleyframework.controller.annotation.LiveOB;
import cn.luyinbros.valleyframework.controller.annotation.OnActivityResult;
import cn.luyinbros.valleyframework.controller.annotation.OnPermissionResult;
import cn.luyinbros.compiler.ElementHelper;
import cn.luyinbros.compiler.TypeHelper;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

public class Checker {

    public static boolean isInvalidateBindView(Element element) {
        if (isInvalidateController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, BindView.class, "field")) {
            return true;
        }
        if (isNotFieldElement(element, BindView.class)) {
            return true;
        }
        return false;

    }


    public static boolean isInvalidateInitState(Element element) {
        if (isInvalidateController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, InitState.class, "method")) {
            return true;
        }

        if (isNotMethodElement(element, InitState.class)) {
            return true;
        }

        return false;
    }


    public static boolean isInvalidateBuildView(Element element) {
        if (isInvalidateController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, BuildView.class, "method")) {
            return true;
        }

        if (isNotMethodElement(element, BuildView.class)) {
            return true;
        }

        return false;
    }


    public static boolean isInvalidateDidChangeLifecycleEvent(Element element) {
        if (isInvalidateController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, DidChangeLifecycleEvent.class, "method")) {
            return true;
        }
        if (isNotMethodElement(element, DidChangeLifecycleEvent.class)) {
            return true;
        }
        return false;
    }

    public static boolean isInvalidateLiveOB(Element element) {
        if (isInvalidateController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, LiveOB.class, "method")) {
            return true;
        }
        if (isNotMethodElement(element, LiveOB.class)) {
            return true;
        }
        return false;
    }





    public static boolean isInvalidateDispose(Element element) {
        if (isInvalidateController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, Dispose.class, "method")) {
            return true;
        }
        if (isNotMethodElement(element, Dispose.class)) {
            return true;
        }
        return false;
    }


    public static boolean isInvalidateOnActivityResult(Element element) {
        if (isInvalidateController(element.getEnclosingElement())) {
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
        return false;
    }

    public static boolean isInvalidateOnPermissionResult(Element element) {
        if (isInvalidateController(element.getEnclosingElement())) {
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
        return false;
    }

    public static boolean isInvalidateController(Element element) {
        if (element.getKind() != CLASS) {
            CompileMessager.error(element, "controller only be class");
            return true;
        }
        if (element.getModifiers().contains(PRIVATE)) {
            CompileMessager.error(element, "controller not be private");
            return true;
        }
        return false;
    }


    public static boolean isInvalidateBundleValue(Element element) {
        if (isInvalidateController(element.getEnclosingElement())) {
            return true;
        }
        if (isInaccessibleElement(element, BundleValue.class, "field")) {
            return true;
        }
        if (isNotFieldElement(element, BundleValue.class)) {
            return true;
        }
        return false;
    }


    private static boolean isInaccessibleElement(Element element,
                                                 Class<? extends Annotation> annotationClass,
                                                 String targetThing) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            TypeElement enclosingElement = ElementHelper.asType(element);
            CompileMessager.error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            return true;
        } else {
            return false;
        }

    }

    private static boolean isNotMethodElement(Element element,
                                              Class<? extends Annotation> annotationClass) {
        if (element.getKind() != ElementKind.METHOD) {
            CompileMessager.error(element, "@%s annotation must be on a method", annotationClass);
            return true;
        }
        return false;
    }

    private static boolean isNotFieldElement(Element element,
                                             Class<? extends Annotation> annotationClass) {
        if (element.getKind() != ElementKind.FIELD) {
            CompileMessager.error(element, "@%s annotation must be on a field", annotationClass);
            return true;
        }
        return false;
    }


    private static boolean isNotActivityOrFragment(Element element,
                                                   Class<? extends Annotation> annotationClass) {
        TypeMirror typeMirror = ElementHelper.asType(element).asType();
        if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_ACTIVITY) ||
                TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_FRAGMENT)) {
            return false;
        }
        CompileMessager.error(element, "@%s annotation only support Activity or Fragment", annotationClass);
        return true;

    }


}
