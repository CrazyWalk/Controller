package cn.luyinbros.valleyframework.controller.provider;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;


import cn.luyinbros.valleyframework.controller.Constants;
import cn.luyinbros.valleyframework.controller.ControllerProcessor;
import cn.luyinbros.valleyframework.controller.ElementHelper;
import cn.luyinbros.valleyframework.controller.RSProvider;
import cn.luyinbros.valleyframework.controller.ResId;
import cn.luyinbros.valleyframework.controller.TypeHelper;
import cn.luyinbros.valleyframework.controller.Utils;
import cn.luyinbros.valleyframework.controller.annotation.ListenerMethod;
import cn.luyinbros.valleyframework.controller.binding.ListenerBinding;
import cn.luyinbros.valleyframework.controller.listener.ListenerClassInfo;
import cn.luyinbros.valleyframework.controller.listener.ListenerMethodInfo;

public class ListenerBindingProvider {
    private List<ListenerBinding> listenerBindings = new ArrayList<>();


    public List<ListenerBinding> getListenerBindings() {
        return listenerBindings;
    }


    public static ListenerBindingProvider of(TypeElement controllerElement, RSProvider rsProvider) {
        ListenerBindingProvider provider = new ListenerBindingProvider();
        List<? extends Element> controllerEnclosedElement = controllerElement.getEnclosedElements();
        // CompileMessager.note(controllerEnclosedElement.toString());
        Map<ExecutableElement, AnnotationMirror> declareListenerMap = new LinkedHashMap<>();
        Map<ExecutableElement, AnnotationMirror> sourceListenerMap = new LinkedHashMap<>();

        for (Element enclosedElement : controllerEnclosedElement) {
            if (enclosedElement.getKind() == ElementKind.METHOD) {
                ExecutableElement controllerExecutableElement = ElementHelper.asExecutable(enclosedElement);
                List<? extends AnnotationMirror> controllerExecutableAnnotationMirrors = controllerExecutableElement.getAnnotationMirrors();
                for (AnnotationMirror annotationMirror : controllerExecutableAnnotationMirrors) {
                    TypeElement executableAnnotationElement = ElementHelper.asType(annotationMirror.getAnnotationType().asElement());
                    List<? extends AnnotationMirror> maybeListenerAnnotationMirrors = executableAnnotationElement.getAnnotationMirrors();
                    for (AnnotationMirror maybeListenerAnnotationMirror : maybeListenerAnnotationMirrors) {
                        if (TypeHelper.isTypeEqual(maybeListenerAnnotationMirror.getAnnotationType(), Constants.TYPE_LISTENER_CLASS_ANNOTATION)) {
                            declareListenerMap.put(controllerExecutableElement, annotationMirror);
                            sourceListenerMap.put(controllerExecutableElement, maybeListenerAnnotationMirror);
                            break;
                        }
                    }
                }
            }
        }

        for (Map.Entry<ExecutableElement, AnnotationMirror> listenerEntry : declareListenerMap.entrySet()) {
            ExecutableElement executableElement = listenerEntry.getKey();
            ListenerBinding listenerBinding = new ListenerBinding();
            listenerBinding.setMethodName(executableElement.getSimpleName().toString());
            listenerBinding.setHasArguments(executableElement.getParameters().size() != 0);

            {
                List<TypeMirror> typeMirrors = new ArrayList<>();
                List<? extends VariableElement> variableElements = executableElement.getParameters();

                for (VariableElement variableElement : variableElements) {
                    typeMirrors.add(variableElement.asType());
                }
                listenerBinding.setArgumentTypeMirrors(typeMirrors);

            }

            listenerBinding.setReturnTypeMirror(executableElement.getReturnType());
            listenerBinding.setRequired(!Utils.isOptionElement(executableElement));

            AnnotationMirror declareListenerAnnotationMirror = listenerEntry.getValue();
            AnnotationMirror sourceListenerAnnotationMirror = sourceListenerMap.get(listenerEntry.getKey());

            listenerBinding.setIds(findResId(declareListenerAnnotationMirror,
                    listenerEntry.getKey(),
                    declareListenerAnnotationMirror.getElementValues(),
                    rsProvider));
            listenerBinding.setListenerClassInfo(generationListenerClassInfo(sourceListenerAnnotationMirror));
            listenerBinding.setListenerMethodInfo(findCallback(declareListenerAnnotationMirror.getElementValues(), listenerBinding));
            listenerBinding.getListenerClassInfo().adjust();
            provider.listenerBindings.add(listenerBinding);
        }

        return provider;
    }


