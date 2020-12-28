package cn.luyinbros.valleyframework.controller.binding;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.CompileMessager;
import cn.luyinbros.valleyframework.controller.Constants;
import cn.luyinbros.valleyframework.controller.ControllerDelegateInfo;
import cn.luyinbros.valleyframework.controller.FullTypeName;
import cn.luyinbros.valleyframework.controller.ResId;
import cn.luyinbros.valleyframework.controller.Utils;
import cn.luyinbros.valleyframework.controller.listener.ListenerBinding;
import cn.luyinbros.valleyframework.controller.listener.ListenerBindingProvider;
import cn.luyinbros.valleyframework.controller.listener.ListenerClassInfo;
import cn.luyinbros.valleyframework.controller.listener.ListenerMethodInfo;
import cn.luyinbros.compiler.TypeHelper;
import cn.luyinbros.compiler.TypeNameHelper;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;


public class BindViewProvider {
    private ControllerDelegateInfo controllerDelegateInfo;
    private BuildViewBinding bindViewBinding;
    private List<BuildViewBinding> buildViewBindings;
    private List<ViewFieldBinding> viewFieldBindings = new ArrayList<>();
    private ListenerBindingProvider listenerBindingProvider;

    public BindViewProvider(ControllerDelegateInfo controllerDelegateInfo) {
        this.controllerDelegateInfo = controllerDelegateInfo;
    }

    public void setListenerBindingProvider(ListenerBindingProvider listenerBindingProvider) {
        this.listenerBindingProvider = listenerBindingProvider;
    }


    public boolean isBuildNewView() {
        return bindViewBinding != null || controllerDelegateInfo.getLayoutId() != null;
    }


    public void addBinding(BuildViewBinding binding) {
        if (binding.getReturnClassName() != null) {
            if (bindViewBinding != null) {
                CompileMessager.warn(binding.toString());
            } else {
                bindViewBinding = binding;
            }
        } else {
            if (buildViewBindings == null) {
                buildViewBindings = new ArrayList<>();
            }
            buildViewBindings.add(binding);
        }
    }

    public void addBinding(ViewFieldBinding binding) {
        viewFieldBindings.add(binding);
    }


    public void code(TypeElement typeElement, TypeSpec.Builder result, boolean isParentBuildNewView) {
        if (isParentBuildNewView) {
            if (isBuildNewView()) {
                CompileMessager.error(typeElement, "父类已经构建了视图,无法重复构建");
                return;
            }
        }

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("buildView")
                .addAnnotation(Override.class)
                .addModifiers(PROTECTED)
                .addParameter(Constants.INTERFACE_BUILD_CONTEXT, "buildContext")
                .returns(Constants.CLASS_VIEW);
        ResId layoutId = controllerDelegateInfo.getLayoutId();
        if (isParentBuildNewView) {
            methodBuilder.addStatement("$T view=super.buildView($L)",
                    Constants.CLASS_VIEW,
                    "buildContext");
            methodBuilder.addCode(bindViewCodeBlock(result));
            generationBuildView(methodBuilder);
            methodBuilder.addStatement("return view");
        } else if (bindViewBinding != null) {
            if (layoutId != null) {
                CompileMessager.warn("layoutId: " + layoutId.getCode() + " override");
            }
            if (bindViewBinding.hasParam()) {
                methodBuilder.addStatement("$T view= $T.requireNonNull(target.$L($L))",
                        bindViewBinding.getReturnClassName(),
                        Constants.CLASS_OBJECTS,
                        bindViewBinding.getMethodName(),
                        "buildContext");
            } else {
                methodBuilder.addStatement("$T view= $T.requireNonNull(target.$L())",
                        bindViewBinding.getReturnClassName(),
                        Constants.CLASS_OBJECTS,
                        bindViewBinding.getMethodName());
            }
            methodBuilder.addCode(bindViewCodeBlock(result));
            generationBuildView(methodBuilder);
            methodBuilder.addStatement("return view");
        } else {
            if (layoutId != null) {
                methodBuilder.addStatement("$T view= buildContext.inflate($L)", Constants.CLASS_VIEW, layoutId.getCode());
                methodBuilder.addCode(bindViewCodeBlock(result));
                generationBuildView(methodBuilder);
                methodBuilder.addStatement("return view");
            } else {
                methodBuilder.addStatement("return super.buildView($L)",
                        "buildContext");
            }

        }

        result.addMethod(methodBuilder.build());
    }

