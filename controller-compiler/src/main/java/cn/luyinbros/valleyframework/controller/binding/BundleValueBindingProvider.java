package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.Constants;
import cn.luyinbros.valleyframework.controller.ControllerDelegateInfo;
import cn.luyinbros.compiler.TypeHelper;

public class BundleValueBindingProvider {
    private List<BundleValueBinding> bundleValueBindings = new ArrayList<>();

    public void addBinding(BundleValueBinding binding) {
        bundleValueBindings.add(binding);
    }

    public boolean isEmpty() {
        return bundleValueBindings.isEmpty();
    }

    public CodeBlock code(ControllerDelegateInfo info) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (isEmpty()) {
            return builder.build();
        }

        ControllerDelegateInfo.Type clientType = info.getType();
        if (clientType == ControllerDelegateInfo.Type.ACTIVITY) {
            builder.addStatement("final $L data=target.getIntent()", Constants.CLASS_INTENT);
        } else {
            builder.addStatement("final $L data=target.getArguments()", Constants.CLASS_BUNDLE);
        }
        builder.beginControlFlow("if(data==null)");
        for (BundleValueBinding bundleValueBinding : bundleValueBindings) {
            TypeMirror mirror = bundleValueBinding.getTypeMirror();
            TypeKind typeKind = mirror.getKind();
            if (!typeKind.isPrimitive()) {
                if (bundleValueBinding.isRequired()) {
                    builder.addStatement("$L.requireNonNull(target.$L)", Constants.CLASS_OBJECTS, bundleValueBinding.getFiledName());
                }
            }
        }
        builder.nextControlFlow("else");
        for (BundleValueBinding bundleValueBinding : bundleValueBindings) {
            TypeMirror mirror = bundleValueBinding.getTypeMirror();
            TypeKind typeKind = mirror.getKind();
            if (typeKind.isPrimitive()) {
                builder.addStatement("target.$L=data.$L($S,target.$L)",
                        bundleValueBinding.getFiledName(),
                        getInvokeMethodName(clientType, mirror),
                        bundleValueBinding.getKey(),
                        bundleValueBinding.getFiledName()
                );
            } else if (typeKind == TypeKind.ARRAY || typeKind == TypeKind.DECLARED) {
                String invokeMethodName = getInvokeMethodName(clientType, mirror);
                if (invokeMethodName != null && !invokeMethodName.isEmpty()) {
                    builder.addStatement("final $L $L=($L)data.$L($S)",
                            ClassName.get(bundleValueBinding.getTypeMirror()),
                            bundleValueBinding.getFiledName(),
                            ClassName.get(bundleValueBinding.getTypeMirror()),
                            invokeMethodName,
                            bundleValueBinding.getKey());
                    if (bundleValueBinding.isRequired()) {
                        builder.beginControlFlow("if($L==null)",
                                bundleValueBinding.getFiledName());
                        builder.addStatement("$L.requireNonNull(target.$L)",
                                Constants.CLASS_OBJECTS,
                                bundleValueBinding.getFiledName());
                        builder.nextControlFlow("else");
                        builder.addStatement("target.$L=$L.requireNonNull($L)",
                                bundleValueBinding.getFiledName(),
                                Constants.CLASS_OBJECTS,
                                bundleValueBinding.getFiledName());
                        builder.endControlFlow();
                    } else {
                        builder.addStatement("target.$L=$L",
                                bundleValueBinding.getFiledName(),
                                bundleValueBinding.getFiledName());
                    }
                }

            }
        }

        builder.endControlFlow();

