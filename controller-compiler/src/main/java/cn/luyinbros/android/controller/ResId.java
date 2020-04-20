package cn.luyinbros.android.controller;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.sun.tools.javac.code.Symbol;

import javax.annotation.Nullable;


public class ResId {
    private final int id;
    private static final ClassName ANDROID_R = ClassName.get("android", "R");
    final CodeBlock code;
    final boolean qualifed;
    private static final String R = "R";

    public ResId(int id) {
        this(id, null);
    }

    public ResId(int value, @Nullable Symbol rSymbol) {
        this.id = value;
        if (rSymbol != null) {
            ClassName className = ClassName.get(rSymbol.packge().getQualifiedName().toString(), R,
                    rSymbol.enclClass().name.toString());
            String resourceName = rSymbol.name.toString();

            this.code = className.topLevelClassName().equals(ANDROID_R)
                    ? CodeBlock.of("$L.$N", className, resourceName)
                    : CodeBlock.of("$T.$N", className, resourceName);
            this.qualifed = true;
        } else {
            this.code = CodeBlock.of("$L", value);
            this.qualifed = false;
        }
       // CompileMessager.note(code.toString());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ResId && id == ((ResId) o).id;
    }

    public CodeBlock getCode() {
        return code;
    }


    @Override
    public String toString() {
        return "ResId{" +
                "id=" + id +
                ", code=" + code +
                ", qualifed=" + qualifed +
                '}';
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }
}