    private void generationBuildView(MethodSpec.Builder methodBuilder) {
        if (buildViewBindings != null && !buildViewBindings.isEmpty()) {
            for (BuildViewBinding buildViewBinding : buildViewBindings) {
                StringBuilder argumentString = new StringBuilder();
                for (FullTypeName cls : buildViewBinding.getParamClassNames()) {
                    if (TypeHelper.isSubtypeOfType(cls.getTypeMirror(), Constants.TYPE_VIEW)) {
                        argumentString.append("view").append(",");
                    } else if (TypeHelper.isSubtypeOfType(cls.getTypeMirror(), Constants.TYPE_BUILD_CONTEXT)) {
                        argumentString.append("buildContext").append(",");
                    }
                }
                if (argumentString.length() == 0) {
                    methodBuilder.addStatement("target.$L()", buildViewBinding.getMethodName());
                } else {
                    methodBuilder.addStatement("target.$L($L)", buildViewBinding.getMethodName(), argumentString.substring(0, argumentString.length() - 1));
                }
            }
        }
    }

    public boolean isNeedDispose() {
        return (listenerBindingProvider != null && listenerBindingProvider.getListenerBindings().size() > 0) ||
                viewFieldBindings.size() > 0;
    }

    public CodeBlock dispose() {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        if (listenerBindingProvider != null) {
            List<ListenerBinding> listenerBindings = listenerBindingProvider.getListenerBindings();
            if (listenerBindings.size() > 0) {

                for (ListenerBinding listenerBinding : listenerBindings) {
                    ListenerClassInfo listenerClassInfo = listenerBinding.getListenerClassInfo();
                    String targetType = listenerClassInfo.getTargetType();
                    String remover = listenerClassInfo.getRemover();
                    String setter = listenerClassInfo.getSetter();

                    for (ResId id : listenerBinding.getIds()) {

                        ViewFieldBinding viewFieldBinding = find(id);

                        if (!listenerBinding.isRequired()) {
                            if (viewFieldBinding != null) {
                                codeBlockBuilder.beginControlFlow("if(target.$L!=null)", viewFieldBinding.getFieldName());
                            } else {
                                codeBlockBuilder.beginControlFlow("if($L!=null)", getListenerIdFiledName(id));
                            }
                        }


                        CodeBlock listenerCodeBlock;
                        CodeBlock viewCodeBlock;

                        if (viewFieldBinding != null) {
                            viewCodeBlock = CodeBlock.builder()
                                    .add("target.$L", viewFieldBinding.getFieldName())
                                    .build();
                        } else {
                            viewCodeBlock = CodeBlock.builder()
                                    .add("(($L)$L)",
                                            targetType,
                                            getListenerIdFiledName(id))
                                    .build();
                        }
                        if (remover.isEmpty()) {
                            listenerCodeBlock = CodeBlock.builder().add("$L(null)", setter).build();
                        } else {
                            String fieldName = getListenerFieldName(listenerBinding);
                            listenerCodeBlock = CodeBlock.builder().add("$L($L)", remover, fieldName).build();
                        }


                        codeBlockBuilder.addStatement("$L.$L",
                                viewCodeBlock,
                                listenerCodeBlock);

                        if (!listenerBinding.isRequired()) {
                            codeBlockBuilder.endControlFlow();
                        }
                    }

                }

                {
                    HashSet<ResId> hashSet = new HashSet<>();
                    for (ListenerBinding listenerBinding : listenerBindings) {
                        for (ResId resId : listenerBinding.getIds()) {
                            if (find(resId) == null) {
                                hashSet.add(resId);
                            }
                        }
                    }

                    for (ResId resId : hashSet) {
                        String fieldName = getListenerIdFiledName(resId);
                        codeBlockBuilder.addStatement("$L=null", getListenerIdFiledName(resId));
                    }
                }
            }

        }

        for (ViewFieldBinding binding : viewFieldBindings) {
            codeBlockBuilder.addStatement("target.$L=null",
                    binding.getFieldName());
        }


        return codeBlockBuilder.build();
    }

