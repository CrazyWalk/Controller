package cn.luyinbros.android.controller.binding;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import cn.luyinbros.android.controller.Constants;
import cn.luyinbros.android.controller.FullTypeName;

import static javax.lang.model.element.Modifier.PUBLIC;

public class PermissionResultBindingProvider {
    private List<PermissionResultBinding> requestBindings = new ArrayList<>();
    private List<PermissionResultBinding> permissionBindings = new ArrayList<>();
    private List<PermissionResultBinding> otherBindings = new ArrayList<>();

    private static final String PARAM_NAME_REQUEST_CODE = "requestCode";
    private static final String PARAM_NAME_PERMISSION = "permissions";
    private static final String PARAM_NAME_GRANT_RESULTS = "grantResults";

    public void addBinding(PermissionResultBinding binding) {
        if (binding.getRequestCode() >= PermissionResultBinding.ALLOW_EXCLUDE_MIN_REQUEST_CODE) {
            requestBindings.add(binding);
        } else if (binding.getPermissions().length > 0) {
            permissionBindings.add(binding);
        } else {
            otherBindings.add(binding);
        }
    }


    public void code(TypeSpec.Builder result) {
        if (requestBindings.isEmpty() &&
                permissionBindings.isEmpty() &&
                otherBindings.isEmpty()) {
            return;
        }
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("onRequestPermissionsResult")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.INT, PARAM_NAME_REQUEST_CODE)
                .addParameter(ArrayTypeName.of(Constants.CLASS_STRING), PARAM_NAME_PERMISSION)
                .addParameter(ArrayTypeName.of(TypeName.INT), PARAM_NAME_GRANT_RESULTS)
                .returns(ClassName.VOID);

        methodBuilder.addCode(CodeBlock.builder()
                .beginControlFlow("if($L.length==$L)", PARAM_NAME_PERMISSION, 0)
                .addStatement("return")
                .endControlFlow()
                .build());

