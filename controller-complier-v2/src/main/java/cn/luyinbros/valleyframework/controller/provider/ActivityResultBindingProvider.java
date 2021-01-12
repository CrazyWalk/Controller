package cn.luyinbros.valleyframework.controller.provider;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import cn.luyinbros.valleyframework.controller.Constants;
import cn.luyinbros.valleyframework.controller.binding.ActivityResultBinding;

import static javax.lang.model.element.Modifier.PUBLIC;

public class ActivityResultBindingProvider {
    private List<ActivityResultBinding> requestCodeBindings = new ArrayList<>();
    private List<ActivityResultBinding> resultCodeBindings = new ArrayList<>();
    private List<ActivityResultBinding> otherBindings = new ArrayList<>();
    private static final String PARAM_REQUEST_CODE = "requestCode";
    private static final String PARAM_RESULT_CODE = "resultCode";
    private static final String PARAM_INTENT = "data";

    public boolean isEmpty() {
        return requestCodeBindings.isEmpty() && resultCodeBindings.isEmpty() && otherBindings.isEmpty();
    }

    public void addBinding(ActivityResultBinding binding) {
        int currentRequestCode = binding.getRequestCode();
        if (currentRequestCode >= ActivityResultBinding.ALLOW_EXCLUDE_MIN_REQUEST_CODE) {
            requestCodeBindings.add(binding);
            return;
        }

        int currentResultCode = binding.getResultCode();
        if (currentResultCode >= ActivityResultBinding.ALLOW_EXCLUDE_MIN_RESULT_CODE) {
            resultCodeBindings.add(binding);
            return;
        }

        if (otherBindings == null) {
            otherBindings = new ArrayList<>();
        }
        otherBindings.add(binding);

    }


    public void code(TypeSpec.Builder result) {
        //  CompileMessager.note(requestCodeBindings + "   " + resultCodeBindings + " " + " " + otherBindings);
        if (requestCodeBindings.isEmpty() &&
                resultCodeBindings.isEmpty() &&
                otherBindings.isEmpty()) {
            return;
        }
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("onActivityResult")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.INT, PARAM_REQUEST_CODE)
                .addParameter(TypeName.INT, PARAM_RESULT_CODE)
                .addParameter(Constants.CLASS_INTENT, PARAM_INTENT)
                .returns(ClassName.VOID);

        methodBuilder.addStatement("super.onActivityResult(requestCode,resultCode,data)");


        methodBuilder.addCode(generationOtherCodeBindingCode(false));

        if (!requestCodeBindings.isEmpty()) {
            CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
            boolean hasRequestNextController = false;
            for (ActivityResultBinding binding : requestCodeBindings) {
                if (hasRequestNextController) {
                    codeBlockBuilder.beginControlFlow("else if($L == $L)",
                            PARAM_REQUEST_CODE,
                            binding.getRequestCode());
                } else {
                    codeBlockBuilder.beginControlFlow("if($L == $L)",
                            PARAM_REQUEST_CODE,
                            binding.getRequestCode());
                }


                if (binding.getResultCode() >= ActivityResultBinding.ALLOW_EXCLUDE_MIN_RESULT_CODE) {
                    codeBlockBuilder.beginControlFlow("if($L == $L)",
                            PARAM_RESULT_CODE,
                            binding.getResultCode());
                    codeBlockBuilder.add(invokeCode(binding));
                    codeBlockBuilder.endControlFlow();
                } else {
                    codeBlockBuilder.add(invokeCode(binding));
                }
                codeBlockBuilder.endControlFlow();
                hasRequestNextController = true;
            }

            if (!resultCodeBindings.isEmpty()) {
                codeBlockBuilder.beginControlFlow("else");
                codeBlockBuilder.add(handleResultCodeBindingCode().build());
                codeBlockBuilder.endControlFlow();
            } else if (!otherBindings.isEmpty()) {
                codeBlockBuilder.beginControlFlow("else");
                codeBlockBuilder.add(generationOtherCodeBindingCode(true));
                codeBlockBuilder.endControlFlow();
            }
            methodBuilder.addCode(codeBlockBuilder.build());
        } else if (!resultCodeBindings.isEmpty()) {
            methodBuilder.addCode(handleResultCodeBindingCode().build());
        } else if (!otherBindings.isEmpty()) {
            methodBuilder.addCode(generationOtherCodeBindingCode(true));
        }
        result.addMethod(methodBuilder.build());
    }

    private CodeBlock.Builder handleResultCodeBindingCode() {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        boolean hasNextController = false;
        for (ActivityResultBinding activityResultBinding : resultCodeBindings) {
            if (hasNextController) {
                codeBlockBuilder.beginControlFlow("else if(resultCode == $L)", activityResultBinding.getResultCode());
            } else {
                codeBlockBuilder.beginControlFlow("if(resultCode == $L)", activityResultBinding.getResultCode());
            }
            codeBlockBuilder.add(invokeCode(activityResultBinding));
            codeBlockBuilder.endControlFlow();
            hasNextController = true;
        }
        if (!otherBindings.isEmpty()) {
            codeBlockBuilder.beginControlFlow("else");
            codeBlockBuilder.add(generationOtherCodeBindingCode(true));
            codeBlockBuilder.endControlFlow();
        }
        return codeBlockBuilder;
    }

    private CodeBlock generationOtherCodeBindingCode(boolean isAfter) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (ActivityResultBinding activityResultBinding : otherBindings) {
            if (activityResultBinding.isAfter() == isAfter) {
                builder.add(invokeCode(activityResultBinding));
            }
        }
        return builder.build();
    }

    private CodeBlock invokeCode(ActivityResultBinding activityResultBinding) {
        CodeBlock.Builder innerInvokeBuilder = CodeBlock.builder();

        final String methodName = activityResultBinding.getMethodName();
        final int paramSize = activityResultBinding.getParams().size();
        if (paramSize == 0) {
            innerInvokeBuilder.addStatement("target.$L()", methodName);
        } else if (paramSize == 1) {
            TypeName typeName = activityResultBinding.getParams().get(0);
            if (typeName.equals(TypeName.INT)) {
                innerInvokeBuilder.addStatement("target.$L(resultCode)", methodName);
            } else {
                innerInvokeBuilder.addStatement("target.$L(data)", methodName);
            }
        } else if (paramSize == 2) {
            StringBuilder argumentString = new StringBuilder();
            for (TypeName typeName : activityResultBinding.getParams()) {
                if (typeName == TypeName.INT) {
                    argumentString.append("resultCode").append(",");
                } else {
                    argumentString.append("data").append(",");
                }
            }

            innerInvokeBuilder.addStatement("target.$L($L)", methodName, argumentString.substring(0, argumentString.length() - 1));
        } else if (paramSize == 3) {
            innerInvokeBuilder.addStatement("target.$L(requestCode,resultCode,data)", methodName);
        }

        if (activityResultBinding.isRequiredNonNullIntent()) {
            return CodeBlock.builder()
                    .beginControlFlow("if(data!=null)")
                    .add(innerInvokeBuilder.build())
                    .endControlFlow()
                    .build();
        } else {
            return innerInvokeBuilder.build();

        }
    }
}
