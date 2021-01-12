package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.ClassName;

import java.util.List;

import cn.luyinbros.valleyframework.controller.FullTypeName;

/**
 * if has returnClassName .  paramClassNames must has one buildContext;
 */
public class BuildViewBinding {
    private final String methodName;
    private final List<FullTypeName> paramClassNames;

    private final ClassName returnClassName;

    public BuildViewBinding(String methodName, List<FullTypeName> paramClassNames, ClassName returnClassName) {
        this.methodName = methodName;
        this.paramClassNames = paramClassNames;
        this.returnClassName = returnClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<FullTypeName> getParamClassNames() {
        return paramClassNames;
    }

    public boolean hasParam() {
        return paramClassNames != null && !paramClassNames.isEmpty();
    }

    public ClassName getReturnClassName() {
        return returnClassName;
    }

//    public static BuildViewBinding create(Element element) {
//        final ExecutableElement executableElement = ElementHelper.asExecutable(element);
//        final TypeMirror returnType = executableElement.getReturnType();
//
//        if (!(returnType.getKind() == TypeKind.VOID || returnType.getKind() == TypeKind.DECLARED)) {
//            CompileMessager.error(element, "invalidate");
//            return null;
//        }
//
//
//        ClassName returnClassName = null;
//        List<FullTypeName> argumentClassNames = null;
//
//        boolean hasError = true;
//        if (returnType.getKind() == TypeKind.VOID) {
//            final List<? extends VariableElement> methodParameters = executableElement.getParameters();
//            if (methodParameters.size() <= 2) {
//                argumentClassNames = new ArrayList<>();
//
//                boolean foundBuildContext = false;
//                boolean foundView = false;
//                hasError = false;
//                for (VariableElement variableElement : methodParameters) {
//                    TypeMirror mirror = variableElement.asType();
//                    if (TypeHelper.isTypeEqual(mirror, Constants.TYPE_BUILD_CONTEXT)) {
//                        if (foundBuildContext) {
//                            hasError = true;
//                            break;
//                        }
//                        argumentClassNames.add(new FullTypeName(TypeNameHelper.get(mirror), mirror));
//                        foundBuildContext = true;
//
//                    } else if (TypeHelper.isSubtypeOfType(mirror, Constants.TYPE_VIEW)) {
//                        if (foundView) {
//                            hasError = true;
//                            break;
//                        }
//                        argumentClassNames.add(new FullTypeName(TypeNameHelper.get(mirror), mirror));
//                        foundView = true;
//                    } else {
//                        hasError = true;
//                        break;
//                    }
//                }
//            }
//        } else if (returnType.getKind() == TypeKind.DECLARED) {
//            if (TypeHelper.isSubtypeOfType(returnType, Constants.TYPE_VIEW)) {
//                returnClassName = TypeNameHelper.get(returnType);
//                final List<? extends VariableElement> methodParameters = executableElement.getParameters();
//                if (methodParameters.size() == 0) {
//                    argumentClassNames = new ArrayList<>();
//                    hasError = false;
//                } else if (methodParameters.size() == 1) {
//                    VariableElement variableElement = methodParameters.get(0);
//                    TypeMirror mirror = variableElement.asType();
//                    if (TypeHelper.isTypeEqual(mirror, Constants.TYPE_BUILD_CONTEXT)) {
//                        argumentClassNames = ImmutableList.of(new FullTypeName(TypeNameHelper.get(mirror), mirror));
//                        hasError = false;
//                    }
//                }
//            }
//        }
//
//        if (hasError) {
//            CompileMessager.error(element, "invalidate");
//            return null;
//        }
//
//
//        final String methodName = executableElement.getSimpleName().toString();
//        return new BuildViewBinding(methodName,
//                argumentClassNames, returnClassName);
//    }

    @Override
    public String toString() {
        return "BuildViewBinding{" +
                "methodName='" + methodName + '\'' +
                ", paramClassNames=" + paramClassNames +
                ", returnClassName=" + returnClassName +
                '}';
    }
}