        addFields(result);
        methodBuilder.addCode(generationOtherCodeBindingCode(false));
        if (!requestBindings.isEmpty()) {
            CodeBlock.Builder rootCodeBlock = CodeBlock.builder();
            boolean hasNextControl = false;
            for (PermissionResultBinding binding : requestBindings) {
                if (hasNextControl) {
                    rootCodeBlock.beginControlFlow("else if($L == $L)", PARAM_NAME_REQUEST_CODE, binding.getRequestCode());
                } else {
                    rootCodeBlock.beginControlFlow("if($L == $L)", PARAM_NAME_REQUEST_CODE, binding.getRequestCode());
                }

                if (binding.getPermissions().length != 0) {
                    rootCodeBlock.beginControlFlow("if($L)", getEqualPermissionsCodeText(binding));
                    rootCodeBlock.add(invokeCode(binding));
                    rootCodeBlock.endControlFlow();
                } else {
                    rootCodeBlock.add(invokeCode(binding));
                }
                hasNextControl = true;
                rootCodeBlock.endControlFlow();
            }

            if (!permissionBindings.isEmpty()) {
                rootCodeBlock.beginControlFlow("else");
                rootCodeBlock.add(handleControlPermissions());
                rootCodeBlock.endControlFlow();
            } else if (!otherBindings.isEmpty()) {
                rootCodeBlock.beginControlFlow("else");
                rootCodeBlock.add(generationOtherCodeBindingCode(true));
                rootCodeBlock.endControlFlow();
            }
            methodBuilder.addCode(rootCodeBlock.build());
        } else if (!permissionBindings.isEmpty()) {
              methodBuilder.addCode(handleControlPermissions());
        } else if (!otherBindings.isEmpty()) {
             methodBuilder.addCode(generationOtherCodeBindingCode(true));
        }
        result.addMethod(methodBuilder.build());

    }


    private CodeBlock handleControlPermissions() {
        boolean hasNextControl = false;
        CodeBlock.Builder coldBlockBuilder = CodeBlock.builder();
        for (PermissionResultBinding binding : permissionBindings) {
            if (hasNextControl) {
                coldBlockBuilder.beginControlFlow("else if($L)",
                        getEqualPermissionsCodeText(binding));
            } else {
                coldBlockBuilder.beginControlFlow("if($L)",
                        getEqualPermissionsCodeText(binding));
            }
            hasNextControl = true;
            coldBlockBuilder.add(invokeCode(binding));
            coldBlockBuilder.endControlFlow();
        }
        if (!otherBindings.isEmpty()) {
            coldBlockBuilder.beginControlFlow("else");
            coldBlockBuilder.add(generationOtherCodeBindingCode(true));
            coldBlockBuilder.endControlFlow();
        }
        return coldBlockBuilder.build();
    }


    private CodeBlock generationOtherCodeBindingCode(boolean isAfter) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (PermissionResultBinding binding : otherBindings) {
            if (binding.isAfter() == isAfter) {
                builder.add(invokeCode(binding));
            }
        }
        return builder.build();
    }

    private CodeBlock invokeCode(PermissionResultBinding binding) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        final List<FullTypeName> params = binding.getParams();
        final int paramSize = params.size();
        final String methodName = binding.getMethodName();

        if (paramSize == 0) {
            codeBlockBuilder.beginControlFlow("if($L.isGranted($L))", Constants.CLASS_CONTROLLER_HELPER, PARAM_NAME_GRANT_RESULTS);
            codeBlockBuilder.addStatement("target.$L()", methodName);
            codeBlockBuilder.endControlFlow();
        } else if (paramSize == 1) {
            FullTypeName typeName = params.get(0);
            if (typeName.isAssignType(TypeKind.INT)) {
                codeBlockBuilder.addStatement("target.$L($L)",
                        methodName, getPermissionResultCodeText());

            } else if (typeName.isArray(TypeKind.INT)) {
                codeBlockBuilder.addStatement("target.$L($L)",
                        methodName, PARAM_NAME_GRANT_RESULTS);
            }
        } else if (paramSize == 3) {
            StringBuilder argumentString = new StringBuilder();
            argumentString.append("requestCode")
                    .append(",")
                    .append(PARAM_NAME_PERMISSION)
                    .append(",");
            FullTypeName lastTypeName = params.get(2);
            if (lastTypeName.isArray(TypeKind.INT)) {
                argumentString.append(PARAM_NAME_GRANT_RESULTS);
            } else {
                argumentString.append(getPermissionResultCodeText());
            }
            codeBlockBuilder.addStatement("target.$L($L)", methodName, argumentString.toString());
        }

        return codeBlockBuilder.build();
    }

    private String getEqualPermissionsCodeText(PermissionResultBinding binding) {
        return CodeBlock.builder()
                .add("$L.isEqualPermissions($L,$L)",
                        Constants.CLASS_CONTROLLER_HELPER,
                        PARAM_NAME_PERMISSION, createFieldName(binding.getMethodName()))
                .build().toString();
    }

    private String getPermissionResultCodeText() {
        return CodeBlock.builder()
                .add("$L.getPermissionResult(target,$L,$L)",
                        Constants.CLASS_CONTROLLER_HELPER,
                        PARAM_NAME_PERMISSION,
                        PARAM_NAME_GRANT_RESULTS).build().toString();
    }

    public void addFields(TypeSpec.Builder builder) {
        List<PermissionResultBinding> permissionResultBindings = new ArrayList<>();
        if (!requestBindings.isEmpty()) {
            permissionResultBindings.addAll(requestBindings);
        }
        if (!permissionBindings.isEmpty()) {
            permissionResultBindings.addAll(permissionBindings);
        }

        if (!permissionResultBindings.isEmpty()) {
            for (PermissionResultBinding binding : permissionResultBindings) {
                String[] permissions = binding.getPermissions();
                if (permissions.length > 0) {
                    StringBuilder codeTextBuilder = new StringBuilder();
                    codeTextBuilder.append("{");
                    for (String permission : permissions) {
                        codeTextBuilder.append("\"").append(permission).append("\"").append(",");
                    }
                    codeTextBuilder.deleteCharAt(codeTextBuilder.length() - 1);
                    codeTextBuilder.append("}");
                    builder.addField(FieldSpec.builder(ArrayTypeName.of(Constants.CLASS_STRING), createFieldName(binding.getMethodName()))
                            .addModifiers(Modifier.PRIVATE)
                            .initializer("$L", codeTextBuilder.toString())
                            .build());
                }

            }
        }

    }

    private String createFieldName(String methodName) {
        return "permission_" + methodName ;
    }
}
