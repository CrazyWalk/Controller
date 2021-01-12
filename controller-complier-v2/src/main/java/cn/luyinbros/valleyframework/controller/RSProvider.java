package cn.luyinbros.valleyframework.controller;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class RSProvider {
    private final Trees trees;
    private final RScanner rScanner;

    public RSProvider(Trees trees,
                      CompilerMessager messager) {
        this.trees = trees;
        this.rScanner = new RScanner(messager);
    }

    public ResId elementToId(Element element, Class<? extends Annotation> annotation, int value) {
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


    private static class RScanner extends TreeScanner {
        private Map<Integer, ResId> resourceIds = new LinkedHashMap<>();
        private final CompilerMessager messager;

        private RScanner(CompilerMessager messager) {
            this.messager = messager;
        }

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
                    //ignore
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
                //  error(null, ignored);
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
