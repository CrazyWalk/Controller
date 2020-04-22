package cn.luyinbros.valleyframework.controller;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.luyinbros.valleyframework.controller.annotation.BindView;
import cn.luyinbros.valleyframework.controller.annotation.BuildView;
import cn.luyinbros.valleyframework.controller.annotation.BundleValue;
import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.valleyframework.controller.annotation.DidChangeLifecycleEvent;
import cn.luyinbros.valleyframework.controller.annotation.Dispose;
import cn.luyinbros.valleyframework.controller.annotation.InitState;
import cn.luyinbros.valleyframework.controller.annotation.OnActivityResult;
import cn.luyinbros.valleyframework.controller.annotation.OnPermissionResult;
import cn.luyinbros.valleyframework.controller.binding.ActivityResultBinding;
import cn.luyinbros.valleyframework.controller.binding.BuildViewBinding;
import cn.luyinbros.valleyframework.controller.binding.BundleValueBinding;
import cn.luyinbros.valleyframework.controller.binding.DisposeBinding;
import cn.luyinbros.valleyframework.controller.binding.InitStateBinding;
import cn.luyinbros.valleyframework.controller.binding.LifecycleBinding;
import cn.luyinbros.valleyframework.controller.binding.PermissionResultBinding;
import cn.luyinbros.valleyframework.controller.binding.ViewFieldBinding;
import cn.luyinbros.compiler.ElementHelper;


