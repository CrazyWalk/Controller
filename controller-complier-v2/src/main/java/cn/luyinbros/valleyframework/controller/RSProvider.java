package cn.luyinbros.valleyframework.controller;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class RSProvider {
    private final Trees trees;
    private final RScanner rScanner;

    public RSProvider(Trees trees,
                      Messager messager) {
        this.trees = trees;
        this.rScanner = new RScanner(messager);
    }


    private static class RScanner extends TreeScanner {
        private Map<Integer, ResId> resourceIds = new LinkedHashMap<>();
        private final Messager messager;

        private RScanner(Messager messager) {
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
                    messager.printMessage(Diagnostic.Kind.ERROR, ignored.getMessage());
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
