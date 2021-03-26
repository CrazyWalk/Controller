package cn.luyinbros.valleyframework.controller.provider;


import java.util.ArrayList;
import java.util.List;


import cn.luyinbros.valleyframework.controller.binding.InitStateBinding;


public class InitStateBindingProvider {
    private List<InitStateBinding> initStateBindings = new ArrayList<>();

    public void addBinding(InitStateBinding binding) {
        initStateBindings.add(binding);
    }

    public boolean isEmpty() {
        return initStateBindings.isEmpty();
    }

    public List<InitStateBinding> getInitStateBindings() {
        return initStateBindings;
    }

}
