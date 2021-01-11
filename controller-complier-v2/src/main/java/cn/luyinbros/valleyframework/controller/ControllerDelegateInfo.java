package cn.luyinbros.valleyframework.controller;

import cn.luyinbros.valleyframework.controller.binding.ControllerBinding;

class ControllerDelegateInfo {
    private final ControllerBinding controllerBinding;

    private ControllerDelegateInfo(ControllerBinding controllerBinding) {
        this.controllerBinding = controllerBinding;
    }

    static class Builder {
        private final ControllerBinding controllerBinding;

        public Builder(ControllerBinding controllerBinding) {
            this.controllerBinding = controllerBinding;
        }


        public ControllerDelegateInfo build() {
            return new ControllerDelegateInfo(controllerBinding);
        }
    }

}
