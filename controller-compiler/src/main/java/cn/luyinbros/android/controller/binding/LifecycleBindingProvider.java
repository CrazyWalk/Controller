package cn.luyinbros.android.controller.binding;

import androidx.lifecycle.Lifecycle;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import cn.luyinbros.android.controller.Constants;

import static javax.lang.model.element.Modifier.PUBLIC;

public class LifecycleBindingProvider {
    private List<LifecycleBinding> allBindings=new ArrayList<>();
    private List<LifecycleBinding> onCreateBindings=new ArrayList<>();
    private List<LifecycleBinding> onStartBindings=new ArrayList<>();
    private List<LifecycleBinding> onResumeBindings=new ArrayList<>();
    private List<LifecycleBinding> onPauseBindings=new ArrayList<>();
    private List<LifecycleBinding> onStopBindings=new ArrayList<>();
    private List<LifecycleBinding> onDestroyBindings=new ArrayList<>();
    private List<LifecycleBinding> onAnyBindings=new ArrayList<>();


    public void addBinding(LifecycleBinding binding) {
        Lifecycle.Event event = binding.getState();
        if (event == Lifecycle.Event.ON_CREATE) {
            onCreateBindings.add(binding);
        } else if (event == Lifecycle.Event.ON_START) {
            onStartBindings.add(binding);
        } else if (event == Lifecycle.Event.ON_RESUME) {
            onResumeBindings.add(binding);
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            onPauseBindings.add(binding);
        } else if (event == Lifecycle.Event.ON_STOP) {
            onStopBindings.add(binding);
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            onDestroyBindings.add(binding);
        } else if (event == Lifecycle.Event.ON_ANY) {
            onAnyBindings.add(binding);
        }
        allBindings.add(binding);
    }

    public boolean isEmpty() {
        return allBindings == null || allBindings.isEmpty();
    }

    public void code(TypeSpec.Builder result) {
        if (isEmpty()){
            return;
        }
        CodeBlock.Builder builder = CodeBlock.builder();
        boolean hasNext = false;

        if (!onCreateBindings.isEmpty()) {
            builder.beginControlFlow("if(state==$T.ON_CREATE)", Constants.ENUM_LIFECYCLE_EVENT);
            for (LifecycleBinding binding : onCreateBindings) {
                addStatement(builder, binding);
            }
            builder.endControlFlow();
            hasNext = true;
        }

        if (!onStartBindings.isEmpty()) {
            if (hasNext){
                builder.beginControlFlow("else if(state==$T.ON_START)", Constants.ENUM_LIFECYCLE_EVENT);
            }else{
                builder.beginControlFlow("if(state==$T.ON_START)", Constants.ENUM_LIFECYCLE_EVENT);
            }
            for (LifecycleBinding binding : onStartBindings) {
                addStatement(builder, binding);
            }
            builder.endControlFlow();
            hasNext = true;
        }
        if (!onResumeBindings.isEmpty()) {
            if (hasNext){
                builder.beginControlFlow("else if(state==$T.ON_RESUME)", Constants.ENUM_LIFECYCLE_EVENT);
            }else{
                builder.beginControlFlow("if(state==$T.ON_RESUME)", Constants.ENUM_LIFECYCLE_EVENT);
            }
            for (LifecycleBinding binding : onResumeBindings) {
                addStatement(builder, binding);
            }
            builder.endControlFlow();
            hasNext = true;
        }
        if (!onPauseBindings.isEmpty()) {
            if (hasNext){
                builder.beginControlFlow("else if(state==$T.ON_PAUSE)", Constants.ENUM_LIFECYCLE_EVENT);
            }else{
                builder.beginControlFlow("if(state==$T.ON_PAUSE)", Constants.ENUM_LIFECYCLE_EVENT);
            }
            for (LifecycleBinding binding : onPauseBindings) {
                addStatement(builder, binding);
            }
            builder.endControlFlow();
            hasNext = true;
        }
        if (!onStopBindings.isEmpty()) {
            if (hasNext){
                builder.beginControlFlow("else if(state==$T.ON_STOP)", Constants.ENUM_LIFECYCLE_EVENT);
            }else{
                builder.beginControlFlow("if(state==$T.ON_STOP)", Constants.ENUM_LIFECYCLE_EVENT);
            }
            for (LifecycleBinding binding : onStopBindings) {
                addStatement(builder, binding);
            }
            builder.endControlFlow();
            hasNext = true;
        }
        if (!onDestroyBindings.isEmpty()) {
            if (hasNext){
                builder.beginControlFlow("else if(state==$T.ON_DESTROY)", Constants.ENUM_LIFECYCLE_EVENT);
            }else{
                builder.beginControlFlow("if(state==$T.ON_DESTROY)", Constants.ENUM_LIFECYCLE_EVENT);
            }
            for (LifecycleBinding binding : onDestroyBindings) {
                addStatement(builder, binding);
            }
            builder.endControlFlow();
            hasNext = true;
        }
        if (!onAnyBindings.isEmpty()) {
            if (hasNext){
                builder.beginControlFlow("else", Constants.ENUM_LIFECYCLE_EVENT);
            }
            for (LifecycleBinding binding : onAnyBindings) {
                addStatement(builder, binding);
            }
            builder.endControlFlow();
            hasNext = true;
        }

        addFields(result);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("didChangeAppLifecycleState")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(Constants.ENUM_LIFECYCLE_EVENT, "state")
                .returns(ClassName.VOID);
        methodBuilder.addStatement("super.didChangeAppLifecycleState(state)");
        methodBuilder.addCode(builder.build());

        result.addMethod(methodBuilder.build());

    }

    private void addStatement(CodeBlock.Builder builder, LifecycleBinding binding) {
        final int count = binding.getCount();

        if (count >= 1) {
            builder.beginControlFlow("if($L<$L)", createFieldName(binding.getMethodName()), count);
            if (binding.getArgumentClassName() != null) {
                builder.addStatement("target.$L(state)", binding.getMethodName());
            } else {
                builder.addStatement("target.$L()", binding.getMethodName());
            }
            builder.addStatement("$L++", createFieldName(binding.getMethodName()));
            builder.endControlFlow();
        } else {
            if (binding.getArgumentClassName() != null) {
                builder.addStatement("target.$L(state)", binding.getMethodName());
            } else {
                builder.addStatement("target.$L()", binding.getMethodName());
            }
        }

    }

    public void addFields(TypeSpec.Builder builder) {
        if (allBindings != null) {
            for (LifecycleBinding binding : allBindings) {
                if (binding.getCount() >= 1) {
                    builder.addField(FieldSpec.builder(TypeName.INT, createFieldName(binding.getMethodName()))
                            .addModifiers(Modifier.PRIVATE)
                            .initializer("$L", 0)
                            .build());
                }
            }
        }

    }

    private String createFieldName(String methodName) {
        return "lifecycle_" + methodName + "_count";
    }
}