    private CodeBlock bindViewCodeBlock(TypeSpec.Builder result) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (ViewFieldBinding binding : viewFieldBindings) {
            if (binding.isRequired()) {
                builder.addStatement("target.$L=$L.requiredViewById(view,$L)",
                        binding.getFieldName(),
                        Constants.CLASS_CONTROLLER_HELPER,
                        binding.getResId().getCode());
            } else {
                builder.addStatement("target.$L=$L.findViewById(view,$L)",
                        binding.getFieldName(),
                        Constants.CLASS_CONTROLLER_HELPER,
                        binding.getResId().getCode());
            }

        }
        if (listenerBindingProvider != null) {
            List<ListenerBinding> listenerBindings = listenerBindingProvider.getListenerBindings();
            if (listenerBindings.size() > 0) {


                {
                    HashSet<ResId> hashSet = new HashSet<>();
                    for (ListenerBinding listenerBinding : listenerBindings) {
                        for (ResId resId : listenerBinding.getIds()) {
                            if (find(resId) == null) {
                                hashSet.add(resId);
                            }
                        }
                    }

                    for (ResId resId : hashSet) {
                        String fieldName = getListenerIdFiledName(resId);
                        result.addField(Constants.CLASS_VIEW, getListenerIdFiledName(resId), PRIVATE);
                        builder.addStatement("$L=$L.requiredViewById(view,$L)", fieldName, Constants.CLASS_CONTROLLER_HELPER, resId.getCode());
                    }
                }


                for (ListenerBinding listenerBinding : listenerBindings) {
                    ListenerClassInfo listenerClassInfo = listenerBinding.getListenerClassInfo();
                    String targetType = listenerClassInfo.getTargetType();
                    String remover = listenerClassInfo.getRemover();
                    String setter = listenerClassInfo.getSetter();

                    for (ResId id : listenerBinding.getIds()) {
                        CodeBlock listenerCodeBlock;

                        if (remover.isEmpty()) {
                            listenerCodeBlock = generateListenerTypeSpec(result, listenerBinding);
                        } else {
                            String fieldName = getListenerFieldName(listenerBinding);
                            result.addField(ClassName.bestGuess(listenerBinding.getListenerClassInfo().getType()),
                                    fieldName, PRIVATE);
                            builder.addStatement("$L=$L", fieldName, generateListenerTypeSpec(result, listenerBinding));
                            listenerCodeBlock = CodeBlock.builder().add(fieldName).build();
                        }


                        {
                            CodeBlock viewCodeBlock;

                            ViewFieldBinding viewFieldBinding = find(id);
                            if (viewFieldBinding != null) {
                                if (viewFieldBinding.isRequired() != listenerBinding.isRequired()) {
                                    CompileMessager.error(viewFieldBinding.getElement(), listenerBinding.getMethodName() +
                                            " is option." +
                                            "but id:" +
                                            viewFieldBinding.getResId().getCode() +
                                            "is not option");
                                }
                                viewCodeBlock = CodeBlock.builder()
                                        .add("target.$L", viewFieldBinding.getFieldName())
                                        .build();
                            } else {
                                viewCodeBlock = CodeBlock.builder()
                                        .add("(($L)$L)",
                                                targetType,
                                                getListenerIdFiledName(id))
                                        .build();
                            }

                            if (listenerBinding.isRequired()) {
                                builder.addStatement("$L.$L($L)",
                                        viewCodeBlock,
                                        setter,
                                        listenerCodeBlock);
                            } else {
                                if (viewFieldBinding != null) {

                                    builder.beginControlFlow("if(target.$L!=null)", viewFieldBinding.getFieldName());
                                } else {
                                    builder.beginControlFlow("if($L!=null)", getListenerIdFiledName(id));

                                }
                                builder.addStatement("$L.$L($L)",
                                        viewCodeBlock,
                                        setter,
                                        listenerCodeBlock);
                                builder.endControlFlow();
                            }


                        }

                    }


                }
            }
        }


