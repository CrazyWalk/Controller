package cn.luyinbros.valleyframework.controller.provider;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import cn.luyinbros.valleyframework.controller.binding.DisposeBinding;

import static javax.lang.model.element.Modifier.PROTECTED;

public class DisposeBindingProvider {
    private List<DisposeBinding> disposeBindings;

    public void addBinding(DisposeBinding binding) {
        if (disposeBindings == null) {
            disposeBindings = new ArrayList<>();
        }
        disposeBindings.add(binding);
    }

    public boolean isEmpty() {
        return disposeBindings == null || disposeBindings.isEmpty();
    }

    public List<DisposeBinding> getDisposeBindings() {
        return disposeBindings;
    }

    @Override
    public String toString() {
        return "DisposeBindingProvider{" +
                "disposeBindings=" + disposeBindings +
                '}';
    }
}
