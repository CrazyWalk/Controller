package cn.luyinbros.valleyframework.controller.provider;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;

import cn.luyinbros.valleyframework.controller.CompilerMessager;

public class ViewModelProvider {
    private Map<String, String> viewModelProvider = new HashMap<>();
    private Map<String, String> liveDataProvider = new HashMap<>();


    public void scanViewModel(CompilerMessager messager, Element element) {
        messager.noteMessage(element.getEnclosedElements() + "");
    }

    public void ensureLiveData() {

    }
}