@SupportedSourceVersion(SourceVersion.RELEASE_8)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
@AutoService(Processor.class)
public class ControllerProcessor extends AbstractProcessor {
    private Filer mFilter;
    @Nullable
    private Trees trees;
    private RScanner rScanner = new RScanner();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mFilter = env.getFiler();
        try {
            trees = Trees.instance(processingEnv);
        } catch (IllegalArgumentException ignored) {
            try {
                // Get original ProcessingEnvironment from Gradle-wrapped one or KAPT-wrapped one.
                for (Field field : processingEnv.getClass().getDeclaredFields()) {
                    if (field.getName().equals("delegate") || field.getName().equals("processingEnv")) {
                        field.setAccessible(true);
                        ProcessingEnvironment javacEnv = (ProcessingEnvironment) field.get(processingEnv);
                        trees = Trees.instance(javacEnv);
                        break;
                    }
                }
            } catch (Throwable ignored2) {
            }
        }
        CompileMessager.setMessager(env.getMessager());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set,
                           RoundEnvironment env) {
        Map<TypeElement, ControllerDelegateSet> bindingMap = findAndParseTargets(env);
        for (Map.Entry<TypeElement, ControllerDelegateSet> entry : bindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            ControllerDelegateSet binding = entry.getValue();
            JavaFile javaFile = binding.brewJava();
            try {
                javaFile.writeTo(mFilter);
            } catch (IOException e) {
                CompileMessager.error(typeElement, "Unable to write binding for type %s: %s", typeElement, e.getMessage());
            }
        }


        return false;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> result = new HashSet<>();
        if (trees != null) {
            result.add(IncrementalAnnotationProcessorType.ISOLATING.getProcessorOption());
        }
        return result;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<Class<? extends Annotation>> annotationCls = new HashSet<>();
        annotationCls.add(Controller.class);
        annotationCls.add(InitState.class);
        annotationCls.add(BuildView.class);
        annotationCls.add(DidChangeLifecycleEvent.class);
        annotationCls.add(Dispose.class);
        annotationCls.add(OnActivityResult.class);
        annotationCls.add(OnPermissionResult.class);
        annotationCls.add(BindView.class);
        annotationCls.add(BundleValue.class);
        Set<String> result = new HashSet<>();
        for (Class<? extends Annotation> cls : annotationCls) {
            result.add(cls.getCanonicalName());
        }

        return result;
    }

    private Map<TypeElement, ControllerDelegateSet> findAndParseTargets(RoundEnvironment env) {

        final Map<TypeElement, ControllerDelegateSet.Builder> builderMap = new LinkedHashMap<>();

        final Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();
        for (Element element : env.getElementsAnnotatedWith(Controller.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseController(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                error(element, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(BindView.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseBindView(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                error(element, e);
            }
        }


        for (Element element : env.getElementsAnnotatedWith(BundleValue.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseBuildValue(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                error(element, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(InitState.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseInitState(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                error(element, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(BuildView.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseBuildView(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                error(element, e);
            }
        }


        for (Element element : env.getElementsAnnotatedWith(DidChangeLifecycleEvent.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseLifecycle(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                error(element, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(Dispose.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseDispose(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                error(element, e);
            }
        }
        for (Element element : env.getElementsAnnotatedWith(OnActivityResult.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseOnActivityResult(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                error(element, e);
            }
        }
        for (Element element : env.getElementsAnnotatedWith(OnPermissionResult.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseOnPermissionResult(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                error(element, e);
            }
        }


        final Map<TypeElement, ControllerDelegateSet> controllerDelegate = new LinkedHashMap<>();
        for (Map.Entry<TypeElement, ControllerDelegateSet.Builder> builderEntry : builderMap.entrySet()) {
            controllerDelegate.put(builderEntry.getKey(), builderEntry.getValue().build());
        }

        for (Map.Entry<TypeElement, ControllerDelegateSet> entry : controllerDelegate.entrySet()) {

            TypeElement superClass = getSuperClass(entry.getKey());
            while (superClass != null) {
                if (controllerDelegate.containsKey(superClass)) {
                    controllerDelegate.get(entry.getKey()).setParent(controllerDelegate.get(superClass));
                    break;
                }
                superClass = getSuperClass(superClass);
            }

        }


        return controllerDelegate;
    }


    private void parseController(Element element,
                                 Map<TypeElement, ControllerDelegateSet.Builder> builderMap,
                                 Set<TypeElement> erasedTargetNames) {
        if (Checker.isInvalidateController(element)) {
            return;
        }

        TypeElement typeElement = ElementHelper.asType(element);
        ControllerDelegateSet.Builder builder = builderMap.get(typeElement);
        if (builder == null) {
            builder = ControllerDelegateSet.newBuilder(typeElement,
                    elementToId(typeElement,
                            Controller.class,
                            typeElement.getAnnotation(Controller.class).value()),
                    this);
            builderMap.put(typeElement, builder);
        }

        erasedTargetNames.add(typeElement);
    }

    private void parseBindView(Element element,
                               Map<TypeElement, ControllerDelegateSet.Builder> builderMap,
                               Set<TypeElement> erasedTargetNames) {
        if (Checker.isInvalidateBindView(element)) {
            return;
        }


        ViewFieldBinding binding = ViewFieldBinding.create(element, elementToId(element,
                BindView.class,
                element.getAnnotation(BindView.class).value()));

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateSet.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            builder.addBinding(binding);
            erasedTargetNames.add(enclosingElement);
        }

    }

    private void parseBuildValue(Element element,
                                 Map<TypeElement, ControllerDelegateSet.Builder> builderMap,
                                 Set<TypeElement> erasedTargetNames) {
        if (Checker.isInvalidateBundleValue(element)) {
            return;
        }
        BundleValueBinding binding = BundleValueBinding.create(element);
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        final ControllerDelegateSet.Builder builder = builderMap.get(enclosingElement);
        if (builder != null) {
            builder.addBinding(binding);
            erasedTargetNames.add(enclosingElement);
        }
    }

    private void parseInitState(Element element,
                                Map<TypeElement, ControllerDelegateSet.Builder> builderMap,
                                Set<TypeElement> erasedTargetNames) {
        if (Checker.isInvalidateInitState(element)) {
            return;
        }
        InitStateBinding binding = InitStateBinding.create(element);
        if (binding != null) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            final ControllerDelegateSet.Builder builder = builderMap.get(enclosingElement);
            if (builder != null) {
                builder.addBinding(binding);
                erasedTargetNames.add(enclosingElement);
            }
        }
    }

    private void parseBuildView(Element element,
                                Map<TypeElement, ControllerDelegateSet.Builder> builderMap,
                                Set<TypeElement> erasedTargetNames) {
        if (Checker.isInvalidateBuildView(element)) {
            return;
        }
        BuildViewBinding binding = BuildViewBinding.create(element);
        if (binding != null) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            final ControllerDelegateSet.Builder builder = builderMap.get(enclosingElement);
            if (builder != null) {
                builder.addBinding(binding);
                erasedTargetNames.add(enclosingElement);
            }
        }
    }


    private void parseLifecycle(Element element,
                                Map<TypeElement, ControllerDelegateSet.Builder> builderMap,
                                Set<TypeElement> erasedTargetNames) {
        if (Checker.isInvalidateDidChangeLifecycleEvent(element)) {
            return;
        }
        LifecycleBinding binding = LifecycleBinding.create(element);
        if (binding != null) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            final ControllerDelegateSet.Builder builder = builderMap.get(enclosingElement);
            if (builder != null) {
                builder.addBinding(binding);
                erasedTargetNames.add(enclosingElement);
            }
        }
    }

    private void parseDispose(Element element,
                              Map<TypeElement, ControllerDelegateSet.Builder> builderMap,
                              Set<TypeElement> erasedTargetNames) {
        if (Checker.isInvalidateDispose(element)) {
            return;
        }
        DisposeBinding binding = DisposeBinding.create(element);
        if (binding != null) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            final ControllerDelegateSet.Builder builder = builderMap.get(enclosingElement);
            if (builder != null) {
                builder.addBinding(binding);
                erasedTargetNames.add(enclosingElement);
            }
        }
    }

    private void parseOnActivityResult(Element element,
                                       Map<TypeElement, ControllerDelegateSet.Builder> builderMap,
                                       Set<TypeElement> erasedTargetNames) {
        if (Checker.isInvalidateOnActivityResult(element)) {
            return;
        }
        ActivityResultBinding binding = ActivityResultBinding.create(element);
        if (binding != null) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            final ControllerDelegateSet.Builder builder = builderMap.get(enclosingElement);
            if (builder != null) {
                builder.addBinding(binding);
                erasedTargetNames.add(enclosingElement);
            }
        }
    }

    private void parseOnPermissionResult(Element element,
                                         Map<TypeElement, ControllerDelegateSet.Builder> builderMap,
                                         Set<TypeElement> erasedTargetNames) {
        if (Checker.isInvalidateOnPermissionResult(element)) {
            return;
        }
        PermissionResultBinding binding = PermissionResultBinding.create(element);
        if (binding != null) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            final ControllerDelegateSet.Builder builder = builderMap.get(enclosingElement);
            if (builder != null) {
                builder.addBinding(binding);
                erasedTargetNames.add(enclosingElement);
            }
        }
    }

    private ResId elementToId(Element element, Class<? extends Annotation> annotation, int value) {
        // CompileMessager.warn("elementToId " + getMirror(element, annotation).toString());
        JCTree tree = (JCTree) trees.getTree(element, getMirror(element, annotation));
        if (tree != null) { // tree can be null if the references are compiled types and not source
            rScanner.reset();
            tree.accept(rScanner);
            if (!rScanner.resourceIds.isEmpty()) {
                return rScanner.resourceIds.values().iterator().next();
            }
        }
        if (value == -1) {
            return null;
        }
        return new ResId(value);
    }

    public Map<Integer, ResId> elementToIds(Element element,
                                            AnnotationMirror mirror,
                                            int[] values) {
        //  CompileMessager.warn("elementToIds " + mirror.toString());
        Map<Integer, ResId> resourceIds = new LinkedHashMap<>();
        JCTree tree = (JCTree) trees.getTree(element, mirror);
        if (tree != null) { // tree can be null if the references are compiled types and not source
            rScanner.reset();
            tree.accept(rScanner);
            resourceIds = rScanner.resourceIds;
        }

        // Every value looked up should have an Id
        for (int value : values) {
            resourceIds.putIfAbsent(value, new ResId(value));
        }
        return resourceIds;
    }


    @Nullable
    private static AnnotationMirror getMirror(Element element,
                                              Class<? extends Annotation> annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
                return annotationMirror;
            }
        }
        return null;
    }

    private @Nullable TypeElement getSuperClass(TypeElement typeElement) {
        TypeMirror type = typeElement.getSuperclass();
        if (type.getKind() == TypeKind.NONE) {
            return null;
        }
        return (TypeElement) ((DeclaredType) type).asElement();
    }

    private static void error(Element element, Throwable e) {
//        StackTraceElement[] stackTraceElementArray = e.getStackTrace();
//        StringBuilder sb = new StringBuilder();
//        for (StackTraceElement stackTraceElement : stackTraceElementArray) {
//            sb.append(stackTraceElement).append("\n");
//        }
//        CompileMessager.warn(e + " " + sb.toString());
          CompileMessager.error(element, "invalidate " + e.getMessage());
    }

    private static class RScanner extends TreeScanner {
        Map<Integer, ResId> resourceIds = new LinkedHashMap<>();

        @Override
        public void visitIdent(JCTree.JCIdent jcIdent) {
            super.visitIdent(jcIdent);
            Symbol symbol = jcIdent.sym;
            //   CompileMessager.warn("visitIdent " + symbol.getClass());


            if (symbol.type instanceof Type.JCPrimitiveType) {
                //    CompileMessager.warn("visitIdent  JCPrimitiveType");
                ResId id = parseId(symbol);
                if (id != null) {
                    resourceIds.put(id.getId(), id);
                }
            }
        }

        @Override
        public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
            Symbol symbol = jcFieldAccess.sym;
            //   CompileMessager.warn("visitSelect");
            ResId id = parseId(symbol);
            if (id != null) {
                resourceIds.put(id.getId(), id);
            }
        }

        @Nullable
        private ResId parseId(Symbol symbol) {
            ResId id = null;
            //CompileMessager.warn(symbol.toString());
//            if ("cn.luyinbros.demo.controller.OnSingleClick".equals(symbol.toString())){
//                Symbol.ClassSymbol classSymbol= (Symbol.ClassSymbol)symbol;
//                CompileMessager.warn(classSymbol);
//            }
            //   CompileMessager.warn("parseId symbol  " + symbol + "  " + symbol.getClass());

            if (symbol.getEnclosingElement() != null
                    && symbol.getEnclosingElement().getEnclosingElement() != null
                    && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
                try {
                    int value = (Integer) Objects.requireNonNull(((Symbol.VarSymbol) symbol).getConstantValue());
                    id = new ResId(value, symbol);
                } catch (Throwable ignored) {
                    // error(null,ignored);
                    //CompileMessager.note(ignored.getMessage());
                }
            }
            return id;
        }

        @Override
        public void visitLiteral(JCTree.JCLiteral jcLiteral) {
            //   CompileMessager.warn("visitLiteral");
            try {
                int value = (Integer) jcLiteral.value;
                resourceIds.put(value, new ResId(value));
            } catch (Exception ignored) {
                error(null, ignored);
                //  CompileMessager.warn(ignored);
            }
        }

        @Override
        public void visitTypeArray(JCTree.JCArrayTypeTree jcArrayTypeTree) {
            super.visitTypeArray(jcArrayTypeTree);
            //  CompileMessager.warn("visitTypeArray");
        }

        @Override
        public void visitNewArray(JCTree.JCNewArray jcNewArray) {
            super.visitNewArray(jcNewArray);
//            JCTree.JCExpression expressionList = jcNewArray.elems.get(0);
//            CompileMessager.warn("visitNewArray  " + expressionList.getKind());

        }


        void reset() {
            resourceIds.clear();
        }

    }

}
