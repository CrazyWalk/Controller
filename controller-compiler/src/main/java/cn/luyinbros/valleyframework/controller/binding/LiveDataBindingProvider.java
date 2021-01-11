package cn.luyinbros.valleyframework.controller.binding;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.compiler.ElementHelper;
import cn.luyinbros.compiler.TypeHelper;
import cn.luyinbros.valleyframework.controller.Checker;
import cn.luyinbros.valleyframework.controller.CompileMessager;
import cn.luyinbros.valleyframework.controller.Constants;
import cn.luyinbros.valleyframework.controller.ControllerDelegateInfo;
import cn.luyinbros.valleyframework.controller.Utils;


public class LiveDataBindingProvider {
    private List<LiveDataBinding> bindings = new ArrayList<>();
    private Map<String, ViewModelBinding> modelBindingMap = new HashMap<>();


    public void addBinding(LiveDataBinding binding) {
        bindings.add(binding);
    }

    public void code(TypeSpec.Builder result) {
        if (!bindings.isEmpty()) {
            CompileMessager.note("准备解析:" + bindings);
        }
    }

    public static LiveDataBindingProvider of(TypeElement rootElement) {
        final List<? extends Element> elements = rootElement.getEnclosedElements();
        final LiveDataBindingProvider liveDataBindingProvider = new LiveDataBindingProvider();
        //CompileMessager.note(elements + "");
        for (Element element : elements) {
            switch (element.getKind()) {
                case FIELD:
                    VariableElement variableElement = ElementHelper.asVariable(element);
                    if (TypeHelper.isSubtypeOfType(variableElement.asType(), Constants.TYPE_VIEW_MODEL)) {
                        ViewModelBinding viewModelBinding = liveDataBindingProvider.modelBindingMap.get(variableElement.getSimpleName().toString());
                        CompileMessager.note("findViewModel:" + variableElement.asType());
                    }
                    //   if (TypeHelper.isSubtypeOfType(element,Constants.TYPE_VIEW_MODEL))
                    break;
            }
        }

        return new LiveDataBindingProvider();
    }

    @Override
    public String toString() {
        return "LiveDataBindingProvider{" +
                "bindings=" + bindings +
                '}';
    }
}