    private static List<ResId> findResId(AnnotationMirror annotationMirror,
                                         Element controllerMethodElement,
                                         Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues
            , RSProvider rsProvider) {
        List<ResId> resIds = new ArrayList<>();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
            ExecutableElement executableElement = entry.getKey();
            if (executableElement.getSimpleName().toString().equals("value")) {
                if (TypeHelper.isArray(executableElement.getReturnType(), TypeKind.INT)) {
                    List<? extends AnnotationValue> annotationValues = (List<? extends AnnotationValue>) entry.getValue().getValue();
                    int[] ids = new int[annotationValues.size()];

                    for (int index = 0; index < annotationValues.size(); index++) {
                        ids[index] = (Integer) annotationValues.get(index).getValue();
                    }
                    Map<Integer, ResId> resIdMap = rsProvider.elementToIds(controllerMethodElement, annotationMirror, ids);
                    for (Map.Entry<Integer, ResId> resIdEntry : resIdMap.entrySet()) {
                        resIds.add(resIdEntry.getValue());
                    }
                }
            }
        }
        return resIds;
    }


    private static ListenerMethodInfo findCallback(Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues,
                                                   ListenerBinding listenerBinding) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
            ExecutableElement executableElement = entry.getKey();
            if (executableElement.getSimpleName().toString().equals("callBack")) {
                AnnotationValue annotationValue = entry.getValue();
                return listenerBinding.getListenerClassInfo().getMethodInfoMap().get(
                        ((VariableElement) annotationValue.getValue()).getSimpleName().toString());

            }
        }

        List<ListenerMethodInfo> methodInfoList = listenerBinding.getListenerClassInfo().getMethodInfoList();
        if (methodInfoList.isEmpty()) {
            return null;
        }
        return listenerBinding.getListenerClassInfo().getMethodInfoList().get(0);
    }

    private static ListenerClassInfo generationListenerClassInfo(AnnotationMirror listenerClassInfoAnnotationMirror) {
        ListenerClassInfo listenerClassInfo = new ListenerClassInfo();

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : listenerClassInfoAnnotationMirror.getElementValues().entrySet()) {
            ExecutableElement executableElement = entry.getKey();
            String methodName = executableElement.getSimpleName().toString();
            if ("targetType".equals(methodName)) {
                listenerClassInfo.setTargetType((String) entry.getValue().getValue());
            } else if ("setter".equals(methodName)) {
                listenerClassInfo.setSetter((String) entry.getValue().getValue());
            } else if ("remover".equals(methodName)) {
                listenerClassInfo.setRemover((String) entry.getValue().getValue());
            } else if ("type".equals(methodName)) {
                listenerClassInfo.setType((String) entry.getValue().getValue());
            } else if ("callbacks".equals(methodName)) {
                listenerClassInfo.setMethodInfoMap(generationCallBackMap(entry.getValue()));
            } else if ("method".equals(methodName)) {
                listenerClassInfo.setMethodInfoList(generationMethodListenerInfoList(entry.getValue()));
            }
        }


        return listenerClassInfo;
    }

    private static Map<String, ListenerMethodInfo> generationCallBackMap(AnnotationValue annotationValue) {
        Map<String, ListenerMethodInfo> map = new LinkedHashMap<>();
        TypeElement typeElement = ElementHelper.asType(TypeHelper.asDeclared((TypeMirror) annotationValue.getValue()).asElement());
        if (typeElement.getKind() == ElementKind.ENUM) {
            List<? extends Element> elements = typeElement.getEnclosedElements();
            for (Element element : elements) {
                if (element.getKind() == ElementKind.ENUM_CONSTANT) {
                    //TypeElement enumConstantElement = ElementHelper.asType(element);
                    ListenerMethod listenerMethod = element.getAnnotation(ListenerMethod.class);
                    if (listenerMethod != null) {
                        ListenerMethodInfo listenerMethodInfo = new ListenerMethodInfo();
                        listenerMethodInfo.setName(listenerMethod.name());
                        listenerMethodInfo.setDefaultReturn(listenerMethod.defaultReturn());
                        listenerMethodInfo.setParameters(listenerMethod.parameters());
                        listenerMethodInfo.setReturnType(listenerMethod.returnType());
                        map.put(element.getSimpleName().toString(), listenerMethodInfo);
                    }
                    //CompileMessager.note(listenerMethod+"");
                }
            }

        }
        // CompileMessager.note(typeElement.toString());

        return map;
    }

    private static List<ListenerMethodInfo> generationMethodListenerInfoList(AnnotationValue annotationValue) {
        List<ListenerMethodInfo> listenerMethodInfoList = new ArrayList<>();
        List<? extends AnnotationValue> methodInfoAnnotationValueList = (List<? extends AnnotationValue>) annotationValue.getValue();
        for (AnnotationValue methodInfoAnnotationValue : methodInfoAnnotationValueList) {
            listenerMethodInfoList.add(generationMethodListenerInfo((AnnotationMirror) methodInfoAnnotationValue));
        }
        return listenerMethodInfoList;
    }

    private static ListenerMethodInfo generationMethodListenerInfo(AnnotationMirror annotationValue) {
        ListenerMethodInfo methodInfo = new ListenerMethodInfo();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationValue.getElementValues().entrySet()) {
            ExecutableElement executableElement = entry.getKey();
            String methodName = executableElement.getSimpleName().toString();
            if ("name".equals(methodName)) {
                methodInfo.setName((String) entry.getValue().getValue());
            } else if ("parameters".equals(methodName)) {
                List<? extends AnnotationValue> parameterAnnotationValues = (List<? extends AnnotationValue>) entry.getValue().getValue();
                String[] parameter = new String[parameterAnnotationValues.size()];
                for (int index = 0; index < parameterAnnotationValues.size(); index++) {
                    parameter[index] = (String) parameterAnnotationValues.get(index).getValue();
                }
                methodInfo.setParameters(parameter);
            } else if ("returnType".equals(methodName)) {
                methodInfo.setReturnType((String) entry.getValue().getValue());
            } else if ("defaultReturn".equals(methodName)) {
                methodInfo.setDefaultReturn((String) entry.getValue().getValue());
            }
        }

        return methodInfo;
    }


    private static String getAnnotationValueType(AnnotationValue annotationValue) {
        Object o = annotationValue.getValue();
        if (o instanceof Number) {
            return "number";
        } else if (o instanceof String) {
            return "string";
        } else if (o instanceof TypeMirror) {
            return "typeMirror";
        } else if (o instanceof VariableElement) {
            return "VariableElement";
        } else if (o instanceof AnnotationMirror) {
            return "AnnotationMirror";
        } else {
            return "List<? extends AnnotationValue>";
        }
    }
}