        return builder.build();
    }


    private ViewFieldBinding find(ResId id) {
        for (ViewFieldBinding binding : viewFieldBindings) {
            if (binding.getResId().equals(id)) {
                return binding;
            }
        }
        return null;
    }

    private CodeBlock generateListenerTypeSpec(TypeSpec.Builder rootTypeSpecBuilder, ListenerBinding listenerBinding) {
        ListenerClassInfo listenerClassInfo = listenerBinding.getListenerClassInfo();
        String type = listenerClassInfo.getType();
        if (TypeHelper.isSubtypeOfType(listenerBinding.getReturnTypeMirror(), type)) {
            return CodeBlock.builder()
                    .add("target.$L()", listenerBinding.getMethodName())
                    .build();
        } else {
            TypeSpec.Builder listenerTypeSpecBuilder = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ClassName.bestGuess(type));
            for (ListenerMethodInfo methodInfo : listenerClassInfo.getMethodInfoList()) {
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodInfo.getName())
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .returns(Utils.getTypeName(methodInfo.getReturnType()));

                for (int index = 0; index < methodInfo.getParameters().length; index++) {
                    methodBuilder.addParameter(TypeNameHelper.get(methodInfo.getParameters()[index]), "arg" + index);
                }

                boolean isReturn = false;

                if (methodInfo == listenerBinding.getListenerMethodInfo()) {
                    if (listenerBinding.isHasArguments()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int index = 0; index < methodInfo.getParameters().length; index++) {
                            TypeMirror typeMirror = listenerBinding.getArgumentTypeMirrors().get(index);
                            if (!typeMirror.getKind().isPrimitive()) {
                                stringBuilder.append("(").append(ClassName.get(typeMirror).toString()).append(")");
                            }
                            stringBuilder.append("arg").append(index).append(",");

                        }

                        if (listenerBinding.getReturnTypeMirror().toString().equals("void")) {
                            methodBuilder.addStatement("target.$L($L)", listenerBinding.getMethodName(), stringBuilder.subSequence(0, stringBuilder.length() - 1));
                        } else {
                            isReturn = true;
                            methodBuilder.addStatement("return target.$L($L)", listenerBinding.getMethodName(), stringBuilder.subSequence(0, stringBuilder.length() - 1));
                        }

                    } else {
                        if (listenerBinding.getReturnTypeMirror().toString().equals("void")) {
                            methodBuilder.addStatement("target.$L()", listenerBinding.getMethodName());
                        } else {
                            isReturn = true;
                            methodBuilder.addStatement("return target.$L()", listenerBinding.getMethodName());
                        }

                    }

                }

                if (!isReturn && !methodInfo.getReturnType().equals("void")) {
                    methodBuilder.addStatement("return $L", methodInfo.getDefaultReturn());
                }

                listenerTypeSpecBuilder.addMethod(methodBuilder.build());
            }

            return CodeBlock.builder().add("$L", listenerTypeSpecBuilder.build()).build();
        }


    }

    private String getListenerFieldName(ListenerBinding listenerBinding) {
        return listenerBinding.getMethodName() + "_listener";
    }

    private String getListenerIdFiledName(ResId resId) {
        return "view" + resId.getId();
    }

}
