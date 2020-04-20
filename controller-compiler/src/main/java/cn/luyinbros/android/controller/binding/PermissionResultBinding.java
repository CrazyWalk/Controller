package cn.luyinbros.android.controller.binding;

import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.android.controller.CompileMessager;
import cn.luyinbros.android.controller.FullTypeName;
import cn.luyinbros.android.controller.Utils;
import cn.luyinbros.android.controller.annotation.OnPermissionResult;
import cn.luyinbros.compiler.ElementHelper;
import cn.luyinbros.compiler.TypeHelper;

public class PermissionResultBinding {
    private String methodName;
    private String[] permissions;
    private int requestCode;
    private List<FullTypeName> params;
    private boolean after;
    public static final int ALLOW_EXCLUDE_MIN_REQUEST_CODE = 1;

    public String getMethodName() {
        return methodName;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public List<FullTypeName> getParams() {
        return params;
    }

    public boolean isAfter() {
        return after;
    }


    public static PermissionResultBinding create(Element element) {
        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
        final TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            CompileMessager.error(element, "invalidate");
            return null;
        }

        boolean hasError = true;

        final List<? extends VariableElement> parameters = executableElement.getParameters();
        final int parametersSize = parameters.size();
        OnPermissionResult onPermissionResult = executableElement.getAnnotation(OnPermissionResult.class);
        PermissionResultBinding binding = new PermissionResultBinding();
        binding.requestCode = onPermissionResult.value();
        binding.params = new ArrayList<>();
        binding.methodName = executableElement.getSimpleName().toString();
        binding.after = onPermissionResult.after();
        binding.permissions = onPermissionResult.permissions();

        if (parametersSize == 0) {
            hasError = false;
        } else if (parametersSize == 1) {
            VariableElement variableElement = parameters.get(0);
            TypeMirror typeMirror = variableElement.asType();
            if (typeMirror.getKind() == TypeKind.INT) {
                hasError = false;
                binding.params.add(new FullTypeName(TypeName.INT, typeMirror));
            } else if (TypeHelper.isArray(typeMirror, TypeKind.INT)) {
                hasError = false;
                binding.params.add(new FullTypeName(TypeName.get(typeMirror), typeMirror));
            }
        } else if (parametersSize == 3) {
            if (Utils.isStrictTypeParameters(parameters, TypeKind.INT, "java.lang.String[]", TypeKind.INT) ||
                    Utils.isStrictTypeParameters(parameters, TypeKind.INT, "java.lang.String[]", "int[]")) {
                hasError = false;
                for (VariableElement variableElement : parameters) {
                    TypeMirror typeMirror = variableElement.asType();
                    binding.params.add(new FullTypeName(TypeName.get(typeMirror), typeMirror));
                }
            }
        }
        if (hasError) {
            //CompileMessager.error(element, "invalidate");
            return null;
        }

        return binding;


    }
}
