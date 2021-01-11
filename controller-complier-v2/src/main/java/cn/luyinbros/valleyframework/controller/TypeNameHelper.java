package cn.luyinbros.valleyframework.controller;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;

public class TypeNameHelper {

    public static ClassName get(TypeMirror type) {
        return ClassName.get(ElementHelper.asType(TypeHelper.asDeclared(type).asElement()));
    }



    public static TypeName get(String name) {
        if (name.equals("void")) {
            return TypeName.VOID;
        } else if (name.equals("boolean")) {
            return TypeName.BOOLEAN;
        } else if (name.equals("byte")) {
            return TypeName.BYTE;
        } else if (name.equals("short")) {
            return TypeName.FLOAT;
        } else if (name.equals("int")) {
            return TypeName.INT;
        } else if (name.equals("long")) {
            return TypeName.LONG;
        } else if (name.equals("char")) {
            return TypeName.CHAR;
        } else if (name.equals("float")) {
            return TypeName.FLOAT;
        } else if (name.equals("double")) {
            return TypeName.DOUBLE;
        } else {
            return ClassName.bestGuess(name);
        }
    }


}