        // if (!bundleValueBindings.isEmpty()) {

//    }
        return builder.build();
    }


    private static Map<String, String> intentInvokeMethodNameMap = new HashMap<>();
    private static Map<String, String> bundleInvokeMethodNameMap = new HashMap<>();

    static {
        //intent
        intentInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.CHAR), "getCharExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.BYTE), "getByteExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.INT), "getIntExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.LONG), "getLongExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.FLOAT), "getFloatExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.DOUBLE), "getDoubleExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.BOOLEAN), "getBooleanExtra");

        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.CHAR), "getCharArrayExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.BYTE), "getByteArrayExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.INT), "getIntArrayExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.LONG), "getLongArrayExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.FLOAT), "getFloatArrayExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.DOUBLE), "getDoubleArrayExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.BOOLEAN), "getBooleanArrayExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(Constants.TYPE_CHAR_SEQUENCE), "getCharSequenceArrayExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(Constants.TYPE_STRING), "getStringArrayExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(Constants.TYPE_PARCELABLE), "getParcelableArrayExtra");


        intentInvokeMethodNameMap.put(Constants.TYPE_SERIALIZABLE, "getSerializableExtra");
        intentInvokeMethodNameMap.put(Constants.TYPE_PARCELABLE, "getParcelableExtra");
        intentInvokeMethodNameMap.put(Constants.TYPE_STRING, "getStringExtra");
        intentInvokeMethodNameMap.put(Constants.TYPE_CHAR_SEQUENCE, "getCharSequenceExtra");
        intentInvokeMethodNameMap.put(Constants.TYPE_BUNDLE, "getBundleExtra");

        intentInvokeMethodNameMap.put(getInvokeMethodNameListKey(Constants.TYPE_INTEGER), "getIntegerArrayListExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameListKey(Constants.TYPE_STRING), "getStringArrayListExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameListKey(Constants.TYPE_CHAR_SEQUENCE), "getCharSequenceArrayListExtra");
        intentInvokeMethodNameMap.put(getInvokeMethodNameListKey(Constants.TYPE_PARCELABLE), "getParcelableArrayListExtra");


        //bundle
        bundleInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.CHAR), "getChar");
        bundleInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.BYTE), "getByte");
        bundleInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.INT), "getInt");
        bundleInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.LONG), "getLong");
        bundleInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.FLOAT), "getFloat");
        bundleInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.DOUBLE), "getDouble");
        bundleInvokeMethodNameMap.put(getInvokeMethodNamePrimitiveKey(TypeKind.BOOLEAN), "getBoolean");

        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.CHAR), "getCharArray");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.BYTE), "getByteArray");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.INT), "getIntArray");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.LONG), "getLongArray");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.FLOAT), "getFloatArray");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.DOUBLE), "getDoubleArray");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(TypeKind.BOOLEAN), "getBooleanArray");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(Constants.TYPE_CHAR_SEQUENCE), "getCharSequenceArray");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(Constants.TYPE_STRING), "getStringArray");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameArrayKey(Constants.TYPE_PARCELABLE), "getParcelableArray");


        bundleInvokeMethodNameMap.put(Constants.TYPE_SERIALIZABLE, "getSerializable");
        bundleInvokeMethodNameMap.put(Constants.TYPE_PARCELABLE, "getParcelable");
        bundleInvokeMethodNameMap.put(Constants.TYPE_STRING, "getString");
        bundleInvokeMethodNameMap.put(Constants.TYPE_CHAR_SEQUENCE, "getCharSequence");
        bundleInvokeMethodNameMap.put(Constants.TYPE_BUNDLE, "getBundle");

        bundleInvokeMethodNameMap.put(getInvokeMethodNameListKey(Constants.TYPE_INTEGER), "getIntegerArrayList");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameListKey(Constants.TYPE_STRING), "getStringArrayList");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameListKey(Constants.TYPE_CHAR_SEQUENCE), "getCharSequenceArrayList");
        bundleInvokeMethodNameMap.put(getInvokeMethodNameListKey(Constants.TYPE_PARCELABLE), "getParcelableArrayList");
    }


    private static String getInvokeMethodNamePrimitiveKey(TypeKind typeKind) {
        if (typeKind == TypeKind.CHAR) {
            return "char";
        } else if (typeKind == TypeKind.BYTE) {
            return "byte";
        } else if (typeKind == TypeKind.INT) {
            return "int";
        } else if (typeKind == TypeKind.LONG) {
            return "long";
        } else if (typeKind == TypeKind.FLOAT) {
            return "float";
        } else if (typeKind == TypeKind.DOUBLE) {
            return "double";
        } else if (typeKind == TypeKind.BOOLEAN) {
            return "boolean";
        } else {
            return "";
        }
    }

    private static String getInvokeMethodNameArrayKey(TypeKind typeKind) {
        if (typeKind == TypeKind.CHAR) {
            return "charArray";
        } else if (typeKind == TypeKind.BYTE) {
            return "byteArray";
        } else if (typeKind == TypeKind.INT) {
            return "intArray";
        } else if (typeKind == TypeKind.LONG) {
            return "longArray";
        } else if (typeKind == TypeKind.FLOAT) {
            return "floatArray";
        } else if (typeKind == TypeKind.DOUBLE) {
            return "doubleArray";
        } else if (typeKind == TypeKind.BOOLEAN) {
            return "booleanArray";
        } else {
            return "";
        }
    }


    private static String getInvokeMethodNameArrayKey(String type) {
        return type + "Array";
    }

    private static String getInvokeMethodNameListKey(String type) {
        return type + "ArrayList";
    }

    public String getInvokeMethodName(ControllerDelegateInfo.Type type, TypeMirror typeMirror) {
        TypeKind kind = typeMirror.getKind();
        if (type == ControllerDelegateInfo.Type.ACTIVITY) {
            if (kind.isPrimitive()) {
                return intentInvokeMethodNameMap.get(getInvokeMethodNamePrimitiveKey(kind));
            } else if (kind == TypeKind.ARRAY) {
                ArrayType arrayType = TypeHelper.asArray(typeMirror);
                TypeMirror componentTypeMirror = arrayType.getComponentType();
                TypeKind componentTypeKind = componentTypeMirror.getKind();
                if (componentTypeKind.isPrimitive()) {
                    return intentInvokeMethodNameMap.get(getInvokeMethodNameArrayKey(componentTypeKind));
                } else if (TypeHelper.isTypeEqual(componentTypeMirror, Constants.TYPE_STRING)) {
                    return intentInvokeMethodNameMap.get(getInvokeMethodNameArrayKey(Constants.TYPE_STRING));
                } else if (TypeHelper.isTypeEqual(componentTypeMirror, Constants.TYPE_CHAR_SEQUENCE)) {
                    return intentInvokeMethodNameMap.get(getInvokeMethodNameArrayKey(Constants.TYPE_CHAR_SEQUENCE));
                } else if (TypeHelper.isSubtypeOfType(componentTypeMirror, Constants.TYPE_PARCELABLE)) {
                    return intentInvokeMethodNameMap.get(getInvokeMethodNameArrayKey(Constants.TYPE_PARCELABLE));
                } else if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_SERIALIZABLE)) {
                    return intentInvokeMethodNameMap.get(Constants.TYPE_SERIALIZABLE);
                }

            } else if (kind == TypeKind.DECLARED) {
                if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_STRING)) {
                    return intentInvokeMethodNameMap.get(Constants.TYPE_STRING);
                } else if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_CHAR_SEQUENCE)) {
                    return intentInvokeMethodNameMap.get(Constants.TYPE_CHAR_SEQUENCE);
                } else if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_BUNDLE)) {
                    return intentInvokeMethodNameMap.get(Constants.TYPE_BUNDLE);
                } else if (TypeHelper.isTypeEqualIgnoreTypeVar(typeMirror, Constants.TYPE_ARRAY_LIST)) {
                    TypeMirror typeVar = TypeHelper.asDeclared(typeMirror).getTypeArguments().get(0);
                    if (TypeHelper.isTypeEqual(typeVar, Constants.TYPE_INTEGER)) {
                        return intentInvokeMethodNameMap.get(getInvokeMethodNameListKey(Constants.TYPE_INTEGER));
                    } else if (TypeHelper.isTypeEqual(typeVar, Constants.TYPE_STRING)) {
                        return intentInvokeMethodNameMap.get(getInvokeMethodNameListKey(Constants.TYPE_STRING));
                    } else if (TypeHelper.isTypeEqual(typeVar, Constants.TYPE_CHAR_SEQUENCE)) {
                        return intentInvokeMethodNameMap.get(getInvokeMethodNameListKey(Constants.TYPE_CHAR_SEQUENCE));
                    } else if (TypeHelper.isTypeEqual(typeVar, Constants.TYPE_PARCELABLE)) {
                        return intentInvokeMethodNameMap.get(getInvokeMethodNameListKey(Constants.TYPE_PARCELABLE));
                    } else {
                        return intentInvokeMethodNameMap.get(Constants.TYPE_SERIALIZABLE);
                    }
                } else if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_SERIALIZABLE)) {
                    return intentInvokeMethodNameMap.get(Constants.TYPE_SERIALIZABLE);
                } else if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_PARCELABLE)) {
                    return intentInvokeMethodNameMap.get(Constants.TYPE_PARCELABLE);
                }
            }
        } else {
            if (kind.isPrimitive()) {
                return bundleInvokeMethodNameMap.get(getInvokeMethodNamePrimitiveKey(kind));
            } else if (kind == TypeKind.ARRAY) {
                ArrayType arrayType = TypeHelper.asArray(typeMirror);
                TypeMirror componentTypeMirror = arrayType.getComponentType();
                TypeKind componentTypeKind = componentTypeMirror.getKind();
                if (componentTypeKind.isPrimitive()) {
                    return bundleInvokeMethodNameMap.get(getInvokeMethodNameArrayKey(componentTypeKind));
                } else if (TypeHelper.isTypeEqual(componentTypeMirror, Constants.TYPE_STRING)) {
                    return bundleInvokeMethodNameMap.get(getInvokeMethodNameArrayKey(Constants.TYPE_STRING));
                } else if (TypeHelper.isTypeEqual(componentTypeMirror, Constants.TYPE_CHAR_SEQUENCE)) {
                    return bundleInvokeMethodNameMap.get(getInvokeMethodNameArrayKey(Constants.TYPE_CHAR_SEQUENCE));
                } else if (TypeHelper.isSubtypeOfType(componentTypeMirror, Constants.TYPE_PARCELABLE)) {
                    return bundleInvokeMethodNameMap.get(getInvokeMethodNameArrayKey(Constants.TYPE_PARCELABLE));
                } else if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_SERIALIZABLE)) {
                    return bundleInvokeMethodNameMap.get(Constants.TYPE_SERIALIZABLE);
                }

            } else if (kind == TypeKind.DECLARED) {
                if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_STRING)) {
                    return bundleInvokeMethodNameMap.get(Constants.TYPE_STRING);
                } else if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_CHAR_SEQUENCE)) {
                    return bundleInvokeMethodNameMap.get(Constants.TYPE_CHAR_SEQUENCE);
                } else if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_BUNDLE)) {
                    return bundleInvokeMethodNameMap.get(Constants.TYPE_BUNDLE);
                } else if (TypeHelper.isTypeEqualIgnoreTypeVar(typeMirror, Constants.TYPE_ARRAY_LIST)) {
                    TypeMirror typeVar = TypeHelper.asDeclared(typeMirror).getTypeArguments().get(0);
                    if (TypeHelper.isTypeEqual(typeVar, Constants.TYPE_INTEGER)) {
                        return bundleInvokeMethodNameMap.get(getInvokeMethodNameListKey(Constants.TYPE_INTEGER));
                    } else if (TypeHelper.isTypeEqual(typeVar, Constants.TYPE_STRING)) {
                        return bundleInvokeMethodNameMap.get(getInvokeMethodNameListKey(Constants.TYPE_STRING));
                    } else if (TypeHelper.isTypeEqual(typeVar, Constants.TYPE_CHAR_SEQUENCE)) {
                        return bundleInvokeMethodNameMap.get(getInvokeMethodNameListKey(Constants.TYPE_CHAR_SEQUENCE));
                    } else if (TypeHelper.isTypeEqual(typeVar, Constants.TYPE_PARCELABLE)) {
                        return bundleInvokeMethodNameMap.get(getInvokeMethodNameListKey(Constants.TYPE_PARCELABLE));
                    } else {
                        return bundleInvokeMethodNameMap.get(Constants.TYPE_SERIALIZABLE);
                    }
                } else if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_SERIALIZABLE)) {
                    return bundleInvokeMethodNameMap.get(Constants.TYPE_SERIALIZABLE);
                } else if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_PARCELABLE)) {
                    return bundleInvokeMethodNameMap.get(Constants.TYPE_PARCELABLE);
                }
            }
        }
        return "";
    }


}
