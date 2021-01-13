package cn.luyinbros.valleyframework.controller;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.annotation.BundleValue;
import cn.luyinbros.valleyframework.controller.annotation.DidChangeLifecycleEvent;
import cn.luyinbros.valleyframework.controller.annotation.LiveOB;
import cn.luyinbros.valleyframework.controller.annotation.OnActivityResult;
import cn.luyinbros.valleyframework.controller.annotation.OnPermissionResult;
import cn.luyinbros.valleyframework.controller.binding.ActivityResultBinding;
import cn.luyinbros.valleyframework.controller.binding.BindingResult;
import cn.luyinbros.valleyframework.controller.binding.BuildViewBinding;
import cn.luyinbros.valleyframework.controller.binding.BundleValueBinding;
import cn.luyinbros.valleyframework.controller.binding.ControllerBinding;
import cn.luyinbros.valleyframework.controller.binding.DisposeBinding;
import cn.luyinbros.valleyframework.controller.binding.InitStateBinding;
import cn.luyinbros.valleyframework.controller.binding.LifecycleBinding;
import cn.luyinbros.valleyframework.controller.binding.LiveOBBinding;
import cn.luyinbros.valleyframework.controller.binding.PermissionResultBinding;
import cn.luyinbros.valleyframework.controller.binding.ViewFieldBinding;

import static javax.lang.model.element.Modifier.FINAL;

/**
 * 绑定工厂
 */
public class BindingFactory {

