package cn.luyinbros.valleyframework.controller.provider;


import java.util.ArrayList;
import java.util.List;

import cn.luyinbros.valleyframework.controller.annotation.InitViewModel;
import cn.luyinbros.valleyframework.controller.binding.InitStateBinding;
import cn.luyinbros.valleyframework.controller.binding.InitViewModelBinding;


public class InitViewModelProvider {
    private List<InitViewModelBinding> initViewModelBindings = new ArrayList<>();

    public void addBinding(InitViewModelBinding binding) {
        initViewModelBindings.add(binding);
    }

    public boolean isEmpty() {
        return initViewModelBindings.isEmpty();
    }

    public List<InitViewModelBinding> getInitViewModelBindings() {
        return initViewModelBindings;
    }
}
