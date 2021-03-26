package cn.luyinbros.valleyframework.controller.provider;

import java.util.ArrayList;
import java.util.List;

import cn.luyinbros.valleyframework.controller.binding.LiveOBBinding;

public class LiveOBBindingProvider {
    private List<LiveOBBinding> bindings = new ArrayList<>();


    public void add(LiveOBBinding binding) {
        bindings.add(binding);
    }

    public boolean isEmpty() {
        return bindings.isEmpty();
    }


    public List<LiveOBBinding> getBindings() {
        return bindings;
    }

    @Override
    public String toString() {
        return "LiveOBBindingProvider{" +
                "bindings=" + bindings +
                '}';
    }
}
