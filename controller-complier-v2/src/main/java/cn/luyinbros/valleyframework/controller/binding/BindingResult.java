package cn.luyinbros.valleyframework.controller.binding;

import javax.lang.model.element.Element;

import afu.org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 绑定结果
 *
 * @param <T> 对应绑定结果
 */
public class BindingResult<T> {
    private final T binding;
    private final boolean isError;
    private final boolean isWarn;
    private final String message;
    private final Element element;

    public BindingResult(T binding) {
        this.binding = binding;
        isError = false;
        isWarn = false;
        message = "";
        element = null;
    }

    private BindingResult(boolean isError, boolean isWarn, String message, Element element) {
        this.binding = null;
        this.isError = isError;
        this.isWarn = isWarn;
        this.message = message;
        this.element = element;
    }

    private BindingResult(T binding, boolean isError, boolean isWarn, String message, Element element) {
        this.binding = binding;
        this.isError = isError;
        this.isWarn = isWarn;
        this.message = message;
        this.element = element;
    }

    public T getBinding() {
        return binding;
    }

    public String getMessage() {
        return message;
    }

    @Nullable
    public Element getElement() {
        return element;
    }

    public boolean isError() {
        return isError;
    }

    public boolean isWarn() {
        return isWarn;
    }

    public static <T> BindingResult<T> createBindResult(T binding) {
        return new BindingResult<>(binding);
    }

    public static <T> BindingResult<T> createWarnResult(@Nullable Element element, String message) {
        return new BindingResult<T>(false, true, message, element);
    }


    public static <T> BindingResult<T> createErrorResult(@Nullable Element element, String message) {
        return new BindingResult<T>(true, false, message, element);
    }

    @Override
    public String toString() {
        return "BindingResult{" +
                "binding=" + binding +
                ", isError=" + isError +
                ", isWarn=" + isWarn +
                ", message='" + message + '\'' +
                ", element=" + element +
                '}';
    }
}

