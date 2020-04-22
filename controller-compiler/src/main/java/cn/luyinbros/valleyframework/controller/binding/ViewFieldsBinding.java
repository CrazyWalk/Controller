package cn.luyinbros.valleyframework.controller.binding;

import java.util.List;

public class ViewFieldsBinding {
    private List<ViewFieldBinding> viewFieldBindings;


    public static ViewFieldsBinding create(List<ViewFieldBinding> viewFieldBindings) {
        ViewFieldsBinding binding = new ViewFieldsBinding();
        binding.viewFieldBindings = viewFieldBindings;
        return binding;
    }


}
