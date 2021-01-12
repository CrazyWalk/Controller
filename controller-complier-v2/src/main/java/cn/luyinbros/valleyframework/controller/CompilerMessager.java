package cn.luyinbros.valleyframework.controller;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class CompilerMessager {
    private final Messager messager;

    public CompilerMessager(Messager messager) {
        this.messager = messager;
    }

    public void errorElement(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    public void errorElement(Element element, Throwable e) {
        messager.printMessage(Diagnostic.Kind.ERROR, "invalidate " + e.getMessage(), element);
    }

    public void errorElement(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    public void warnElement(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.WARNING, message, element);
    }

    public void warnMessage(String message) {
        messager.printMessage(Diagnostic.Kind.WARNING, message);
    }

    public void errorMessage(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    public void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(kind, message, element);

    }
}
