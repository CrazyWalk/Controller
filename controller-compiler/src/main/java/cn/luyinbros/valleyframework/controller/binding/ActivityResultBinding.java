package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.CompileMessager;
import cn.luyinbros.valleyframework.controller.Constants;
import cn.luyinbros.valleyframework.controller.Utils;
import cn.luyinbros.valleyframework.controller.annotation.OnActivityResult;
import cn.luyinbros.compiler.ElementHelper;
import cn.luyinbros.compiler.TypeHelper;
import cn.luyinbros.compiler.TypeNameHelper;


public class ActivityResultBinding {
    private int requestCode;
    private int resultCode;
    private boolean requiredNonNullIntent;
    private List<TypeName> params;
    private String methodName;
    private boolean after;
    public static final int ALLOW_EXCLUDE_MIN_REQUEST_CODE = 1;
    public static final int ALLOW_EXCLUDE_MIN_RESULT_CODE = -1;


    public int getRequestCode() {
        return requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public boolean isRequiredNonNullIntent() {
        return requiredNonNullIntent;
    }

    public List<TypeName> getParams() {
        return params;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isAfter() {
        return after;
    }


    public static ActivityResultBinding create(Element element) {
        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        final TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            CompileMessager.error(element, "invalidate");
            return null;
        }

        boolean hasError = true;


        final List<? extends VariableElement> parameters = executableElement.getParameters();
        final int parametersSize = parameters.size();
        OnActivityResult onActivityResult = executableElement.getAnnotation(OnActivityResult.class);

        final int requestCode = onActivityResult.value();
        final int resultCode = onActivityResult.resultCode();

        ActivityResultBinding binding = new ActivityResultBinding();
        binding.requestCode = requestCode;
        binding.resultCode = resultCode;
        binding.requiredNonNullIntent = true;
        binding.params = new ArrayList<>();
        binding.methodName = executableElement.getSimpleName().toString();
        binding.after = onActivityResult.after();

        if (parametersSize == 0) {
            hasError = false;
            binding.requiredNonNullIntent = false;
        } else if (parametersSize == 1) {
            VariableElement variableElement = parameters.get(0);
            TypeMirror typeMirror = variableElement.asType();
            if (typeMirror.getKind() == TypeKind.INT) {
                binding.params.add(TypeName.INT);
                binding.requiredNonNullIntent = false;
                hasError = false;
            } else if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_INTENT)) {
                binding.params.add(Constants.CLASS_INTENT);
                binding.requiredNonNullIntent = Utils.isNotNullElement(variableElement);
                hasError = false;
            }
        } else if (parametersSize == 2) {
            if (Utils.isMatchTwoNoOrderParameters(parameters, TypeKind.INT, Constants.TYPE_INTENT)) {
                for (VariableElement variableElement : parameters) {
                    if (TypeHelper.isTypeEqual(variableElement.asType(), Constants.TYPE_INTENT)){
                        binding.requiredNonNullIntent = Utils.isNotNullElement(variableElement);
                    }
                    binding.params.add(TypeName.get(variableElement.asType()));
                }
                hasError = false;
            }
        } else if (parametersSize == 3) {
            if (Utils.isStrictTypeParameters(parameters,
                    TypeKind.INT,
                    TypeKind.INT,
                    Constants.TYPE_INTENT)) {
                for (VariableElement variableElement : parameters) {
                    binding.params.add(TypeName.get(variableElement.asType()));
                }
                binding.requiredNonNullIntent = Utils.isNotNullElement(parameters.get(2));
                hasError = false;
            }
        }

        if (hasError) {
            CompileMessager.error(element, "invalidate");
            return null;
        }
        return binding;

    }


    private static boolean handleTwoParameter(List<? extends VariableElement> parameters, ActivityResultBinding binding) {
        int parametersSize = parameters.size();

        if (parametersSize == 1) {
            TypeMirror typeMirror = parameters.get(0).asType();
            if (typeMirror.getKind() == TypeKind.INT) {
                binding.params.add(TypeName.INT);
                binding.requiredNonNullIntent = false;
                return true;
            } else if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_INTENT)) {
                binding.requiredNonNullIntent = Utils.isNotNullElement(parameters.get(0));
                binding.params.add(TypeNameHelper.get(typeMirror));
                return true;
            }
        } else if (parametersSize == 2) {
            boolean isSuccess = true;
            boolean foundInt = false;
            boolean foundIntent = false;
            for (VariableElement variableElement : parameters) {
                TypeMirror typeMirror = variableElement.asType();
                if (typeMirror.getKind() == TypeKind.INT) {
                    if (foundInt) {
                        isSuccess = false;
                        break;
                    }
                    binding.params.add(TypeName.INT);
                    foundInt = true;
                } else if (TypeHelper.isTypeEqual(typeMirror, Constants.TYPE_INTENT)) {
                    if (foundIntent) {
                        isSuccess = false;
                        break;
                    }
                    binding.params.add(TypeNameHelper.get(typeMirror));
                    binding.requiredNonNullIntent = Utils.isNotNullElement(variableElement);
                    foundIntent = true;
                } else {
                    isSuccess = false;
                }
            }
            return isSuccess;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ActivityResultBinding{" +
                "requestCode=" + requestCode +
                ", resultCode=" + resultCode +
                ", requiredNonNullIntent=" + requiredNonNullIntent +
                ", params=" + params +
                ", methodName='" + methodName + '\'' +
                ", after=" + after +
                '}';
    }
}
