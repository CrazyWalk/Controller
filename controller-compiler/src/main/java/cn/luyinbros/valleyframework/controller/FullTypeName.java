package cn.luyinbros.valleyframework.controller;

import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.compiler.TypeHelper;

public class FullTypeName {
    private TypeName typeName;
    private TypeMirror typeMirror;


    public FullTypeName(TypeName typeName, TypeMirror typeMirror) {
        this.typeName = typeName;
        this.typeMirror = typeMirror;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }


    public boolean isArray(TypeKind typeKind) {
        return TypeHelper.isArray(typeMirror, typeKind);
    }

    public boolean isArray(String type) {
        return TypeHelper.isArray(typeMirror, type);
    }


    public boolean isAssignType(TypeKind typeKind) {
        return typeMirror.getKind() == typeKind;
    }

    @Override
    public String toString() {
        return "FullTypeName{" +
                "typeName=" + typeName +
                ", typeMirror=" + typeMirror +
                '}';
    }
}