    public static BindingResult<ControllerBinding> createControllerBinding(TypeElement enclosingElement,
                                                                           ResId layoutRes) {
        final TypeMirror typeMirror = enclosingElement.asType();
        TypeName targetType = TypeName.get(typeMirror);
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }
        ControllerBinding binding = new ControllerBinding();
        binding.setGenerationClassName(getDelegateClassName(enclosingElement));
        binding.setControllerTypeName(targetType);
        binding.setFinal(enclosingElement.getModifiers().contains(FINAL));
        binding.setLayoutId(layoutRes);
        binding.setTypeElement(enclosingElement);
        if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_ACTIVITY)) {
            binding.setControllerType(ControllerType.ACTIVITY);
        } else if (TypeHelper.isSubtypeOfType(typeMirror, Constants.TYPE_FRAGMENT)) {
            binding.setControllerType(ControllerType.FRAGMENT);
        }
        return BindingResult.createBindResult(binding);
    }

    public static BindingResult<BundleValueBinding> createBundleValueBinding(Element element) {
        BundleValueBinding binding = new BundleValueBinding();
        VariableElement variableElement = ElementHelper.asVariable(element);
        BundleValue bundleValue = variableElement.getAnnotation(BundleValue.class);
        binding.setKey(bundleValue.value());
        binding.setFiledName(variableElement.getSimpleName().toString());
        binding.setTypeMirror(variableElement.asType());
        binding.setRequired(Utils.isNotNullElement(element));
        return BindingResult.createBindResult(binding);
    }

    public static BindingResult<ViewFieldBinding> createViewFieldBinding(Element element, ResId resId) {
        VariableElement variableElement = ElementHelper.asVariable(element);
        ViewFieldBinding binding = new ViewFieldBinding();
        binding.setResId(resId);
        binding.setFieldName(variableElement.getSimpleName().toString());
        binding.setRequired(Utils.isNotNullElement(element));
        binding.setElement(element);
        return BindingResult.createBindResult(binding);
    }

    public static BindingResult<InitStateBinding> createInitStatBinding(Element element) {

        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        final TypeMirror returnTypeMirror = executableElement.getReturnType();
        if (returnTypeMirror.getKind() != TypeKind.VOID) {
            return BindingResult.createErrorResult(element, "return type must void");
        }
        ClassName argumentClassName = null;
        final List<? extends VariableElement> methodParameters = executableElement.getParameters();
        int size = methodParameters.size();
        if (size == 1) {
            TypeMirror mirror = methodParameters.get(0).asType();
            if (TypeHelper.isTypeEqual(mirror, Constants.TYPE_BUILD_CONTEXT)) {
                argumentClassName = TypeNameHelper.get(mirror);
            } else {
                return BindingResult.createErrorResult(element, "parameter must " + Constants.TYPE_BUILD_CONTEXT);
            }
        } else if (size > 1) {
            return BindingResult.createErrorResult(element, "parameters size <2");
        }
        return BindingResult.createBindResult(new InitStateBinding(executableElement.getSimpleName().toString(), argumentClassName));

    }

    public static BindingResult<LiveOBBinding> createLiveOBBinding(Element element) {

        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        final TypeMirror returnTypeMirror = executableElement.getReturnType();
        if (returnTypeMirror.getKind() != TypeKind.VOID) {
            return BindingResult.createErrorResult(element, "return type must void");
        }
        ClassName argumentClassName = null;
        final List<? extends VariableElement> methodParameters = executableElement.getParameters();
        int size = methodParameters.size();
        if (size == 1) {
            TypeMirror mirror = methodParameters.get(0).asType();
            argumentClassName = TypeNameHelper.get(mirror);
        } else if (size > 1) {
            return BindingResult.createErrorResult(element, "parameters size <2");
        }

        final LiveOBBinding binding = new LiveOBBinding();
        binding.setMethodName(executableElement.getSimpleName().toString());
        binding.setParamClassName(argumentClassName);
        final LiveOB liveOB = executableElement.getAnnotation(LiveOB.class);
        binding.setForever(liveOB.forever());

        {
            final String[] bindArray = liveOB.value();
            if (bindArray.length == 0) {
                binding.setMap(Collections.emptyMap());
            } else {
                Map<String, String> data = new HashMap<>();
                String[] result;
                for (String s : bindArray) {
                    result = s.split("[:]");
                    if (result.length != 2) {
                        return BindingResult.createErrorResult(element, "target: " + s + " format error. example->viewModel:livedata");
                    } else {
                        data.put(result[0], result[1]);
                    }
                }
                binding.setMap(data);
            }
        }
        return BindingResult.createBindResult(binding);

    }


    public static BindingResult<LifecycleBinding> createLifecycleBinding(Element element) {
        ExecutableElement executableElement = ElementHelper.asExecutable(element);
        TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            return BindingResult.createErrorResult(element, "return type must void");
        }

        ClassName argumentClassName = null;
        final List<? extends VariableElement> methodParameters = executableElement.getParameters();
        int size = methodParameters.size();
        if (size == 1) {
            TypeMirror mirror = methodParameters.get(0).asType();
            if (TypeHelper.isTypeEqual(mirror, Constants.TYPE_LIFECYCLE_EVENT)) {
                argumentClassName = TypeNameHelper.get(mirror);
            } else {
                return BindingResult.createErrorResult(element, "parameter must " + Constants.TYPE_LIFECYCLE_EVENT);
            }
        } else if (size > 1) {
            return BindingResult.createErrorResult(element, "parameters size <2");
        }

        DidChangeLifecycleEvent state = executableElement.getAnnotation(DidChangeLifecycleEvent.class);
        final String methodName = executableElement.getSimpleName().toString();
        return BindingResult.createBindResult(new LifecycleBinding(methodName,
                argumentClassName,
                state.value(),
                state.count()));
    }

    public static BindingResult<ActivityResultBinding> createActivityResultBinding(Element element) {
        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        final TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            return BindingResult.createErrorResult(element, "return type must void");
        }

        final List<? extends VariableElement> parameters = executableElement.getParameters();
        final int parametersSize = parameters.size();
        OnActivityResult onActivityResult = executableElement.getAnnotation(OnActivityResult.class);

        final int requestCode = onActivityResult.value();
        final int resultCode = onActivityResult.resultCode();

        ActivityResultBinding binding = new ActivityResultBinding();
        binding.setRequestCode(requestCode);
        binding.setResultCode(resultCode);
        binding.setRequiredNonNullIntent(true);
        binding.setParams(new ArrayList<>());
        binding.setMethodName(executableElement.getSimpleName().toString());
        binding.setAfter(onActivityResult.after());

        if (parametersSize == 0) {
            binding.setRequiredNonNullIntent(false);
        } else if (parametersSize == 1) {
            VariableElement variableElement = parameters.get(0);
            TypeMirror typeMirror = variableElement.asType();
            if (typeMirror.getKind() == TypeKind.INT) {
                binding.getParams().add(TypeName.INT);
                binding.setRequiredNonNullIntent(false);
            } else if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_INTENT)) {
                binding.getParams().add(Constants.CLASS_INTENT);
                binding.setRequiredNonNullIntent(Utils.isNotNullElement(variableElement));
            } else {
                return BindingResult.createErrorResult(element, "param size =1 .only support int or intent");
            }
        } else if (parametersSize == 2) {
            if (Utils.isMatchTwoNoOrderParameters(parameters, TypeKind.INT, Constants.TYPE_INTENT)) {
                for (VariableElement variableElement : parameters) {
                    if (TypeHelper.isTypeEqual(variableElement.asType(), Constants.TYPE_INTENT)) {
                        binding.setRequiredNonNullIntent(Utils.isNotNullElement(variableElement));
                    }
                    binding.getParams().add(TypeName.get(variableElement.asType()));
                }
            } else {
                return BindingResult.createErrorResult(element, "param size =2 .only support order (int,Intent)");
            }
        } else if (parametersSize == 3) {
            if (Utils.isStrictTypeParameters(parameters,
                    TypeKind.INT,
                    TypeKind.INT,
                    Constants.TYPE_INTENT)) {
                for (VariableElement variableElement : parameters) {
                    binding.getParams().add(TypeName.get(variableElement.asType()));
                }
                binding.setRequiredNonNullIntent(Utils.isNotNullElement(parameters.get(2)));
            } else {
                return BindingResult.createErrorResult(element, "param size =2 .only support order (int, int,Intent)");
            }
        }

        return BindingResult.createBindResult(binding);
    }


    public static BindingResult<PermissionResultBinding> createPermissionResult(Element element) {
        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        final TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            return BindingResult.createErrorResult(element, "return type must void");
        }

        final List<? extends VariableElement> parameters = executableElement.getParameters();
        final int parametersSize = parameters.size();
        OnPermissionResult onPermissionResult = executableElement.getAnnotation(OnPermissionResult.class);
        PermissionResultBinding binding = new PermissionResultBinding();
        binding.setRequestCode(onPermissionResult.value());
        binding.setParams(new ArrayList<>());
        binding.setMethodName(executableElement.getSimpleName().toString());
        binding.setAfter(onPermissionResult.after());
        binding.setPermissions(onPermissionResult.permissions());

        if (parametersSize == 0) {
            //ignore
        } else if (parametersSize == 1) {
            VariableElement variableElement = parameters.get(0);
            TypeMirror typeMirror = variableElement.asType();
            if (typeMirror.getKind() == TypeKind.INT) {
                binding.getParams().add(new FullTypeName(TypeName.INT, typeMirror));
            } else if (TypeHelper.isArray(typeMirror, TypeKind.INT)) {
                binding.getParams().add(new FullTypeName(TypeName.get(typeMirror), typeMirror));
            } else {
                return BindingResult.createErrorResult(element, "param size =1 .only support int or int[]");
            }
        } else if (parametersSize == 3) {
            if (Utils.isStrictTypeParameters(parameters, TypeKind.INT, "java.lang.String[]", TypeKind.INT) ||
                    Utils.isStrictTypeParameters(parameters, TypeKind.INT, "java.lang.String[]", "int[]")) {
                for (VariableElement variableElement : parameters) {
                    TypeMirror typeMirror = variableElement.asType();
                    binding.getParams().add(new FullTypeName(TypeName.get(typeMirror), typeMirror));
                }
            } else {
                return BindingResult.createErrorResult(element, "param size =3 .only support order(int,String[],int) or order(int,String[],int[])");
            }
        } else {
            return BindingResult.createErrorResult(element, "invalidate");
        }

        return BindingResult.createBindResult(binding);
    }

    public static BindingResult<DisposeBinding> createDisposeBinding(Element element) {
        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        final TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            return BindingResult.createErrorResult(element, "return must be void");
        }
        if (!executableElement.getParameters().isEmpty()) {
            return BindingResult.createErrorResult(element, "parameters must empty");
        }
        return BindingResult.createBindResult(new DisposeBinding(executableElement.getSimpleName().toString()));
    }


    public static BindingResult<BuildViewBinding> createBuildView(Element element) {

        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        final TypeMirror returnType = executableElement.getReturnType();

        if (!(returnType.getKind() == TypeKind.VOID || returnType.getKind() == TypeKind.DECLARED)) {
            return BindingResult.createErrorResult(element, "return type must void or Object");
        }


        ClassName returnClassName = null;
        List<FullTypeName> argumentClassNames = null;

        if (returnType.getKind() == TypeKind.VOID) {
            final List<? extends VariableElement> methodParameters = executableElement.getParameters();
            if (methodParameters.size() <= 2) {
                argumentClassNames = new ArrayList<>();

                boolean foundBuildContext = false;
                boolean foundView = false;
                for (VariableElement variableElement : methodParameters) {
                    TypeMirror mirror = variableElement.asType();
                    if (TypeHelper.isTypeEqual(mirror, Constants.TYPE_BUILD_CONTEXT)) {
                        if (foundBuildContext) {
                            return BindingResult.createErrorResult(element, "two buildContext");
                        }
                        argumentClassNames.add(new FullTypeName(TypeNameHelper.get(mirror), mirror));
                        foundBuildContext = true;

                    } else if (TypeHelper.isSubtypeOfType(mirror, Constants.TYPE_VIEW)) {
                        if (foundView) {
                            return BindingResult.createErrorResult(element, "two View");
                        }
                        argumentClassNames.add(new FullTypeName(TypeNameHelper.get(mirror), mirror));
                        foundView = true;
                    } else {
                        return BindingResult.createErrorResult(element, "not support argument");
                    }
                }
            } else {
                return BindingResult.createErrorResult(element, "not support argument");
            }
        } else if (returnType.getKind() == TypeKind.DECLARED) {
            if (TypeHelper.isSubtypeOfType(returnType, Constants.TYPE_VIEW)) {
                returnClassName = TypeNameHelper.get(returnType);
                final List<? extends VariableElement> methodParameters = executableElement.getParameters();
                if (methodParameters.size() == 0) {
                    argumentClassNames = new ArrayList<>();
                } else if (methodParameters.size() == 1) {
                    VariableElement variableElement = methodParameters.get(0);
                    TypeMirror mirror = variableElement.asType();
                    if (TypeHelper.isTypeEqual(mirror, Constants.TYPE_BUILD_CONTEXT)) {
                        argumentClassNames = ImmutableList.of(new FullTypeName(TypeNameHelper.get(mirror), mirror));
                    }
                }
            } else {
                return BindingResult.createErrorResult(element, "not support argument");
            }
        }

        final String methodName = executableElement.getSimpleName().toString();
        return BindingResult.createBindResult(new BuildViewBinding(methodName,
                argumentClassNames, returnClassName));

    }

    private static ClassName getDelegateClassName(TypeElement typeElement) {
        String packageName = Utils.getPackageName(typeElement);
        String className = typeElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');
        return ClassName.get(packageName, className + "_ControllerDelegate");
    }

}
