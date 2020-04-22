package cn.luyinbros.valleyframework.controller;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import cn.luyinbros.compiler.TypeHelper;

public class Utils {

    public static boolean isNotNullElement(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        boolean isNotNull = true;

        for (AnnotationMirror annotationMirror : annotationMirrors) {
            String simpleName = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
            if (simpleName.equals("Nullable") || simpleName.equals("Option")) {
                isNotNull = false;
                break;
            }
        }
        return isNotNull;
    }

    public static boolean isOptionElement(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            String simpleName = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
            if (simpleName.equals("Option")) {
                return true;
            }
        }
        return false;
    }


    public static boolean isStrictTypeParameters(List<? extends VariableElement> variableElements, Object... types) {
        if (variableElements.size() != types.length) {
            throw new IllegalStateException();
        }

        boolean isSuccess = true;

        for (int index = 0; index < variableElements.size(); index++) {
            Object type = types[index];
            VariableElement variableElement = variableElements.get(index);
            if (!matchParameter(variableElement, type)) {
                isSuccess = false;
                break;
            }
        }
        return isSuccess;
    }

    public static boolean isMatchTwoNoOrderParameters(List<? extends VariableElement> variableElements, Object required, Object... other) {
        boolean findRequired = false;
        boolean findOther = false;
        q:
        for (VariableElement variableElement : variableElements) {
            if (matchParameter(variableElement, required)) {
                if (findRequired) {
                    break;
                }
                findRequired = true;
            } else {
                for (Object type : other) {
                    if (matchParameter(variableElement, type)) {
                        if (findOther) {
                            break q;
                        }
                        findOther = true;
                    }
                }
            }

        }

        return findRequired && findOther;

    }

    private static boolean matchParameter(VariableElement variableElement, Object type) {
        if (type instanceof TypeKind) {
            return variableElement.asType().getKind() == type;
        } else if (type instanceof String) {
            return TypeHelper.isTypeEqual(variableElement.asType(), (String) type);
        } else {
            throw new IllegalStateException();
        }
    }

    public static TypeName getTypeName(String target) {
        if ("void".equals(target)) {
            return TypeName.VOID;
        } else if ("char".equals(target)) {
            return TypeName.CHAR;
        } else if ("byte".equals(target)) {
            return TypeName.BYTE;
        } else if ("int".equals(target)) {
            return TypeName.INT;
        } else if ("long".equals(target)) {
            return TypeName.LONG;
        } else if ("float".equals(target)) {
            return TypeName.FLOAT;
        } else if ("double".equals(target)) {
            return TypeName.DOUBLE;
        } else if ("boolean".equals(target)) {
            return TypeName.BOOLEAN;
        } else {
            return ClassName.bestGuess(target);
        }
    }


    public static String getPackageName(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        return ((PackageElement) element).getQualifiedName().toString();
    }


}
